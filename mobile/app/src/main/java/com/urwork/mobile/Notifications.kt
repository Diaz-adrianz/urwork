package com.urwork.mobile

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.urwork.mobile.adapters.NotifAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.NotifApi
import com.urwork.mobile.models.NotifModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB

class Notifications: AppCompatActivity(){

    lateinit var swipe_refresh: SwipeRefreshLayout
    lateinit var loadmore_btn: Button

    lateinit var prefs: TinyDB
    lateinit var NotifServ: NotifApi

    lateinit var result_rv: RecyclerView
    lateinit var notifAdapter: NotifAdapter

    var notifs: ArrayList<NotifModelData> = ArrayList()
     var hasNextPage: Boolean = false
    var page: Int = 1

    lateinit var placeholder_iv: ImageView
    lateinit var placeholder_tv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications)

        supportActionBar!!.title = "Notifications"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        initializeVars()
        setupListener()

        initialState()
        getNotifs()
    }

    private fun initializeVars() {
        prefs = TinyDB(this@Notifications)
        NotifServ = ApiBuilder.buildService(
            NotifApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        notifAdapter = NotifAdapter(this@Notifications, notifs)

        placeholder_iv = findViewById(R.id.placeholder_image)
        placeholder_tv = findViewById(R.id.placeholder_msg)

        swipe_refresh = findViewById(R.id.swiperefresh)
        loadmore_btn = findViewById(R.id.load_more)

        result_rv = findViewById(R.id.result_notifs)
        result_rv.layoutManager = LinearLayoutManager(this@Notifications, LinearLayoutManager.VERTICAL, false)
        result_rv.setHasFixedSize(true)
        result_rv.adapter = notifAdapter
    }

    private fun setupListener() {
        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener{
            if (hasNextPage) {
                page += 1
            }
        }

        notifAdapter.setOnItemClickListener(object: NotifAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(this@Notifications, notifs.get(position).title, Toast.LENGTH_SHORT).show()
            }
        })

        swipe_refresh.setOnRefreshListener {
            initialState()
            getNotifs()
        }
    }

    private fun initialState() {
        notifs.clear()
        notifAdapter.filterList(notifs)

        hasNextPage = false
        page = 1
    }

    private fun getNotifs() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@Notifications, NotifServ.myNotifs()) {res, code, err ->
            placeholder_tv.text = res?.msg ?: "Something wrong"
            placeholder_tv.isVisible = true
            placeholder_iv.isVisible = true

            if (code == 200 && res != null) {
                res.data?.forEach { d -> notifs.add(d) }

                notifAdapter.filterList(notifs)

                loadmore_btn.isVisible = res.nextPage != null
                hasNextPage = res.nextPage != null

                placeholder_tv.isVisible = res.data?.isEmpty() == true
                placeholder_iv.isVisible = res.data?.isEmpty() == true
            }

            swipe_refresh.isRefreshing = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}