package com.codesgood.restaurants.ui.fragment

import androidx.fragment.app.Fragment
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {
    protected lateinit var disposableObserver: Disposable

    override fun onStop() {
        super.onStop()
        if (::disposableObserver.isInitialized) {
            disposableObserver.dispose()
        }
    }
}