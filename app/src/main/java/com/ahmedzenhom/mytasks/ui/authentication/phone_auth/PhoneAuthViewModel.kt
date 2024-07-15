package com.ahmedzenhom.mytasks.ui.authentication.phone_auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.ahmedzenhom.mytasks.utils.base.BaseViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.data.repository.AuthRepository

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class PhoneAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val TAG = "PhoneAuthViewModel"
    }

    val phoneToVerify: String? = savedStateHandle[PhoneAuthActivity.PHONE_TO_VERIFY]

    private var activity: PhoneAuthActivity? = null
    private val auth = FirebaseAuth.getInstance()
    private lateinit var verificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) { // On Auto Verify
            Log.d(TAG, "onVerificationCompleted")
            startSigningInUsingCredentials(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "onVerificationFailed")
            hideLoading()
            showErrorMsg(R.string.phone_verification_failed)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent")
            this@PhoneAuthViewModel.verificationId = verificationId
            resendToken = token
            hideLoading()
            _onSmsCodeSentLiveData.value = true
        }
    }

    private val _onSmsCodeSentLiveData: LiveEvent<Boolean> = LiveEvent()
    val onSmsCodeSentLiveData: LiveData<Boolean> get() = _onSmsCodeSentLiveData

    private val _onPhoneAuthCompletedLiveData: LiveEvent<Boolean> = LiveEvent()
    val onPhoneAuthCompletedLiveData: LiveData<Boolean> get() = _onPhoneAuthCompletedLiveData

    init {
        auth.useAppLanguage()
    }

    fun startPhoneVerification(activity: PhoneAuthActivity) {
        Log.d(TAG, "startPhoneVerification")
        this.activity = activity
        showLoading()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneToVerify ?: return)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendPhoneVerification(activity: PhoneAuthActivity) {
        Log.d(TAG, "startPhoneVerification")
        this.activity = activity
        showLoading()
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneToVerify ?: return)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInUsingSentCode(activity: PhoneAuthActivity, sentCode: String) {
        this.activity = activity
        Log.d(TAG, "signInUsingSentCode")
        val credentials = PhoneAuthProvider.getCredential(verificationId, sentCode)
        startSigningInUsingCredentials(credentials)
    }

    private fun startSigningInUsingCredentials(credentials: PhoneAuthCredential) {
        Log.d(TAG, "startSigningInUsingCredentials")
        if (activity == null)
            return
        showLoading()
        auth.signInWithCredential(credentials)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful && task.result?.user != null) {
                    Log.d(TAG, "startSigningInUsingCredentials Success")
                    val user = task.result!!.user!!
                    safeLauncher {
                        authRepository.saveFirebaseUser(user)
                        authRepository.refreshAndSaveIdToken()
                        _onPhoneAuthCompletedLiveData.value = true
                    }
                } else { // Wrong Code
                    Log.d(TAG, "startSigningInUsingCredentials Failure" + task.exception.toString())
                    hideLoading()
                    showErrorMsg(R.string.invalid_verification_code)
                }
            }
    }

    override fun onCleared() {
        activity = null
        super.onCleared()
    }
}