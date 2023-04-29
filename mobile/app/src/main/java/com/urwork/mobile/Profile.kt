package com.urwork.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB

class Profile : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout
    lateinit var back_btn: ImageButton
    lateinit var logout_btn: ImageButton

    lateinit var name_tv: TextView
    lateinit var about_tv: TextView
    lateinit var photo_iv: ImageView

    lateinit var prefs: TinyDB
    lateinit var AuthServ: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        supportActionBar?.hide();

        prefs = TinyDB(this)
        AuthServ = ApiBuilder.buildService(AuthApi::class.java, prefs.getString(R.string.tokenname.toString()))

        name_tv = findViewById(R.id.profil_name)
        about_tv = findViewById(R.id.profil_about)
        photo_iv = findViewById(R.id.profil_photo)

        swipe_refresh  = findViewById(R.id.swiperefresh)
        back_btn = findViewById(R.id.back_btn)
        logout_btn = findViewById(R.id.logout_btn)

        back_btn.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        logout_btn.setOnClickListener{
            logout()
        }

        getProfileData()
    }

    fun logout() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@Profile, AuthServ.signout()) { res, code, err ->
            swipe_refresh.isRefreshing = false

            if (code == 200) {
                val goLogout = Intent(this@Profile, Login::class.java)
                goLogout.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                startActivity(goLogout)

                finishAffinity()

                prefs.remove(R.string.tokenname.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun getProfileData() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@Profile, AuthServ.userinfo()) {res, code, err ->
            if (code == 200 && res != null) {
                name_tv.text = res.data?.firstName + " " + res.data?.lastName
                about_tv.text = res.data?.about

                Glide
                    .with(this@Profile)
                    .load(res.data?.photo)
                    .placeholder(R.drawable.blank_profilepic)
                    .error(R.drawable.blank_profilepic)
                    .centerCrop()
                    .into(photo_iv);
            }

            swipe_refresh.isRefreshing = false
        }
    }
}