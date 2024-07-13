package com.ahmedzenhom.mytasks.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.databinding.ActivitySplashBinding
import com.ahmedzenhom.mytasks.utils.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    companion object {
        fun getIntent(context: Context) = Intent(context, SplashActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override val viewModel: SplashViewModel by viewModels()
    override val binding: ActivitySplashBinding by viewBinding(ActivitySplashBinding::inflate)

    override fun onActivityCreated() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        initObservers()
        initAnimation()
    }

    private fun initAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.splash)
        animation.interpolator = AccelerateInterpolator()
        animation.duration = 600
        binding.ivLogo.animation = animation
    }

    private fun initObservers() {
        viewModel.navigationLiveData.observe(this) {
            when (it) {
                NavigationCases.TO_MAIN_SCREEN -> openMainScreen()
                NavigationCases.TO_LOGIN -> openLoginScreen()
                else -> Unit
            }
        }
    }

    private fun openMainScreen() {
        //startActivity(HostActivity.getIntent(this))
        //finish()
    }

    private fun openLoginScreen() {
        //startActivity(LoginActivity.getIntent(this))
        //finish()
    }
}