package com.example.schedulemai.utils

import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.schedulemai.R
import com.google.android.material.snackbar.Snackbar


/**
 * Created by Andrey Morgunov on 05/03/2021.
 */


object NetworkUtils {
    fun showNetworkConnectionLostSnackBar(view: View) {
        val snack = Snackbar.make(
            view,
            R.string.network_connection_lost,
            Snackbar.LENGTH_LONG
        )
        snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .also { textView ->
                textView.gravity = Gravity.CENTER_HORIZONTAL
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
        snack.show()
    }
}