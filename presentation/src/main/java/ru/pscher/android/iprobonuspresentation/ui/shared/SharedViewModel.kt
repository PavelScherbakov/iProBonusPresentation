package ru.pscher.android.iprobonuspresentation.ui.shared

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.pscher.android.iprobonuspresentation.IProBonusPresentation
import ru.pscher.android.iprobonuspresentation.pref.Preferences
import ru.pscher.android.iprobonuspresentation.ui.addLiveDataSources
import ru.pscher.android.iprobonuspresentation.ui.handlingRequestError
import ru.pscher.android.iprobonusrepository.IProBonusRepository
import ru.pscher.android.iprobonusrepository.network.IProBonusApi
import ru.pscher.android.iprobonusrepository.network.requests.AccessTokenRequest
import ru.pscher.android.iprobonusrepository.network.result.AccessTokenResult
import timber.log.Timber


class SharedViewModel(private val app: Application): AndroidViewModel(app) {

    val prefs: Preferences by lazy { Preferences.get(app) }

    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,
        PENDING
    }

    val authenticationState = MutableLiveData<AuthenticationState>()

    val authenticationRequestInProgress = MutableLiveData<Boolean>()
    val getBonusGeneralInfoRequestInProgress = MutableLiveData<Boolean>()
    val requestInProgress = MediatorLiveData<Boolean>()

    val loginErrorText = MutableLiveData<String>()
    val loginSystemErrorResId = MutableLiveData<Int>()

    init {
        Timber.d("SharedViewModel init")
    }

    fun login() : Job {
        authenticationState.value =
            AuthenticationState.PENDING

        val job = viewModelScope.launch {
            authenticationRequestInProgress.value = true

            if(IProBonusRepository.config.accessKey != null) {
                val onSuccess: (AccessTokenResult) -> Unit = {

                    Timber.i("token result: $it.toString()")

                    onAuthenticationSuccess(it.accessToken)

                }
                val onFailure: (AccessTokenResult) -> Unit = {
                    refuseAuthentication()
                }

                handlingRequestError(
                    null,
                    loginErrorText,
                    loginSystemErrorResId,
                    this@SharedViewModel,
                    true,
                    authenticationState,
                    onSuccess,
                    onFailure,
                    null
                ) {
                    Timber.i("execute IProBonusApi.retrofitService.postAccessToken")
                    IProBonusApi.retrofitService.postAccessToken(AccessTokenRequest(
                        IProBonusPresentation.config.clientId,
                        "",
                        "device",
                        IProBonusPresentation.config.deviceId))
                }
            } else {
                authenticationState.value =
                    AuthenticationState.UNAUTHENTICATED
            }
            authenticationRequestInProgress.value = null
        }

        return job
    }

    private fun onAuthenticationSuccess(accessToken: String?) {
        viewModelScope.launch {
            prefs.saveCredentials(accessToken)
        }

        if(authenticationState.value != SharedViewModel.AuthenticationState.AUTHENTICATED)
            authenticationState.value = SharedViewModel.AuthenticationState.AUTHENTICATED
    }

    private fun refuseAuthentication() {
        viewModelScope.launch {
            prefs.clearCredentials()

            // Сбросить данные
            //resetUserData()

            authenticationState.value =
                AuthenticationState.UNAUTHENTICATED
        }
    }

    /*private fun resetUserData() {
        _bonusGeneralInfo.value = null
    }*/

    init {
        requestInProgress.addLiveDataSources(authenticationRequestInProgress, getBonusGeneralInfoRequestInProgress)
    }

}