package com.ahmedzenhom.mytasks.ui.authentication.phone_auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import androidx.activity.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.databinding.ActivityPhoneAuthBinding
import com.ahmedzenhom.mytasks.utils.base.BaseActivity
import com.ahmedzenhom.mytasks.utils.ui.makeGone
import com.ahmedzenhom.mytasks.utils.ui.makeVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneAuthActivity : BaseActivity<ActivityPhoneAuthBinding, PhoneAuthViewModel>() {

    companion object {
        const val PHONE_TO_VERIFY = "PHONE_TO_VERIFY"
        fun getIntent(context: Context, phoneToVerify: String): Intent {
            return Intent(context, PhoneAuthActivity::class.java).apply {
                putExtra(PHONE_TO_VERIFY, phoneToVerify)
            }
        }
    }

    override val viewModel: PhoneAuthViewModel by viewModels()
    override val binding: ActivityPhoneAuthBinding by viewBinding(ActivityPhoneAuthBinding::inflate)


    override fun onActivityCreated() {
        initObservers()
        initViews()
        viewModel.startPhoneVerification(this)
    }

    private fun initObservers() {
        with(viewModel) {
            onSmsCodeSentLiveData.observe(this@PhoneAuthActivity) {
                resetSendCounter()
            }
            onPhoneAuthCompletedLiveData.observe(this@PhoneAuthActivity) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initViews() {
        with(binding) {
            lblSmsSent.text = getString(R.string.sms_has_been_sent, viewModel.phoneToVerify)
            pinView.setOtpCompletionListener {
                startSmsCodeVerification()
            }
            btnResend.setOnClickListener {
                viewModel.resendPhoneVerification(this@PhoneAuthActivity)
            }
            btnCodeVerify.setOnClickListener {
                startSmsCodeVerification()
            }
        }
    }

    private fun startSmsCodeVerification() {
        val code = binding.pinView.text.toString()
        if (code.length != binding.pinView.itemCount) {
            showWarningMsg(getString(R.string.invalid_verification_code))
            return
        }
        viewModel.signInUsingSentCode(this, code)
    }

    private fun resetSendCounter() = with(binding) {
        lblResend.makeVisible()
        btnResend.makeGone()
        startResendCounter()
    }

    private fun startResendCounter() = with(binding) {
        object : CountDownTimer(50000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                lblResend.text = getString(R.string.resend_question, millisUntilFinished / 1000)
            }

            // When the task is over it will print 00:00:00 there
            override fun onFinish() {
                lblResend.makeGone()
                btnResend.makeVisible()
            }
        }.start()
    }
}