package ru.pscher.android.iprobonuspresentation

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ru.pscher.android.iprobonusrepository.IProBonusRepository
import ru.pscher.android.iprobonusrepository.IProBonusRepositoryConfig

object IProBonusPresentation {
    lateinit var config: IProBonusPresentationConfig
    lateinit var app: Application

    fun init(app: Application, config: IProBonusPresentationConfig) {
        this.app = app
        this.config = config

        AndroidThreeTen.init(app)

        IProBonusRepository.init(app, IProBonusRepositoryConfig(
            config.baseUrl,
            config.accessKey,
            config.clientId,
            config.deviceId))
    }
}

data class IProBonusPresentationConfig (
    val baseUrl: String,
    val accessKey: String,
    val clientId: String,
    val deviceId: String)
