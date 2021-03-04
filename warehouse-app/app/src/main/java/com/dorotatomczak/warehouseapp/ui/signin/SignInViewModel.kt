package com.dorotatomczak.warehouseapp.ui.signin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.model.OAuth
import com.dorotatomczak.warehouseapp.data.model.Permission
import com.dorotatomczak.warehouseapp.data.model.TokenInfo
import com.dorotatomczak.warehouseapp.data.remote.api.Result
import com.dorotatomczak.warehouseapp.data.repository.AuthRepository
import com.dorotatomczak.warehouseapp.ui.util.AppSettings
import com.dorotatomczak.warehouseapp.ui.util.Event
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignInViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>(false)

    val navigateToSignUp = MutableLiveData<Event<String>>()
    val navigateToProducts = MutableLiveData<Event<String>>()
    val showMessage = MutableLiveData<Event<Int>>()
    val hideKeyboard = MutableLiveData<Event<String>>()

    fun onSignIn() {
        isLoading.postValue(true)
        hideKeyboard.postValue(Event(Event.EMPTY_CONTENT))

        viewModelScope.launch(Dispatchers.IO) {

            val result =
                authRepository.signIn(username.value.toString(), password.value.toString())

            isLoading.postValue(false)

            when (result) {
                is Result.Success<OAuth> -> onSignInSuccess(result.data)
                is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                is Result.HttpError -> onSignInHttpError(result.code)
                else -> showMessage.postValue(Event(R.string.generic_error_message))
            }
        }
    }

    fun onGoogleSignIn(task: Task<GoogleSignInAccount>) {
        isLoading.postValue(true)
        try {
            val account = task.getResult(ApiException::class.java)

            viewModelScope.launch(Dispatchers.IO) {
                val result =
                    authRepository.verifyGoogleToken(account!!.idToken!!)

                isLoading.postValue(false)

                when (result) {
                    is Result.Success<OAuth> -> onSignInSuccess(result.data, withGoogle = true)
                    is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                    else -> showMessage.postValue(Event(R.string.generic_error_message))
                }
            }
        } catch (e: Exception) {
            showMessage.postValue(Event(R.string.generic_error_message))
        }
    }

    fun onSignUp() {
        navigateToSignUp.value = Event(Event.EMPTY_CONTENT)
    }

    private fun onSignInSuccess(auth: OAuth, withGoogle: Boolean = false) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {

            val result =
                authRepository.checkToken(auth.accessToken)

            isLoading.postValue(false)

            when (result) {
                is Result.Success<TokenInfo> -> onTokenChecked(result.data, auth, withGoogle)
                is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                is Result.HttpError -> onSignInHttpError(result.code)
                else -> showMessage.postValue(Event(R.string.generic_error_message))
            }
        }
    }

    private fun onTokenChecked(tokenInfo: TokenInfo, auth: OAuth, wihGoogle: Boolean) {
        if (tokenInfo.authorities.contains(Permission.INVENTORY_REMOVE)) {
            appSettings.hasDeletePermission = true
        }

        appSettings.signedInWithGoogle = wihGoogle
        appSettings.accessToken = auth.accessToken
        appSettings.refreshToken = auth.refreshToken
        navigateToProducts.postValue(Event(Event.EMPTY_CONTENT))
    }

    private fun onSignInHttpError(code: Int) {
        if (code == 400) {
            showMessage.postValue(Event(R.string.incorrect_credentials_message))
        } else {
            showMessage.postValue(Event(R.string.generic_error_message))
        }
    }
}
