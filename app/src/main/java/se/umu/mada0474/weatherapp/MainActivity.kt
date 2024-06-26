package se.umu.mada0474.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit


/**
 * This class manages the main/search activity of the program.
 * @author Mahmoud Daabas
 */
class MainActivity : ToolbarHandlerActivity(){

    private lateinit var apiService: ApiService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissions: AppPermissions
    var cityName: String? = null


    /**
     * Creates the view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiService = ApiService(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        addButtonListeners()

        permissions = AppPermissions()
        if (permissions.isLocationOk(this)){
        }else{
            permissions.requestLocationPermission(this)
        }
    }

    /**
     * When called, this function will retrieve weather data from the API by using a city name.
     * The city name is given in the input on the main screen.
     */
    private fun getDataFromAPISearch() {
        val inputText = findViewById<EditText>(R.id.cityInputTxt)
        apiService.getCoordinatesFromName(inputText.text.toString(), object : ApiService.ApiResponseCallback {
            override fun onResponse(body: String?) {
                try {
                    if (body != null) {
                        val data = JSONObject(body)
                        val latitude = data.getJSONArray("results").getJSONObject(0).getString("latitude")
                        val longitude = data.getJSONArray("results").getJSONObject(0).getString("longitude")
                        val name = data.getJSONArray("results").getJSONObject(0).getString("name")
                        cityName = name //save this to pass to DisplaySearchActivity
                        apiService.getWeatherData(latitude.toDouble(), longitude.toDouble())
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Not found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Enter a valid city name..", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure() {
                try {
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "API call failed.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * When called, this function will retrieve weather data from the API by using the devices current location.
     */
    private fun getDataFromAPIMobileLocation(){
        Toast.makeText(this@MainActivity, "Requesting new location, please wait.", Toast.LENGTH_LONG).show()
        requestNewLocation()
    }

    /**
     * Requests a new location.
     */
    private fun requestNewLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(5)).build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                //Use the new location that was requested.
                if (location != null) {
                    apiService.getWeatherData(location.latitude, location.longitude)
                }
                //Remove location updates as we only need one update
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    /**
     * This function adds listeners to the buttons.
     */
    private fun addButtonListeners(){
        findViewById<Button>(R.id.searchBtn).setOnClickListener {
            getDataFromAPISearch()
        }

        findViewById<Button>(R.id.myLocBtn).setOnClickListener {
            getDataFromAPIMobileLocation()
        }
    }

    /**
     * This function handles the weather data that is sent back from the API.
     * The relevant information is parsed then sent to DisplaySearchActivity for display.
     */
    fun handleReceivedDataFromAPI(json: JSONObject){
        println(json)
        val intent = Intent(this, DisplaySearchActivity::class.java)
        intent.putExtra("weatherData", json.toString())
        if (cityName == null) {
            val longitude = json.getString("longitude")
            val latitude = json.getString("latitude")
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val city = address.locality ?: address.subLocality ?: address.adminArea ?: "Unknown City"
                intent.putExtra("cityName", city)
            } else {
                //If address is not available set city to Unknown City.
                intent.putExtra("cityName", "Unknown City")
            }
        }
        else {
            intent.putExtra("cityName", cityName)
            cityName = null
        }
        startActivity(intent)
    }

    /**
     * Saves the state of the application.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val cityInputTxt = findViewById<EditText>(R.id.cityInputTxt)
        outState.putString("cityInputTxt", cityInputTxt.text.toString())
    }

    /**
     * Restores the state of the application.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val cityInputTxt = savedInstanceState.getString("cityInputTxt")
        findViewById<EditText>(R.id.cityInputTxt).setText(cityInputTxt)
    }

}