package com.urwork.mobile

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.urwork.mobile.adapters.Tag
import com.urwork.mobile.adapters.TaskAdapter
import com.urwork.mobile.adapters.UserAdapter
import com.urwork.mobile.adapters.UserPhotoAdapter
import com.urwork.mobile.api.NotifApi
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.services.TinyDB

class DetailProject : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi

    lateinit var progress_tv: TextView
    lateinit var title_tv: TextView
    lateinit var desc_tv: TextView
    lateinit var count_percentage_tv: TextView
    lateinit var count_collabs_tv: TextView
    lateinit var startat_tv: TextView
    lateinit var deadline_tv: TextView
    lateinit var image1_iv: ImageView
    lateinit var image2_iv: ImageView
    lateinit var image3_iv: ImageView
    lateinit var tags_rv: RecyclerView
    lateinit var author_rv: RecyclerView
    lateinit var collabs_rv: RecyclerView
    lateinit var tasks_rv: RecyclerView

    lateinit var tagsAdapter: Tag
    lateinit var authAdapter: UserAdapter
    lateinit var collabsAdapter: UserAdapter
    lateinit var taskAdapter: TaskAdapter

    var _tags: ArrayList<String> = ArrayList()
    var _authors: ArrayList<UserModelData> = ArrayList()
    var _collabs: ArrayList<UserModelData> = ArrayList()
    var _tasks: ArrayList<TaskModelData> = ArrayList()

    lateinit var bs_collabs: BottomSheetDialog
    lateinit var bs_tasks: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_project)

        supportActionBar!!.title = "Detail"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_starring -> {
                true
            }
            R.id.action_edit_proj -> {
                true
            }
            R.id.action_delete_proj -> {
                true
            }
            R.id.action_report_proj -> {
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