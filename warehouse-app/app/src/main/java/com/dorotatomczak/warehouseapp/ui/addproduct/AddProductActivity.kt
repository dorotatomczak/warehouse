package com.dorotatomczak.warehouseapp.ui.addproduct

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.databinding.ActivityAddProductBinding
import com.dorotatomczak.warehouseapp.ui.base.BaseActivity
import com.dorotatomczak.warehouseapp.ui.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.toolbar.*


@AndroidEntryPoint
class AddProductActivity : AppCompatActivity(), BaseActivity {

    companion object {
        const val REQUEST_RESULT_PRODUCT = "PRODUCT"
    }

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: AddProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product)
        binding.addProductViewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.add_product)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
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

        viewModel.onProductAdded.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                onProductAdded(it)
            }
        }
    }

    override fun hideKeyboard() = linearLayoutAddProduct.hideKeyboard()

    override fun showMessage(resId: Int) =
        Snackbar.make(linearLayoutAddProduct, getString(resId), Snackbar.LENGTH_SHORT).show()

    private fun onProductAdded(product: Product) {
        val resultIntent = Intent().apply {
            putExtra(REQUEST_RESULT_PRODUCT, product)
        }
        setResult(RESULT_OK, resultIntent)
        onBackPressed()
    }
}
