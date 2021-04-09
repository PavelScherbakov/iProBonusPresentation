package ru.pscher.android.iprobonuspresentation.model

import org.threeten.bp.LocalDateTime


data class BonusGeneralInfoModel(
    val typeBonusName: String?,
    val currentQuantity: Double?,
    val forBurningQuantity: Double?,
    val dateBurning: LocalDateTime?
)