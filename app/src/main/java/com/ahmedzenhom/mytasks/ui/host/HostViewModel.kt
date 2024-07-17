package com.ahmedzenhom.mytasks.ui.host

import androidx.lifecycle.LiveData
import com.ahmedzenhom.mytasks.data.repository.AuthRepository
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    private val _onLogOutLiveData: LiveEvent<Boolean> = LiveEvent()
    val onLogOutLiveData: LiveData<Boolean> get() = _onLogOutLiveData

    fun logout() = safeLauncher {
        authRepository.logout()
        _onLogOutLiveData.value = true
    }
}