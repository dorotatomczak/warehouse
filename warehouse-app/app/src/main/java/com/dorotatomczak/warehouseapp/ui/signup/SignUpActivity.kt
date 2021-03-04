package com.dorotatomczak.warehouseapp.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.databinding.ActivitySignUpBinding
import com.dorotatomczak.warehouseapp.ui.base.BaseActivity
import com.dorotatomczak.warehouseapp.ui.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.*

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity(), BaseActivity {

    companion object {
        const val REQUEST_RESULT_MESSAGE_RES_ID = "MESSAGE_RES_ID"
    }

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.signUpViewModel = viewModel
        binding.lifecycleOwner = this

        setUpObservers()
    }

    override fun setUpObservers() {
        viewModel.goBackToSignIn.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                onBackPressed()
            }
        }

        viewModel.showMessage.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                showMessage(it)
            }
        }

        viewModel.onSuccessfulSignUp.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                onSuccessfulSignUp(it)
            }
        }

        viewModel.hideKeyboard.observe(this) { event ->
            event?.getContentIfNotHandledOrReturnNull()?.let {
                hideKeyboard()
            }
        }
    }

    override fun hideKeyboard() = relativeLayoutSignUp.hideKeyboard()

    override fun showMessage(resId: Int) =
        Snackbar.make((relativeLayoutSignUp), getString(resId), Snackbar.LENGTH_SHORT).show()
    
    private fun onSuccessfulSignUp(resId: Int) {
        val resultIntent = Intent().apply {
            putExtra(REQUEST_RESULT_MESSAGE_RES_ID, resId)
        }
        setResult(RESULT_OK, resultIntent)
        onBackPressed()
    }
}
