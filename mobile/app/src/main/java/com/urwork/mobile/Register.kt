package com.urwork.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.services.ApiEnqueue

class Register : AppCompatActivity() {
    lateinit var _firstname_et: EditText
    lateinit var _lastname_et: EditText
    lateinit var _email_et: EditText
    lateinit var _password_et: EditText
    lateinit var _confpassword_et: EditText

    lateinit var _regis_btn: Button
    lateinit var _gauth_btn: Button
    lateinit var _gologin_btn: TextView

    lateinit var AuthServ: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        supportActionBar?.hide()

        AuthServ = ApiBuilder.buildService(AuthApi::class.java, null)

        _firstname_et = findViewById(R.id.regis_input_firstname)
        _lastname_et = findViewById(R.id.regis_input_lastname)
        _email_et = findViewById(R.id.regis_input_email)
        _password_et = findViewById(R.id.regis_input_password)
        _confpassword_et = findViewById(R.id.regis_input_confpassword)

        _regis_btn = findViewById(R.id.regis_go_btn)
        _gauth_btn = findViewById(R.id.login_btn_google)
        _gologin_btn = findViewById(R.id.regis_btn_haveaccount)

        _gologin_btn.setOnClickListener {
            startActivity(Intent(this@Register, Login::class.java))
            finish()
        }

        _regis_btn.setOnClickListener {
            regisEmailPassword()
        }
    }

    fun regisEmailPassword() {
        val _firstname = _firstname_et.text.toString()
        val _lastname = _lastname_et.text.toString()
        val _email = _email_et.text.toString()
        val _password = _password_et.text.toString()
        val _confpass = _confpassword_et.text.toString()

        if (_password != _confpass) {
            Toast.makeText(this@Register, "Recheck your password", Toast.LENGTH_SHORT).show()
            return
        }

        val body = UserModelData()
        body.firstName = _firstname
        body.lastName = _lastname
        body.email = _email
        body.password = _password

        ApiEnqueue.enqueue(
            this,
            AuthServ.signup(body),
            true
        ) { res, code, err ->
            if (res != null && code == 200) {
                Toast.makeText(this@Register, res.msg,  Toast.LENGTH_SHORT ).show()

                startActivity(Intent(this@Register, Login::class.java))
                finish()
            }
        }
    }

}