package com.dorotatomczak.warehouseapp.ui.editproduct

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.databinding.ActivityEditProductBinding
import com.dorotatomczak.warehouseapp.ui.base.BaseActivity
import com.dorotatomczak.warehouseapp.ui.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_edit_product.*
import kotlinx.android.synthetic.main.toolbar.*

@AndroidEntryPoint
class EditProductActivity : AppCompatActivity(), BaseActivity {

    companion object {
        const val PRODUCT_EXTRA = "PRODUCT_EXTRA"
        const val REQUEST_RESULT_PRODUCT = "PRODUCT"
    }

    private lateinit var binding: ActivityEditProductBinding
    private val viewModel: EditProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_product)
        binding.editProductViewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.edit_product)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        intent.extras?.let {
            if (it.containsKey(PRODUCT_EXTRA)) {
                val product = intent.getSerializableExtra(PRODUCT_EXTRA) as Product
                viewModel.product.value = product
            }
        }

        setUpObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun setUpObservers() {
        viewModel.goBack.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                onBackPressed()
            }
        }

        viewModel.showMessage.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                showMessage(it)
            }
        }

        viewModel.onProductEdited.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                onProductEdited(it)
            }
        }
    }

    override fun hideKeyboard() = linearLayoutEditProduct.hideKeyboard()

    override fun showMessage(resId: Int) =
        Snackbar.make(linearLayoutEditProduct, getString(resId), Snackbar.LENGTH_SHORT).show()

    private fun onProductEdited(product: Product) {
        val resultIntent = Intent().apply {
            putExtra(REQUEST_RESULT_PRODUCT, product)
        }
        setResult(RESULT_OK, resultIntent)
        onBackPressed()
    }
}
