package com.ahmedzenhom.mytasks.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ahmedzenhom.mytasks.data.repository.AuthRepository
import com.ahmedzenhom.mytasks.data.repository.SettingsRepository
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    private val _navigationLiveData: MutableLiveData<NavigationCases> = MutableLiveData()
    val navigationLiveData: LiveData<NavigationCases> get() = _navigationLiveData

    init {
        safeLauncher { checkNavigation() }
    }

    private fun checkNavigation() = safeLauncher {
        val isLoggedIn = settingsRepository.isLoggedIn()
        if (!isLoggedIn) authRepository.logout()
        _navigationLiveData.value = when {
            !isLoggedIn -> NavigationCases.TO_LOGIN
            else -> NavigationCases.TO_MAIN_SCREEN
        }
    }
}

enum class NavigationCases {
    TO_LOGIN,
    TO_MAIN_SCREEN,
}