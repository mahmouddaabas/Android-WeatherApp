package se.umu.mada0474.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.json.JSONObject
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    private lateinit var cronetEngine: CronetEngine
    private lateinit var executor: Executor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContent{
            ActionBarComposable()
        }
    }

    fun handleReceivedData(json: JSONObject){
        println(json.get("title"))
    }

    fun getWeatherData(){
        cronetEngine = CronetEngine.Builder(this).build()
        executor = Executors.newSingleThreadExecutor()
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            "https://dummyjson.com/products/1",
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
            handleReceivedData(jsonObject)

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

    /**
     * Creates and handles the applications ActionBar.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ActionBarComposable() {
        Column {
            // Your content here

            TopAppBar(
                title = { Text("WeatherApp") },
                actions = {
                    var expanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { expanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Exit") }, onClick = { exitProcess(0)}
                        )
                        DropdownMenuItem(text = { Text("Weather")}, onClick = { getWeatherData() })
                    }
                },
                navigationIcon = {

                },
            )
        }
    }
}