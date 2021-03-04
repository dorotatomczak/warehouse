package com.dorotatomczak.warehouseapp.ui.editproduct

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.data.remote.api.Result
import com.dorotatomczak.warehouseapp.data.repository.ProductsRepository
import com.dorotatomczak.warehouseapp.ui.util.AppSettings
import com.dorotatomczak.warehouseapp.ui.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProductViewModel @ViewModelInject constructor(
    private val productsRepository: ProductsRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    val product = MutableLiveData<Product>()

    val goBack = MutableLiveData<Event<String>>()
    val showMessage = MutableLiveData<Event<Int>>()
    val onProductEdited = MutableLiveData<Event<Product>>()

    fun onEditProduct() {
        if (isFormValid()) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = productsRepository.editProduct(product.value!!, appSettings.inOnlineMode)) {
                    is Result.Success<Product> -> onProductEdited(result.data)
                    is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                    else -> showMessage.postValue(Event(R.string.generic_error_message))
                }
            }
        } else {
            showMessage.value = Event(R.string.empty_fields_message)
        }
    }

    fun onCancel() {
        goBack.value = Event(Event.EMPTY_CONTENT)
    }

    private fun onProductEdited(product: Product) {
        product.quantity = this.product.value!!.quantity
        onProductEdited.postValue(Event(product))
    }

    private fun isFormValid(): Boolean = !(
            product.value?.name.isNullOrBlank() || product.value?.manufacturer.isNullOrBlank() || product.value?.price == null)

}
