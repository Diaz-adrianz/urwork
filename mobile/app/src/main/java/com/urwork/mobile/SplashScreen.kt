package com.urwork.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB

class SplashScreen : AppCompatActivity() {
    lateinit var prefs: TinyDB
    lateinit var AuthServ: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        supportActionBar?.hide()

        prefs = TinyDB(this@SplashScreen)
        AuthServ = ApiBuilder.buildService(AuthApi::class.java, prefs.getString(R.string.tokenname.toString()))

        Handler(Looper.getMainLooper()).postDelayed({
            ApiEnqueue.enqueue(this, AuthServ.userinfo()) { res, code, err ->
                Log.e("CEK AUTH", res.toString())

                if (code == 200) {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashScreen, Login::class.java))
                }
                finish()
            }
        }, 2000)

    }
}