package com.ahmedzenhom.mytasks.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import com.ahmedzenhom.mytasks.data.model.AccountModel
import com.ahmedzenhom.mytasks.data.repository.AuthRepository
import com.ahmedzenhom.mytasks.data.repository.SettingsRepository
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import com.hadilq.liveevent.LiveEvent
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel() {

    fun getAccount(): LiveData<AccountModel> {
        val liveData = LiveEvent<AccountModel>()
        safeLauncher {
            showLoading()
            liveData.value = authRepository.getAccount()
            hideLoading()
        }
        return liveData
    }

    fun switchLanguage(context: Context): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            val currentLanguage = settingsRepository.getCurrentLanguage()
            val newLanguage = if (currentLanguage == "en") "ar" else "en"
            Lingver.getInstance().setLocale(context, newLanguage)
            settingsRepository.setCurrentLanguage(newLanguage)
            liveData.value = true
        }
        return liveData
    }
}