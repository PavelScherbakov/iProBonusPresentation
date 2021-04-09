package ru.pscher.android.iprobonuspresentation.ui.bonus

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.pscher.android.iprobonuspresentation.model.BonusGeneralInfoModel
import ru.pscher.android.iprobonuspresentation.ui.addLiveDataSources
import ru.pscher.android.iprobonuspresentation.ui.extractModel
import ru.pscher.android.iprobonuspresentation.ui.handlingRequestError
import ru.pscher.android.iprobonuspresentation.ui.shared.SharedViewModel
import ru.pscher.android.iprobonusrepository.network.result.DataInfoByAvailableBonusesResult
import ru.pscher.android.iprobonusrepository.repository.BonusRepository
import timber.log.Timber

class BonusGeneralInfoViewModel (private val authViewModel: SharedViewModel) : ViewModel() {
    private val bonusRepository = BonusRepository()

    // Данные о бонусах
    private val _bonusGeneralInfo = MutableLiveData<BonusGeneralInfoModel>()
    val bonusGeneralInfo: LiveData<BonusGeneralInfoModel>
        get() = _bonusGeneralInfo


    private val getBonusGeneralInfoRequestInProgress = MutableLiveData<Boolean>()

    // Union live data of all requests state, is set up in init section
    val requestInProgress = MediatorLiveData<Boolean>()

    val errorText = MutableLiveData<String>()
    val systemErrorResId = MutableLiveData<Int>()

    init {
        requestInProgress.addLiveDataSources(
            getBonusGeneralInfoRequestInProgress)

        refreshBonusSources()
    }


    private fun refreshBonusSources() {
        val onSuccess: (DataInfoByAvailableBonusesResult) -> Unit = {
            Timber.d("Add services: $it")
            viewModelScope.launch {
                _bonusGeneralInfo.value = it.extractModel()
            }
        }

        viewModelScope.launch {
            handlingRequestError(
                getBonusGeneralInfoRequestInProgress,
                errorText,
                systemErrorResId,
                authViewModel,
                true,
                authViewModel.authenticationState,
                onSuccess,
                null,
                null
            ) {
                //Если токена ещё нет, то получаем
                if (!authViewModel.prefs.hasAuthCredentials()) {
                    //Получаем токен, ждём выполнения запроса
                    authViewModel.login().join()
                }

                bonusRepository.getDataInfoByAvailableBonuses(
                    authViewModel.prefs.getCredentials()!!
                )
            }
        }
    }

    fun onRefresh() {
        refreshBonusSources()
    }
}