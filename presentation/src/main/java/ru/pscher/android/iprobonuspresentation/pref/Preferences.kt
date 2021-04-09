package ru.pscher.android.iprobonuspresentation.pref

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Основной класс работы с настройками. Не вызывать методы в UI потоке!
 */
class Preferences private constructor(private val preferences: SharedPreferences) {
    companion object {
        private const val PREFERENCES_NAME = "iprobonus_test_preferences"

        const val USER_ACCESS_TOKEN = "user_access_token"

        fun get(context: Context): Preferences {
            return Preferences(context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE))
        }
    }

    /**
     * Проверить, есть ранее сохраненные данные для входа
     * Не вызывать в UI потоке!
     */
    suspend fun hasAuthCredentials(): Boolean {
        return withContext(Dispatchers.IO){
            preferences.contains(USER_ACCESS_TOKEN)
                    && !TextUtils.isEmpty(preferences.getString(USER_ACCESS_TOKEN, null))
        }
    }

    /**
     * Сохранить данные для входа после успешной авторизации
     * Не вызывать в UI потоке!
     */
    @SuppressLint("ApplySharedPref")
    suspend fun saveCredentials(accessToken: String?) {
        withContext(Dispatchers.IO) {
            val transaction = preferences.edit()
            transaction.putString(USER_ACCESS_TOKEN, accessToken)
            transaction.commit()
        }
    }


    /**
     * Удалить данные для входа при выходе или при неуспешной авторизации (например если был сменен пароль)
     * Не вызывать в UI потоке!
     */
    @SuppressLint("ApplySharedPref")
    suspend fun clearCredentials() {
        withContext(Dispatchers.IO) {
            val transaction = preferences.edit()
            transaction.putString(USER_ACCESS_TOKEN, null as String?)
            transaction.commit()
        }
    }

    /**
     * Получить сохраненнные данные для входа
     * - для автоматического входа при запуске приложения
     * - для повторного получения токена при его превышении срока жизни
     * Не вызывать в UI потоке!
     */
    suspend fun getCredentials(): String? {
        return withContext(Dispatchers.IO) {
            preferences.getString(USER_ACCESS_TOKEN, null)
        }
    }
}