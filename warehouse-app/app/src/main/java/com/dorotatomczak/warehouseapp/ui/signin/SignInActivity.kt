package com.dorotatomczak.warehouseapp.ui.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.databinding.ActivitySignInBinding
import com.dorotatomczak.warehouseapp.ui.base.BaseActivity
import com.dorotatomczak.warehouseapp.ui.products.ProductsActivity
import com.dorotatomczak.warehouseapp.ui.signup.SignUpActivity
import com.dorotatomczak.warehouseapp.ui.util.hideKeyboard
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_in.*
import javax.inject.Inject


@AndroidEntryPoint
class SignInActivity : AppCompatActivity(), BaseActivity {

    companion object {
        private const val SIGN_UP_REQUEST_CODE = 1
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 2
    }

    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels()

    @Inject lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        binding.signInViewModel = viewModel
        binding.lifecycleOwner = this

        buttonGoogleSignIn.setOnClickListener {
            onGoogleSignIn()
        }

        setUpObservers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SIGN_UP_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val resId = data.getIntExtra(
                        SignUpActivity.REQUEST_RESULT_MESSAGE_RES_ID,
                        R.string.generic_error_message
                    )
                    showMessage(resId)
                }
            }
            GOOGLE_SIGN_IN_REQUEST_CODE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                viewModel.onGoogleSignIn(task)
            }
        }
    }

    override fun setUpObservers() {
        viewModel.navigateToSignUp.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                navigateToSignUp()
            }
        }

        viewModel.navigateToProducts.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                navigateToProducts()
            }
        }

        viewModel.showMessage.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                showMessage(it)
            }
        }

        viewModel.hideKeyboard.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                hideKeyboard()
            }
        }
    }

    override fun showMessage(resId: Int) =
        Snackbar.make((relativeLayoutSignIn), getString(resId), Snackbar.LENGTH_SHORT).show()


    override fun hideKeyboard() = relativeLayoutSignIn.hideKeyboard()

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivityForResult(intent, SIGN_UP_REQUEST_CODE)
    }

    private fun navigateToProducts() {
        val intent = Intent(this, ProductsActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun onGoogleSignIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }
}
