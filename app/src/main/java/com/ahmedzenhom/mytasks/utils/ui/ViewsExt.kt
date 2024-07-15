package com.ahmedzenhom.mytasks.utils.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ahmedzenhom.mytasks.R
import com.kennyc.view.MultiStateView


fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.setIsVisible(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}


fun MultiStateView.showLoading() {
    viewState = MultiStateView.ViewState.LOADING
}

fun MultiStateView.showContent() {
    viewState = MultiStateView.ViewState.CONTENT
}

fun MultiStateView.showError(errorMessage: String? = null, onRetry: () -> Unit) {
    viewState = MultiStateView.ViewState.ERROR
    val view = getView(MultiStateView.ViewState.ERROR)
    errorMessage?.let {
        val textView: TextView? = view?.findViewById(R.id.tvErrorMessage)
        textView?.text = it
    }
    val button: Button? = view?.findViewById(R.id.btn_retry)
    button?.setOnClickListener {
        onRetry.invoke()
    }
}

fun Context.isNightMode(): Boolean {
    val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == UI_MODE_NIGHT_YES
}

fun Context.isRTL(): Boolean {
    val configuration = resources.configuration
    return configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

fun DialogFragment.showIfNotAdded(fragmentManager: FragmentManager, tag: String) {
    val fragment = fragmentManager.findFragmentByTag(tag)
    if (fragment == null || !fragment.isAdded) show(fragmentManager, tag)
}