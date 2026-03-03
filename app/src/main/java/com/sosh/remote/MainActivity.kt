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

        // Configuration de tous les boutons
        val buttons = mapOf(
            R.id.btnSearch to "SCAN", R.id.btnPower to "116", R.id.btnMute to "113",
            R.id.btnUp to "103", R.id.btnDown to "108", R.id.btnLeft to "105",
            R.id.btnRight to "106", R.id.btnOK to "28", R.id.btnBack to "158",
            R.id.btnHome to "139", R.id.btnVolUp to "115", R.id.btnVolDown to "114",
            R.id.btnChUp to "402", R.id.btnChDown to "403", R.id.btnRewind to "168",
            R.id.btnPlayPause to "164", R.id.btnForward to "159",
            R.id.btn0 to "512", R.id.btn1 to "513", R.id.btn2 to "514",
            R.id.btn3 to "515", R.id.btn4 to "516", R.id.btn5 to "517",
            R.id.btn6 to "518", R.id.btn7 to "519", R.id.btn8 to "520", R.id.btn9 to "521"
        )

        buttons.forEach { (id, code) ->
            findViewById<Button>(id).setOnClickListener {
                if (code == "SCAN") discoverDecoder() else sendCommand(code)
            }
        }
    }

    private fun discoverDecoder() {
        Toast.makeText(this, "Recherche du décodeur...", Toast.LENGTH_SHORT).show()
        for (i in 10..50) {
            val testIp = "192.168.1.$i"
            val request = Request.Builder().url("http://$testIp:8080/remoteControl/cmd?operation=10").build()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        decoderIp = testIp
                        runOnUiThread { Toast.makeText(this@MainActivity, "Connecté !", Toast.LENGTH_SHORT).show() }
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
        } ?: Toast.makeText(this, "Cliquez sur SCAN", Toast.LENGTH_SHORT).show()
    }
}
