package com.dorotatomczak.warehouseapp.ui.products

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.local.entity.Operation
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.data.model.SyncResult
import com.dorotatomczak.warehouseapp.data.model.Warehouse
import com.dorotatomczak.warehouseapp.data.queue.OfflineRequest
import com.dorotatomczak.warehouseapp.data.queue.OfflineRequestQueue
import com.dorotatomczak.warehouseapp.data.remote.api.Result
import com.dorotatomczak.warehouseapp.data.repository.ProductsRepository
import com.dorotatomczak.warehouseapp.data.repository.WarehousesRepository
import com.dorotatomczak.warehouseapp.ui.util.AppSettings
import com.dorotatomczak.warehouseapp.ui.util.Event
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProductsViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository,
    private val warehousesRepository: WarehousesRepository,
    private val appSettings: AppSettings,
    private val googleSignInClient: GoogleSignInClient,
    private val queue: OfflineRequestQueue
) : ViewModel() {

    val products = MutableLiveData<ArrayList<Product>>(ArrayList())

    val isLoading = MutableLiveData<Boolean>(false)

    val navigateToAddProduct = MutableLiveData<Event<String>>()
    val navigateToEditProduct = MutableLiveData<Event<Product>>()
    val navigateToSignIn = MutableLiveData<Event<String>>()
    val showMessage = MutableLiveData<Event<Int>>()
    val showStringMessage = MutableLiveData<Event<String>>()
    val onProductDeleted = MutableLiveData<Event<Int>>()
    val onProductDeleteFailure = MutableLiveData<Event<Int>>()
    val onProductQuantityChanged = MutableLiveData<Event<Int>>()
    val showSyncResults = MutableLiveData<Event<ArrayList<SyncResult>>>()
    val showWarehousesSelectDialog = MutableLiveData<Event<List<Warehouse>>>()

    init {
        loadProducts(appSettings.inOnlineMode)
    }

    fun onAddProduct() {
        navigateToAddProduct.value = Event(Event.EMPTY_CONTENT)
    }

    fun onProductClicked(product: Product) {
        navigateToEditProduct.value = Event(product)
    }

    fun onQuantityChangeClicked(product: Product, quantityChange: Int) {
        if (quantityChange != 0 && canChangeProductQuantity(product, quantityChange)) {
            isLoading.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                val result =
                    product.id?.let {
                        productsRepository.changeProductQuantity(
                            product,
                            quantityChange,
                            appSettings.currentWarehouseId,
                            appSettings.inOnlineMode
                        )
                    }
                isLoading.postValue(false)

                when (result) {
                    is Result.Success<Product> -> onProductQuantityChanged(result.data)
                    is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                    is Result.HttpError -> showMessage.postValue(Event(R.string.quantity_below_zero_error_message))
                    else -> showMessage.postValue(Event(R.string.generic_error_message))
                }
            }
        }
    }

    fun onLogout() {
        if (appSettings.signedInWithGoogle) {
            googleSignInClient.signOut()
        }
        appSettings.removeAll()
        navigateToSignIn.value = Event(Event.EMPTY_CONTENT)
    }

    fun loadProducts(sync: Boolean = false) {
        isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            val result = productsRepository.getProducts(
                appSettings.currentWarehouseId,
                appSettings.inOnlineMode || sync
            )
            isLoading.postValue(false)

            if (sync) showMessage.postValue(Event(R.string.successful_sync))

            when (result) {
                is Result.Success<List<Product>> -> onProductsLoaded(result.data)
                is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                else -> showMessage.postValue(Event(R.string.generic_error_message))
            }
        }
    }

    fun onDeleteProduct(position: Int) {
        val product = products.value!![position]

        viewModelScope.launch(Dispatchers.IO) {
            when (productsRepository.deleteProduct(product, appSettings.inOnlineMode)) {
                is Result.Success<Unit> -> onProductDeleted(position)
                is Result.NetworkError -> {
                    onProductDeleteFailure(position)
                    showMessage.postValue(Event(R.string.network_error_message))
                }
                else -> {
                    onProductDeleteFailure(position)
                    showMessage.postValue(Event(R.string.generic_error_message))
                }
            }
        }
    }

    fun hasUserDeletePermission(): Boolean = appSettings.hasDeletePermission

    fun isInOnlineMode(): Boolean = appSettings.inOnlineMode

    fun switchOnlineMode(isOnline: Boolean) {
        appSettings.inOnlineMode = isOnline

        if (isOnline) synchronize()
    }

    fun getCurrentWarehouseId(): Int = appSettings.currentWarehouseId

    fun setCurrentWarehouseId(id: Int) {
        if (appSettings.currentWarehouseId != id) {
            appSettings.currentWarehouseId = id
            loadProducts()
        }
    }

    fun onChangeWarehouseClicked() {
        isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            val result = warehousesRepository.getWarehouses(appSettings.inOnlineMode)
            isLoading.postValue(false)

            when (result) {
                is Result.Success<List<Warehouse>> ->
                    showWarehousesSelectDialog.postValue(Event(result.data))
                is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                else -> showMessage.postValue(Event(R.string.generic_error_message))
            }
        }
    }

    private fun synchronize() {
        isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            val requests = queue.peekAll()
            if (requests.isEmpty()) {
                loadProducts(true)
            } else {
                synchronize(requests)
            }
        }
    }

    private fun synchronize(requests: List<OfflineRequest>) {
        viewModelScope.launch(Dispatchers.IO) {
            val syncResults = ArrayList<SyncResult>(ArrayList())

            for (request in requests) {
                when (request.operation) {
                    Operation.ADD -> synchronizeAdd(request, syncResults).join()
                    Operation.UPDATE -> synchronizeUpdate(request, syncResults).join()
                    Operation.CHANGE_QUANTITY -> synchronizeChangeQuantity(
                        request,
                        syncResults
                    ).join()
                    Operation.DELETE -> synchronizeDelete(request, syncResults).join()
                }
            }
            queue.clearAll()
            showSyncResults.postValue(Event(syncResults))
            loadProducts(true)
        }
    }

    private fun synchronizeAdd(request: OfflineRequest, syncResults: ArrayList<SyncResult>) =
        viewModelScope.launch(Dispatchers.IO) {
            request.product.id = null
            val result = productsRepository.addProduct(request.product, true)
            addItemToShowInSyncResultDialog(request, result, syncResults).join()
        }


    private fun synchronizeUpdate(request: OfflineRequest, syncResults: ArrayList<SyncResult>) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = productsRepository.editProduct(request.product, true)
            addItemToShowInSyncResultDialog(request, result, syncResults).join()
        }


    private fun synchronizeChangeQuantity(
        request: OfflineRequest,
        syncResults: ArrayList<SyncResult>
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            request.delta?.let {
                val result = productsRepository.changeProductQuantity(
                    request.product,
                    it,
                    request.warehouseId ?: 1,
                    true
                )
                addItemToShowInSyncResultDialog(request, result, syncResults).join()
            }
        }

    private fun synchronizeDelete(request: OfflineRequest, syncResults: ArrayList<SyncResult>) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = productsRepository.deleteProduct(request.product, true)
            addItemToShowInSyncResultDialog(request, result, syncResults).join()
        }

    private fun addItemToShowInSyncResultDialog(
        request: OfflineRequest,
        result: Result<*>,
        syncResults: ArrayList<SyncResult>
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val wasSuccessful = result is Result.Success
            val message = if (result is Result.HttpError) result.message else "OK"

            val syncResult = SyncResult(request, wasSuccessful, message)
            syncResults.add(syncResult)
        }


    private fun onProductDeleteFailure(position: Int) =
        onProductDeleteFailure.postValue(Event(position))

    private fun onProductDeleted(position: Int) =
        viewModelScope.launch(Dispatchers.Main) {
            products.value!!.removeAt(position)
            onProductDeleted.value = Event(position)
            showMessage.postValue(Event(R.string.product_deleted))
        }

    private fun onProductsLoaded(products: List<Product>) =
        this.products.postValue(ArrayList(products))

    private fun onProductQuantityChanged(product: Product) {
        val position = products.value!!.indexOfFirst { product.id == it.id }
        products.value!![position] = product

        onProductQuantityChanged.postValue(Event(position))
        showMessage.postValue(Event(R.string.product_quantity_changed_message))
    }

    private fun canChangeProductQuantity(product: Product, quantityChange: Int): Boolean {
        val quantity = product.quantity + quantityChange
        return (quantity >= 0).also { if (quantity < 0) showMessage.postValue(Event(R.string.quantity_below_zero_error_message)) }
    }
}
