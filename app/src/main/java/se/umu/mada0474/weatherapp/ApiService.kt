package se.umu.mada0474.weatherapp

import android.location.Address
import android.location.Geocoder
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.json.JSONObject
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ApiService {

    private var mainActivity: MainActivity
    private lateinit var cronetEngine: CronetEngine
    private lateinit var executor: Executor
    private lateinit var geocoder: Geocoder

    constructor(mainActivity: MainActivity){
         this.mainActivity = mainActivity;
     }

    fun getCoordinates(locationName: String): List<Address>? {
        geocoder = Geocoder(mainActivity)
        val addressList: List<Address>
        addressList = geocoder.getFromLocationName(locationName, 1)!!;
        if(addressList != null){
            return addressList;
        }
        else {
            return null;
        }
    }

    fun getWeatherData(latitude: Double, longitude: Double){
        cronetEngine = CronetEngine.Builder(mainActivity).build()
        executor = Executors.newSingleThreadExecutor()
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true",
            MyUrlRequestCallback(),
            executor
        )

        val request: UrlRequest = requestBuilder.build()
        request.start();
    }

    private inner class MyUrlRequestCallback : UrlRequest.Callback() {
        override fun onRedirectReceived(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            newLocationUrl: String?
        ) {
            request?.followRedirect()
        }

        override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
            request?.read(ByteBuffer.allocateDirect(102400))
        }

        override fun onReadCompleted(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            byteBuffer: ByteBuffer?
        ) {
            byteBuffer?.flip()

            val data = ByteArray(byteBuffer?.remaining() ?: 0)
            byteBuffer?.get(data)

            val responseData = String(data, Charsets.UTF_8)

            val jsonObject = JSONObject(responseData)
            mainActivity.handleReceivedDataFromAPI(jsonObject);

            byteBuffer?.clear()

            request?.read(byteBuffer)
        }

        override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {

        }

        override fun onFailed(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            error: CronetException?
        ) {
            // Request failed, handle the error
        }
    }

}