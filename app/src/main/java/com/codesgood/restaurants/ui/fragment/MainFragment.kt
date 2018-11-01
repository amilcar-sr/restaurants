package com.codesgood.restaurants.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.codesgood.restaurants.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackbar = Snackbar.make(fragment_container, getString(R.string.location_change_instructions), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.got_it)) { this.snackbar.dismiss() }

        main_bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_action -> {
                    snackbar.dismiss()
                    placeListFragment()
                }
                R.id.map_action -> {
                    placeMapFragment()
                }
            }

            return@setOnNavigationItemSelectedListener true
        }

        if (savedInstanceState == null) {
            placeListFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.change_location_action -> {
                main_bottom_navigation.menu.findItem(R.id.map_action).isChecked = true
                placeMapFragment()
                snackbar.show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun placeListFragment() {
        val listFragment = childFragmentManager.findFragmentByTag(RestaurantListFragment.TAG)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, listFragment ?: RestaurantListFragment(), RestaurantListFragment.TAG)
            .commit()
    }

    private fun placeMapFragment() {
        val mapFragment = childFragmentManager.findFragmentByTag(RestaurantMapFragment.TAG)
        val transaction = childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mapFragment ?: RestaurantMapFragment(), RestaurantMapFragment.TAG)

        if (mapFragment == null) {
            transaction.addToBackStack(RestaurantMapFragment.TAG)
        }
        transaction.commit()
    }

    companion object {
        const val TAG = "MainFragment"
    }
}