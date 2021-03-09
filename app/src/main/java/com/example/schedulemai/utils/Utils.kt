package com.example.schedulemai.utils

import android.view.View
import com.example.schedulemai.R
import com.google.android.material.snackbar.Snackbar


/**
 * Created by Andrey Morgunov on 05/03/2021.
 */


object Utils {
    fun showNetworkConnectionLostSnackBar(view: View) {
        Snackbar.make(
            view,
            R.string.network_connection_lost,
            Snackbar.LENGTH_LONG
        ).show()
    }
}