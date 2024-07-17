package com.ahmedzenhom.mytasks.ui.profile

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.databinding.FragmentProfileBinding
import com.ahmedzenhom.mytasks.ui.host.HostViewModel
import com.ahmedzenhom.mytasks.ui.splash.SplashActivity
import com.ahmedzenhom.mytasks.utils.base.BaseFragment
import com.ahmedzenhom.mytasks.utils.ui.InfoDialog
import com.ahmedzenhom.mytasks.utils.ui.showIfNotAdded
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {

    override val viewModel: ProfileViewModel by viewModels()
    override val binding by viewBinding(FragmentProfileBinding::bind)
    private val hostViewModel: HostViewModel by activityViewModels()

    override fun onViewCreated() {
        initViews()
    }

    override fun onResume() {
        super.onResume()
        getProfile()
    }

    private fun initViews() {
        with(binding) {
            toolbar.tvTitle.text = getString(R.string.profile)
            cvChangeLanguage.setOnClickListener { showChangeLanguageSheet() }
            btnLogout.setOnClickListener { showLogoutSheet() }
        }
    }

    private fun getProfile() = viewModel.getAccount().observe(viewLifecycleOwner) {
        binding.tvName.text = it?.name
        binding.tvPhone.text = it?.phone
    }

    private fun showChangeLanguageSheet() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = requireContext(),
            imageRes = R.drawable.warning,
            message = getString(R.string.are_you_sure_you_want_to_switch_language),
            onConfirm = { infoDialog?.dismiss();switchLanguage() },
            isCancelable = true
        )
        infoDialog.showIfNotAdded(parentFragmentManager, InfoDialog.TAG)
    }

    private fun showLogoutSheet() {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = requireContext(),
            imageRes = R.drawable.warning,
            message = getString(R.string.are_you_sure_you_want_to_log_out),
            confirmText = getString(R.string.logout),
            onConfirm = { infoDialog?.dismiss();logOut() },
            isCancelable = true
        )
        infoDialog.showIfNotAdded(parentFragmentManager, InfoDialog.TAG)
    }

    private fun switchLanguage() {
        viewModel.switchLanguage(requireContext()).observe(viewLifecycleOwner) {
            val intent = SplashActivity.getIntent(requireContext())
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun logOut() {
        hostViewModel.logout()
    }
}