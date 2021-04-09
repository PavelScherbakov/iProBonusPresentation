package ru.pscher.android.iprobonuspresentation.ui.bonus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.pscher.android.iprobonuspresentation.ui.shared.SharedViewModel


class BonusGeneralInfoViewModelFactory (private val authViewModel: SharedViewModel) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BonusGeneralInfoViewModel::class.java)) {
            return BonusGeneralInfoViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}