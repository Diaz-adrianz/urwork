package com.urwork.mobile;

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.models.UpdateUserModelData
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB

class MyAccount : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout
    lateinit var save_btn: Button

    lateinit var firstname_et: EditText
    lateinit var lastname_et: EditText
    lateinit var about_et: EditText
    lateinit var institution_et: EditText

    lateinit var prefs: TinyDB
    lateinit var AuthServ: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myaccount)

        supportActionBar!!.title = "Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        prefs = TinyDB(this)
        AuthServ = ApiBuilder.buildService(
            AuthApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        swipe_refresh = findViewById(R.id.swiperefresh)
        save_btn = findViewById(R.id.account_btn_save)

        firstname_et = findViewById(R.id.account_input_firstname)
        lastname_et = findViewById(R.id.account_input_lastname)
        about_et = findViewById(R.id.account_input_about)
        institution_et = findViewById(R.id.account_input_institution)

        save_btn.setOnClickListener{
            setProfileData()
        }

        swipe_refresh.setOnRefreshListener {
            getProfileData()
        }

        getProfileData()
    }

    @SuppressLint("SetTextI18n")
    fun getProfileData() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@MyAccount, AuthServ.userinfo()) { res, code, err ->
            if (code == 200 && res != null) {
                firstname_et.setText(res.data?.firstName)
                lastname_et.setText(res.data?.lastName)
                about_et.setText(res.data?.about)
                institution_et.setText(res.data?.institute)

                prefs.putString("first_name", res.data?.firstName)
                prefs.putString("photo", res.data?.photo)
            }

            swipe_refresh.isRefreshing = false
        }
    }

    fun setProfileData() {
        swipe_refresh.isRefreshing = true

        val _firstName = firstname_et.text.toString()
        val _lastName = lastname_et.text.toString()
        val _about = about_et.text.toString()
        val _intitution = institution_et.text.toString()

        val body = UpdateUserModelData(_firstName, _lastName, _about, _intitution)

        ApiEnqueue.enqueue(this@MyAccount, AuthServ.updateAccount(body)) {res, code ,err ->
            if (res != null && code == 200) {
                getProfileData()

                Toast.makeText(this@MyAccount, res.msg, Toast.LENGTH_SHORT).show()
            }

            swipe_refresh.isRefreshing = false
        }
    }

    fun logout() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@MyAccount, AuthServ.signout()) { res, code, err ->
            swipe_refresh.isRefreshing = false

            if (code == 200) {
                val goLogout = Intent(this@MyAccount, Login::class.java)
                goLogout.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                startActivity(goLogout)

                finishAffinity()

                prefs.remove(R.string.tokenname.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_account, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
