package com.dorotatomczak.warehouseapp.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.dorotatomczak.warehouseapp.ui.products.ProductsActivity
import com.dorotatomczak.warehouseapp.ui.signin.SignInActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpObservers()
        viewModel.redirect()
    }

    private fun setUpObservers() {
        viewModel.navigateToSignIn.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                navigateToSignIn()
            }
        }

        viewModel.navigateToProducts.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                navigateToProducts()
            }
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun navigateToProducts() {
        val intent = Intent(this, ProductsActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }
}
