package com.dorotatomczak.warehouseapp.ui.launcher

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dorotatomczak.warehouseapp.ui.util.AppSettings
import com.dorotatomczak.warehouseapp.ui.util.Event
import com.dorotatomczak.warehouseapp.ui.util.NetworkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.qualifiers.ApplicationContext

class LauncherViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val appSettings: AppSettings,
    private val networkManager: NetworkManager
) : ViewModel() {

    val navigateToSignIn = MutableLiveData<Event<String>>()
    val navigateToProducts = MutableLiveData<Event<String>>()

    fun redirect() {
        if (!networkManager.isOnline() && appSettings.signedIn) navigateToProducts.value = Event(Event.EMPTY_CONTENT)

        val signedInWithGoogle = GoogleSignIn.getLastSignedInAccount(context)

        when {
            signedInWithGoogle != null -> navigateToProducts.value = Event(Event.EMPTY_CONTENT)

            appSettings.refreshToken == null || appSettings.accessToken == null ->
                navigateToSignIn.value = Event(Event.EMPTY_CONTENT)

            else -> navigateToProducts.value = Event(Event.EMPTY_CONTENT)
        }
    }
}
