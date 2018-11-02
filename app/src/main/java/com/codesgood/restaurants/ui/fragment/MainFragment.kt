package com.codesgood.restaurants.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.codesgood.restaurants.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Manages the RestaurantListFragment and RestaurantMapFragment, switches them with a BottomNavigationView
 *
 * @author Amilcar Serrano
 */
class MainFragment : Fragment() {

    //Snackbar that explain the location change feature
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

        //Initializing the snackbar,couldn't do it on Fragment creation because of its Action's listener
        snackbar = Snackbar.make(fragmentContainer, getString(R.string.location_change_instructions), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.got_it)) { this.snackbar.dismiss() }

        //Actions executed when items of the BottomNavigationView are selected
        mainBottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
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

        //If this is the first load, the list fragment has to be placed
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
                //Placing the map fragment and explaining feature
                mainBottomNavigation.menu.findItem(R.id.map_action).isChecked = true
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
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, listFragment ?: RestaurantListFragment(), RestaurantListFragment.TAG)
            .commit()
    }

    private fun placeMapFragment() {
        val mapFragment = childFragmentManager.findFragmentByTag(RestaurantMapFragment.TAG)

        val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, mapFragment ?: RestaurantMapFragment(), RestaurantMapFragment.TAG)

        if (mapFragment == null) {
            transaction.addToBackStack(RestaurantMapFragment.TAG)
        }
        transaction.commit()
    }

    companion object {
        const val TAG = "MainFragment"
    }
}