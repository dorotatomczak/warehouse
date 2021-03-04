package com.dorotatomczak.warehouseapp.ui.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.remote.api.Result
import com.dorotatomczak.warehouseapp.data.repository.AuthRepository
import com.dorotatomczak.warehouseapp.ui.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>(false)

    val goBackToSignIn = MutableLiveData<Event<String>>()
    val onSuccessfulSignUp = MutableLiveData<Event<Int>>()
    val showMessage = MutableLiveData<Event<Int>>()
    val hideKeyboard = MutableLiveData<Event<String>>()

    fun onSignIn() {
        goBackToSignIn.value = Event(Event.EMPTY_CONTENT)
    }

    fun onSignUp() {
        isLoading.postValue(true)
        hideKeyboard.postValue(Event(Event.EMPTY_CONTENT))

        if (isFormValid()) {
            viewModelScope.launch(Dispatchers.IO) {
                val result =
                    authRepository.signUp(username.value.toString(), password.value.toString())

                isLoading.postValue(false)

                when (result) {
                    is Result.Success<Unit> -> onSignUpSuccess()
                    is Result.NetworkError -> showMessage.postValue(Event(R.string.network_error_message))
                    is Result.HttpError -> {
                        if (result.code == 409) {
                            showMessage.postValue(Event(R.string.username_exists_message))
                        } else showMessage.postValue(Event(R.string.generic_error_message))
                    }
                    else -> showMessage.postValue(Event(R.string.generic_error_message))
                }
            }
        }
    }

    private fun onSignUpSuccess() {
        onSuccessfulSignUp.postValue(Event(R.string.sign_up_successful))
    }

    private fun isFormValid(): Boolean {

        if (username.value.isNullOrBlank()) {
            showMessage.value = Event(R.string.username_cannot_be_empty)
            return false
        }

        if (password.value.isNullOrBlank()) {
            showMessage.value = Event(R.string.password_cannot_be_empty)
            return false
        }

        if (password.value != confirmPassword.value) {
            showMessage.value = Event(R.string.passwords_are_not_the_same)
            return false
        }

        return true
    }
}
