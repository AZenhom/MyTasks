package com.ahmedzenhom.mytasks.ui.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ahmedzenhom.mytasks.data.model.AccountModel
import com.ahmedzenhom.mytasks.data.repository.AuthRepository
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private var accountModel: AccountModel? = null

    fun checkIfPhoneExists(phoneNo: String): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            val user = authRepository.getUserByPhone(phoneNo)
            accountModel = user
            liveData.value = user != null
            hideLoading()
        }
        return liveData
    }

    fun saveAccountModel(): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        safeLauncher {
            showLoading()
            authRepository.saveAccountModel(accountModel ?: return@safeLauncher)
            liveData.value = true
            hideLoading()
        }
        return liveData
    }

    fun registerUser(name: String): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            showLoading()
            authRepository.registerUser(name)
            liveData.value = true
            hideLoading()
        }
        return liveData
    }
}