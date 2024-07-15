package com.ahmedzenhom.mytasks.ui.authentication.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.databinding.ActivityLoginBinding
import com.ahmedzenhom.mytasks.ui.authentication.phone_auth.PhoneAuthActivity
import com.ahmedzenhom.mytasks.ui.host.HostActivity
import com.ahmedzenhom.mytasks.utils.base.BaseActivity
import com.ahmedzenhom.mytasks.utils.others.PhoneNumberUtils
import com.ahmedzenhom.mytasks.utils.others.PhoneNumberUtils.PhoneNumberUtils.isNull
import com.ahmedzenhom.mytasks.utils.ui.makeGone
import com.ahmedzenhom.mytasks.utils.ui.makeVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    companion object {
        fun getIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override val viewModel: LoginViewModel by viewModels()
    override val binding by viewBinding(ActivityLoginBinding::inflate)

    private var loginPhoneAuthResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) viewModel.saveAccountModel()
                .observe(this@LoginActivity) {
                    navigateToMain()
                }
        }

    private var registerPhoneAuthResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                viewModel
                    .registerUser(binding.etName.toString().trim())
                    .observe(this@LoginActivity) { navigateToMain() }
        }

    private val phoneTextWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.trim().count() > 15) {
                    binding.tilPhone.error = getString(R.string.error_invalid_phone)
                    binding.etPhone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilPhone.error = null
                hideNameField()
            }
        }
    }

    override fun onActivityCreated() {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            etPhone.addTextChangedListener(phoneTextWatcher)
            // Click Listeners
            btnLogin.setOnClickListener {
                hideNameField()
                val phoneNo = validatePhoneNumber() ?: return@setOnClickListener
                viewModel.checkIfPhoneExists(phoneNo).observe(this@LoginActivity) { exists ->
                    if (exists) loginPhoneAuthResultLauncher.launch(
                        PhoneAuthActivity.getIntent(this@LoginActivity, phoneNo)
                    )
                    else showErrorMsg(getString(R.string.phone_number_not_registered))
                }
            }
            btnCreateAccount.setOnClickListener {
                val phoneNo = validatePhoneNumber() ?: return@setOnClickListener
                viewModel.checkIfPhoneExists(phoneNo).observe(this@LoginActivity) { exists ->
                    if (!exists) {
                        val name = etName.text.toString().trim()
                        if (name.isEmpty()) {
                            lblName.makeVisible()
                            tilName.makeVisible()
                            showWarningMsg(getString(R.string.please_enter_you_name))
                        } else registerPhoneAuthResultLauncher.launch(
                            PhoneAuthActivity.getIntent(this@LoginActivity, phoneNo)
                        )
                    } else showErrorMsg(getString(R.string.phone_number_already_registered))
                }
            }
        }
    }

    private fun validatePhoneNumber(): String? {
        val selectedCountryIso = binding.countryPicker.selectedCountryNameCode
        val phoneText = binding.etPhone.text.toString().trim()
        val phoneNumberObject =
            PhoneNumberUtils.getPhoneIfValid(this, selectedCountryIso, phoneText)
        if (phoneNumberObject.isNull()) {
            showWarningMsg(getString(R.string.error_invalid_phone))
            return null
        }
        return "+" + phoneNumberObject?.countryCode.toString() + phoneNumberObject?.nationalNumber.toString()
    }

    private fun hideNameField() = with(binding) {
        lblName.makeGone()
        tilName.makeGone()
        etName.text = null
    }

    private fun navigateToMain() {
        val intent = HostActivity.getIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

}