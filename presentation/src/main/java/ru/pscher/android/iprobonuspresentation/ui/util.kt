package ru.pscher.android.iprobonuspresentation.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ru.pscher.android.iprobonuspresentation.R
import ru.pscher.android.iprobonuspresentation.exceptions.ReloginException
import ru.pscher.android.iprobonuspresentation.model.BonusGeneralInfoModel
import ru.pscher.android.iprobonuspresentation.ui.shared.SharedViewModel
import ru.pscher.android.iprobonusrepository.network.result.BasicResult
import ru.pscher.android.iprobonusrepository.network.result.DataInfoByAvailableBonusesResult
import timber.log.Timber
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.pscher.android.iprobonuspresentation.ui.dialog.ConfirmDialogFragment
import java.util.*


suspend fun <T: BasicResult> handlingRequestError(
    requestInProgress: MutableLiveData<Boolean>?,
    errorText: MutableLiveData<String>,
    systemErrorResId: MutableLiveData<Int>,
    sharedViewModel: SharedViewModel,
    withReloginTry: Boolean = true,
    authState: MutableLiveData<SharedViewModel.AuthenticationState>? = null,
    onSuccess: ((T) -> Unit)? = null,
    onFailure: ((T) -> Unit)? = null,
    onSysError: (() -> Unit)? = null,
    successCodes: Array<Int>? = null,
    requestCall: suspend () -> T) {

    val treatErrorsAsSuccess = successCodes ?: emptyArray<Int>() + arrayOf(BasicResult.RESULT_OK)

    requestInProgress?.value = true
    try {
        val result = withContext(Dispatchers.IO) {
            Timber.e("requestCall()")
            requestCall()
        }

        when(result.resultOperation.status.value) {
            in treatErrorsAsSuccess -> {
                errorText.value = null
                systemErrorResId.value = null
                onSuccess?.let{ it(result) }
            }
            BasicResult.ERROR_TOKEN_EXPIRED -> throw ReloginException()
            else -> {
                onFailure?.let { it(result) }
                errorText.value = result.resultOperation.message
                systemErrorResId.value = null
            }
        }
    } catch (e: ReloginException) {
        if(withReloginTry) {
            sharedViewModel.login()
            if(authState?.value == SharedViewModel.AuthenticationState.AUTHENTICATED) {
                handlingRequestError(
                        requestInProgress,
                        errorText,
                        systemErrorResId,
                        sharedViewModel,
                        false,
                        authState,
                        onSuccess,
                        onFailure,
                        onSysError,
                        successCodes,
                        requestCall
                )
            }
        }
    } catch (e: Exception) {
        Timber.e(e)
        onSysError?.let { it() }
        errorText.value = null
        systemErrorResId.value = R.string.common_api_exception_text
    }
    requestInProgress?.value = null
}

suspend fun DataInfoByAvailableBonusesResult.extractModel(): BonusGeneralInfoModel {
    return BonusGeneralInfoModel(this.data?.typeBonusName,
            this.data?.currentQuantity,
            this.data?.forBurningQuantity,
            this.data?.dateBurning)
}

fun MediatorLiveData<Boolean>.addLiveDataSources(vararg sources: LiveData<Boolean>) {
    val onChanged = Observer<Boolean> {
        this.value = sources.any { it.value == true }
    }

    sources.forEach {  source ->
        this.addSource(source, onChanged)
    }
}

fun Fragment.showErrorDialog(message: String, isHtml: Boolean = false) {
    val dialogTitle = resources.getString(R.string.error_title)
    val buttonYes = R.string.button_ok
    val dialog = ConfirmDialogFragment.newInstance(dialogTitle, message, buttonYes, null, isHtml)
    val dialogId = UUID.randomUUID().toString()
    dialog.show(parentFragmentManager, dialogId)
}

fun Fragment.showErrorDialog(messageResId: Int) {
    val dialogTitle = resources.getString(R.string.error_title)
    val buttonYes = R.string.button_ok
    val message = resources.getString(messageResId)
    val dialog = ConfirmDialogFragment.newInstance(dialogTitle, message, buttonYes, null)
    val dialogId = UUID.randomUUID().toString()
    dialog.show(parentFragmentManager, dialogId)
}