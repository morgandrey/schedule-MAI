package com.example.schedulemai.utils

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.fragment.app.Fragment
import io.reactivex.Observable


/**
 * Created by Andrey Morgunov on 05/03/2021.
 */

object Constants {
    const val LANDING_SERVER = "http://mai.ru/education/schedule/"
}

object Utils {
    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Observable<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return Observable.just(false)
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return Observable.just(false)
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Observable.just(true)
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Observable.just(true)
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> Observable.just(true)
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> Observable.just(true)
                else -> Observable.just(false)
            }
        } else {
            return Observable.just(connectivityManager.activeNetworkInfo?.isConnected ?: false)
        }
    }

    fun showInternetConnectionLost(context: Context) {
        AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle("No Internet Connection")
            .setMessage("Please make sure your Wi-Fi or mobile data is turned on, then try again.")
            .create()
            .show()
    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }
}