package com.codesgood.restaurants.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codesgood.restaurants.R

/**
 * Responsible of showing the loading ProgressBar while the app is fetching the auth token and user's location
 *
 * @author Amilcar Serrano
 */
class SplashFragment : Fragment() {
    companion object {
        const val TAG = "SplashFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}