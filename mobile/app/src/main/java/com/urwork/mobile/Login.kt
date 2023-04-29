package com.urwork.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB

class Login : AppCompatActivity() {
    lateinit var _email_et: EditText
    lateinit var _password_et: EditText
    lateinit var _login_btn: Button
    lateinit var _gauth_btn: Button

    lateinit var AuthServ: AuthApi
    lateinit var prefs: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        supportActionBar?.hide();

        AuthServ = ApiBuilder.buildService(AuthApi::class.java, null)
        prefs = TinyDB(this@Login)

        _email_et = findViewById(R.id.login_input_email)
        _password_et = findViewById(R.id.login_input_password)
        _login_btn = findViewById(R.id.login_btn_go)
        _gauth_btn = findViewById(R.id.login_btn_google)

        _login_btn.setOnClickListener {
            loginEmailPassword()
        }
    }

    fun loginEmailPassword() {
        val _email = _email_et.text.toString()
        val _password = _password_et.text.toString()
        val body = UserModelData()
        body.email = _email
        body.password = _password

        ApiEnqueue.enqueue(
            this,
            AuthServ.signin(body)
        ) { res, code, err ->
            if (res != null && code == 200) {
                prefs.putString(R.string.tokenname.toString(), res.data?.Id)
                startActivity(Intent(this@Login, MainActivity::class.java))
                finish()
            }
        }
    }
}