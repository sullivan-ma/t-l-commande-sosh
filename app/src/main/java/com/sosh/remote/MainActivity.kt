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

        // Bouton de recherche automatique
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            discoverDecoder()
        }

        // Bouton Power
        findViewById<Button>(R.id.btnPower).setOnClickListener {
            sendCommand("116") // Code 116 = Power pour Orange/Sosh
        }
    }

    private fun discoverDecoder() {
        // On teste les IPs classiques de la Livebox (192.168.1.10 à 192.168.1.25)
        for (i in 10..25) {
            val testIp = "192.168.1.$i"
            val request = Request.Builder().url("http://$testIp:8080/remoteControl/cmd?operation=10").build()
            
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        decoderIp = testIp
                        runOnUiThread { Toast.makeText(this@MainActivity, "Décodeur trouvé sur $testIp", Toast.LENGTH_SHORT).show() }
                    }
                }
                override fun onFailure(call: Call, e: IOException) {}
            })
        }
    }

    private fun sendCommand(key: String) {
        decoderIp?.let { ip ->
            val url = "http://$ip:8080/remoteControl/cmd?operation=01&key=$key&mode=0"
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {}
            })
        } ?: Toast.makeText(this, "Cherchez d'abord le décodeur !", Toast.LENGTH_SHORT).show()
    }
}
