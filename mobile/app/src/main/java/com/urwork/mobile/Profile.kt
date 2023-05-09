package com.urwork.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView.RecyclerListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.urwork.mobile.adapters.Project1
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB

class Profile : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout
    lateinit var back_btn: ImageButton
    lateinit var setting_btn: ImageButton
    lateinit var loadmore_btn: Button

    lateinit var name_tv: TextView
    lateinit var about_tv: TextView
    lateinit var photo_iv: ImageView

    lateinit var prefs: TinyDB
    lateinit var AuthServ: AuthApi
    lateinit var ProjServ: ProjectApi

    lateinit var projects_rv: RecyclerView
    lateinit var projectsAdapter: Project1

    var projects: ArrayList<ProjectModelData> = ArrayList()
    var projectsPage: Int = 1
    var project_hasNextPage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        supportActionBar?.hide();

        prefs = TinyDB(this)
        AuthServ = ApiBuilder.buildService(
            AuthApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        ProjServ = ApiBuilder.buildService(
            ProjectApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        name_tv = findViewById(R.id.profil_name)
        about_tv = findViewById(R.id.profil_about)
        photo_iv = findViewById(R.id.profil_photo)

        swipe_refresh = findViewById(R.id.swiperefresh)
        back_btn = findViewById(R.id.back_btn)
        setting_btn = findViewById(R.id.profil_btn_gosetting)
        loadmore_btn = findViewById(R.id.projects_more)

        projects_rv = findViewById(R.id.profil_projects)
        projects_rv.layoutManager =
            LinearLayoutManager(this@Profile, LinearLayoutManager.VERTICAL, false)
        projects_rv.setHasFixedSize(true)
        projectsAdapter =
            Project1(this, false, projects)
        projects_rv.adapter = projectsAdapter

        projectsAdapter.setOnItemClickListener(object : Project1.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e("CLICKED", projects.get(position).title.toString())
            }
        })

        back_btn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        setting_btn.setOnClickListener {
            startActivity(Intent(this@Profile, MyAccount::class.java))
        }

        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener {
            if (project_hasNextPage) {
                projectsPage += 1
                getProjects()
            }
        }

        getProfileData()
        getProjects()

        swipe_refresh.setOnRefreshListener {
            projects.clear()
            projectsPage = 1

            getProfileData()
            getProjects()
        }
    }

    fun getProjects() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@Profile, ProjServ.myProjects(projectsPage)) { res, code, err ->
            if (code == 200 && res != null) {
                res.data?.forEach { d ->
                    projects.add(d)
                }

                projectsAdapter.filterList(projects)

                loadmore_btn.isVisible = res.nextPage != null
                project_hasNextPage = res.nextPage != null
            }

            swipe_refresh.isRefreshing = false
        }
    }

    @SuppressLint("SetTextI18n")
    fun getProfileData() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@Profile, AuthServ.userinfo()) { res, code, err ->
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

                prefs.putString("first_name", res.data?.firstName)
                prefs.putString("photo", res.data?.photo)
            }

            swipe_refresh.isRefreshing = false
        }
    }
}