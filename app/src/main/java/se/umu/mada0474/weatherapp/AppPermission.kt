package se.umu.mada0474.weatherapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import se.umu.mada0474.weatherapp.constants.AppConstants

/**
 * This class is used to handle permission for the application.
 * @author Mahmoud Daabas
 */
class AppPermissions{

    /**
     * Checks if the user gave consent to share their location.
     */
    fun isLocationOk(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests that the user approves the application to use their location.
     */
    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            AppConstants.LOCATION_REQUEST_CODE
        )
    }
}