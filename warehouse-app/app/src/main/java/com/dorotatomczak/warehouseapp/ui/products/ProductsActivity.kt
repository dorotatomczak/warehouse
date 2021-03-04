package com.dorotatomczak.warehouseapp.ui.products

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.data.model.SyncResult
import com.dorotatomczak.warehouseapp.data.model.Warehouse
import com.dorotatomczak.warehouseapp.databinding.ActivityProductsBinding
import com.dorotatomczak.warehouseapp.ui.addproduct.AddProductActivity
import com.dorotatomczak.warehouseapp.ui.base.BaseActivity
import com.dorotatomczak.warehouseapp.ui.editproduct.EditProductActivity
import com.dorotatomczak.warehouseapp.ui.signin.SignInActivity
import com.dorotatomczak.warehouseapp.ui.util.Converter
import com.dorotatomczak.warehouseapp.ui.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.dialog_change_quantity.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


@AndroidEntryPoint
class ProductsActivity : AppCompatActivity(), BaseActivity {

    companion object {
        private const val ADD_PRODUCT_REQUEST_CODE = 1
        private const val EDIT_PRODUCT_REQUEST_CODE = 2
    }

    private lateinit var binding: ActivityProductsBinding
    private val viewModel: ProductsViewModel by viewModels()

    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_products)
        binding.productsViewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.products)

        swipeRefreshLayoutProducts.setOnRefreshListener { viewModel.loadProducts() }

        setUpRecyclerView()
        setUpObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_products, menu)

        val menuItem = menu?.findItem(R.id.menuOnlineMode)
        val switch = menuItem!!.actionView as SwitchCompat

        switch.isChecked = viewModel.isInOnlineMode()

        switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchOnlineMode(isChecked)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuLogout -> viewModel.onLogout()
            R.id.menuChangeWarehouse -> viewModel.onChangeWarehouseClicked()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_PRODUCT_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.getSerializableExtra(
                        AddProductActivity.REQUEST_RESULT_PRODUCT
                    ).let {
                        val insertPosition = viewModel.products.value!!.size
                        viewModel.products.value!!.add(insertPosition, it as Product)
                        productsAdapter.notifyItemInserted(insertPosition)
                        showMessage(R.string.product_added)
                    }
                }
            }

            EDIT_PRODUCT_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.getSerializableExtra(
                        EditProductActivity.REQUEST_RESULT_PRODUCT
                    ).let {
                        val position = viewModel.products.value!!
                            .indexOfFirst { product -> product.id == (it as Product).id }
                        viewModel.products.value!![position] = it as Product
                        productsAdapter.notifyItemChanged(position)
                        showMessage(R.string.product_edited)
                    }
                }
            }
        }
    }

    override fun setUpObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            swipeRefreshLayoutProducts.isRefreshing = isLoading
        }

        viewModel.showMessage.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                showMessage(it)
            }
        }

        viewModel.showStringMessage.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                showMessage(it)
            }
        }

        viewModel.navigateToAddProduct.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                navigateToAddProduct()
            }
        }

        viewModel.navigateToEditProduct.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                navigateToEditProduct(it)
            }
        }

        viewModel.navigateToSignIn.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                navigateToSignIn()
            }
        }

        viewModel.products.observe(this) { products ->
            productsAdapter.products = products
            productsAdapter.notifyDataSetChanged()
        }

        viewModel.onProductDeleted.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                productsAdapter.notifyItemRemoved(it)
            }
        }

        viewModel.onProductDeleteFailure.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                productsAdapter.notifyItemChanged(it)
            }
        }

        viewModel.onProductQuantityChanged.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                productsAdapter.notifyItemChanged(it)
            }
        }

        viewModel.showSyncResults.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                showSyncResultsDialog(it)
            }
        }

        viewModel.showWarehousesSelectDialog.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                showWarehousesSelectDialog(it)
            }
        }
    }

    override fun hideKeyboard() = coordinatorLayoutProducts.hideKeyboard()

    override fun showMessage(resId: Int) = showMessage(getString(resId))

    private fun showMessage(message: String) =
        Snackbar.make(coordinatorLayoutProducts, message, Snackbar.LENGTH_SHORT).show()

    private fun setUpRecyclerView() {
        productsAdapter = ProductsAdapter(
            fun(product: Product) {
                viewModel.onProductClicked(product)
            },
            fun(product: Product, shouldAddItems: Boolean) {
                showChangeQuantityDialog(product, shouldAddItems)
            }
        )

        recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        recyclerViewProducts.adapter = productsAdapter

        if (viewModel.hasUserDeletePermission()) {
            val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    showDeleteConfirmationDialog(position)
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(recyclerViewProducts)
        }
    }

    private fun navigateToAddProduct() {
        val intent = Intent(this, AddProductActivity::class.java)
        startActivityForResult(intent, ADD_PRODUCT_REQUEST_CODE)
    }

    private fun navigateToEditProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java).apply {
            putExtra(EditProductActivity.PRODUCT_EXTRA, product)
        }
        startActivityForResult(intent, EDIT_PRODUCT_REQUEST_CODE)
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.delete_product_confirmation))
            setTitle(getString(R.string.delete_product))
            setPositiveButton(
                R.string.yes
            ) { _, _ ->
                viewModel.onDeleteProduct(position)
            }
            setNegativeButton(
                R.string.no
            ) { dialog, _ ->
                dialog.cancel()
                productsAdapter.notifyItemChanged(position)
            }
        }.create().show()
    }

    private fun showChangeQuantityDialog(product: Product, shouldAddItems: Boolean) {
        val messageResId =
            if (shouldAddItems) R.string.add_items_message else R.string.remove_items_message
        val layout = layoutInflater.inflate(R.layout.dialog_change_quantity, null);

        AlertDialog.Builder(this).apply {
            setMessage(getString(messageResId))
            setTitle(getString(R.string.change_quantity))
            setView(layout)
            setPositiveButton(
                R.string.ok
            ) { _, _ ->
                var quantity = Converter.stringToInt(layout.editTextQuantity.text.toString())
                if (!shouldAddItems) {
                    quantity *= -1
                }
                viewModel.onQuantityChangeClicked(product, quantity)
            }
            setNegativeButton(
                R.string.cancel
            ) { dialog, _ ->
                dialog.cancel()
            }
        }.create().show()
    }

    private fun showSyncResultsDialog(syncResults: ArrayList<SyncResult>) {
        val adapter = SynchronizationAdapter(this, R.layout.item_request, syncResults)
        val layout = layoutInflater.inflate(R.layout.dialog_requests, null);

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(layout)
        alertDialog.setTitle(R.string.synchronization_results)
        alertDialog.setPositiveButton(
            R.string.ok
        ) { dialog, _ ->
            dialog.cancel()
        }

        val listView = layout.findViewById(R.id.listViewRequests) as ListView
        listView.adapter = adapter

        alertDialog.show()
    }

    private fun showWarehousesSelectDialog(warehouses: List<Warehouse>) {
        val listItems = warehouses.map { it.name }.toTypedArray()
        val currentWarehouse = warehouses.find { it.id == viewModel.getCurrentWarehouseId() }

        var currentWarehouseIndex = warehouses.indexOf(currentWarehouse)

        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.change_warehouse))

            setSingleChoiceItems(
                listItems,
                currentWarehouseIndex
            ) { _, which ->
                currentWarehouseIndex = which
            }

            setPositiveButton(
                getString(R.string.ok)
            ) { dialog, _ ->
                dialog.dismiss()
                if (warehouses.size > currentWarehouseIndex && currentWarehouseIndex >= 0) {
                    warehouses[currentWarehouseIndex].id?.let { viewModel.setCurrentWarehouseId(it) }
                }
            }
        }.create().show()
    }
}
