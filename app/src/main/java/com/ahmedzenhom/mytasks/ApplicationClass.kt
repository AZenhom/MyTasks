package com.ahmedzenhom.mytasks

import android.app.Application
import com.ahmedzenhom.mytasks.data.repository.SettingsRepository
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
@DelicateCoroutinesApi
class ApplicationClass : Application() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            try {

                Lingver.init(this@ApplicationClass, settingsRepository.getCurrentLanguage())
            } catch (e: Exception) {
                e.printStackTrace()
                // This catch is done to handle Lingver re-initiating in unit testing
            }
        }
    }

}