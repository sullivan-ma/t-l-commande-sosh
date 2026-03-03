package com.sosh.remote

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private var decoderIp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Assignation des boutons aux codes Orange
        setupButton(R.id.btnSearch, "SCAN")
        setupButton(R.id.btnPower, "116")
        setupButton(R.id.btnHome, "139")
        setupButton(R.id.btnUp, "103")
        setupButton(R.id.btnDown, "108")
        setupButton(R.id.btnLeft, "105")
        setupButton(R.id.btnRight, "106")
        setupButton(R.id.btnOK, "28")
        setupButton(R.id.btnVolUp, "115")
        setupButton(R.id.btnVolDown, "114")
        setupButton(R.id.btnChUp, "402")
        setupButton(R.id.btnChDown, "403")
    }

    private fun setupButton(id: Int, key: String) {
        findViewById<Button>(id).setOnClickListener {
            if (key == "SCAN") discoverDecoder() else sendCommand(key)
        }
    }

    private fun discoverDecoder() {
        Toast.makeText(this, "Recherche en cours...", Toast.LENGTH_SHORT).show()
        for (i in 10..40) { // On élargit un peu la plage IP
            val testIp = "192.168.1.$i"
            val request = Request.Builder().url("http://$testIp:8080/remoteControl/cmd?operation=10").build()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        decoderIp = testIp
                        runOnUiThread { Toast.makeText(this@MainActivity, "Connecté au décodeur !", Toast.LENGTH_LONG).show() }
                    }
                }
                override fun onFailure(call: Call, e: IOException) {}
            })
        }
    }

    private fun sendCommand(key: String) {
        decoderIp?.let { ip ->
            val url = "http://$ip:8080/remoteControl/cmd?operation=01&key=$key&mode=0"
            client.newCall(Request.Builder().url(url).build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {}
            })
        } ?: Toast.makeText(this, "Appuyez sur SCAN d'abord", Toast.LENGTH_SHORT).show()
    }
}
