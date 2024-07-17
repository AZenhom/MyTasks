package com.ahmedzenhom.mytasks.ui.host

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ahmedzenhom.mytasks.R
import com.ahmedzenhom.mytasks.databinding.ActivityHostBinding
import com.ahmedzenhom.mytasks.ui.splash.SplashActivity
import com.ahmedzenhom.mytasks.utils.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity : BaseActivity<ActivityHostBinding, HostViewModel>() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, HostActivity::class.java)
        }
    }

    override val viewModel: HostViewModel by viewModels()
    override val binding: ActivityHostBinding by viewBinding(ActivityHostBinding::inflate)

    private lateinit var navController: NavController
    private lateinit var navOptions: NavOptions

    override fun onActivityCreated() {
        doubleBackToExitPressedOnce = false
        initObservers()
        initViews()
    }

    private fun initObservers() {
        viewModel.onLogOutLiveData.observe(this) {
            val intent = SplashActivity.getIntent(this)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        // Prevent navbar navigation recreate on re-selecting
        binding.bottomNav.setOnItemReselectedListener { println() }

        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
        navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph_main, true).build()
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        binding.bottomNav.setOnItemSelectedListener { item: MenuItem ->
            navController.navigate(item.itemId, null, navOptions)
            true
        }

    }
}