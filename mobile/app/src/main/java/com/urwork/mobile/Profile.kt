package com.urwork.mobile

import android.R.attr
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
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
import com.urwork.mobile.models.ProjectModelList
import com.urwork.mobile.models.UserModel
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.FileUtils
import com.urwork.mobile.services.TinyDB
import com.urwork.mobile.services.getMimeType
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import java.io.File


class Profile : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout
    lateinit var loadmore_btn: Button

    lateinit var name_tv: TextView
    lateinit var about_tv: TextView
    lateinit var photo_iv: ImageView

    var gosetting_btn: MenuItem? = null

    lateinit var prefs: TinyDB
    lateinit var AuthServ: AuthApi
    lateinit var ProjServ: ProjectApi

    lateinit var projects_rv: RecyclerView
    lateinit var projectsAdapter: Project1

    var userId: String? = ""

    var projects: ArrayList<ProjectModelData> = ArrayList()
    var projectsPage: Int = 1
    var project_hasNextPage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        supportActionBar!!.title = "Profile";
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        userId = intent.getStringExtra("USER_ID")

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
        loadmore_btn = findViewById(R.id.projects_more)

        projects_rv = findViewById(R.id.profil_projects)
        projects_rv.layoutManager =
            LinearLayoutManager(this@Profile, LinearLayoutManager.VERTICAL, false)
        projectsAdapter =
            Project1(this, false, projects)
        projects_rv.adapter = projectsAdapter

        projectsAdapter.setOnItemClickListener(object : Project1.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e("CLICKED", projects.get(position).title.toString())
            }
        })

        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener {
            if (project_hasNextPage) {
                projectsPage += 1
                getProjects()
            }
        }

        photo_iv.setOnClickListener {
            if (userId == null || userId == "") {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }
        }

        getProfileData()

        swipe_refresh.setOnRefreshListener {
            projects.clear()
            projectsPage = 1

            getProfileData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data

            if (selectedImage != null) {
                val image = FileUtils.getFile(this@Profile, data.data)
                changePhoto(image)
            }
        }
    }

    fun changePhoto(file: File) {
        var requestBody = file.asRequestBody(getMimeType(file)?.toMediaTypeOrNull());
        var body = MultipartBody.Part.createFormData("photo", file.name, requestBody)

        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(this@Profile, AuthServ.setPhoto(body))
        { res, code, err ->
            if (res != null && code == 200) {
                getProfileData()

            }
            swipe_refresh.isRefreshing = false
        }
    }

    fun getProjects() {
        var calling: Call<ProjectModelList> = ProjServ.myProjects(projectsPage)

        if (userId != null) {
            calling = ProjServ.getProjects(projectsPage, "", "", "", userId!!)
        }

        ApiEnqueue.enqueue(this@Profile, calling) { res, code, err ->
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

        var calling: Call<UserModel> = AuthServ.userinfo()

        if (userId != null) {
            calling = AuthServ.userinfo(userId!!)
        }

        ApiEnqueue.enqueue(this@Profile, calling) { res, code, err ->
            if (code == 200 && res != null) {
                name_tv.text = res.data?.firstName + " " + res.data?.lastName
                about_tv.text = res.data?.about

                Glide
                    .with(this@Profile)
                    .load(res.data?.photo)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.blank_profilepic)
                    .centerCrop()
                    .into(photo_iv);

                if (userId != null) {
                    prefs.putString("first_name", res.data?.firstName)
                    prefs.putString("photo", res.data?.photo)
                }
            }

            getProjects()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            gosetting_btn = menu.findItem(R.id.action_setting)

            if (userId != null) {
                menu.removeItem(R.id.action_setting)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                if (userId == null) {
                    startActivity(Intent(this@Profile, MyAccount::class.java))
                }

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