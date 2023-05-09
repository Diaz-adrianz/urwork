package com.urwork.mobile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.urwork.mobile.adapters.Tag
import com.urwork.mobile.adapters.TaskAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.api.TaskApi
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.services.TinyDB
import okhttp3.internal.concurrent.Task

class EditProject : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout

    lateinit var title_et: EditText
    lateinit var desc_et: EditText
    lateinit var duration_start_tv: TextView
    lateinit var duration_end_tv: TextView
    lateinit var collaborators_rv: RecyclerView
    lateinit var tags_rv: RecyclerView
    lateinit var tasks_rv: RecyclerView
    lateinit var images_1_iv: ImageView
    lateinit var images_2_iv: ImageView
    lateinit var images_3_iv: ImageView

    var tags: ArrayList<String> = ArrayList()
    var collaborators: ArrayList<UserModelData> = ArrayList()
    var tasks: ArrayList<TaskModelData> = ArrayList()

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi
    lateinit var TaskServ: TaskApi

    //    lateinit var Collab_adapte:
    lateinit var tags_adapter: Tag
    lateinit var task_adapte: TaskAdapter

//    MODE
    var mode: String = "CREATE"
    var _id: String? = null

//    BOTTOMSHEETS
    lateinit var add_collabs_btn: TextView
    lateinit var add_tags_btn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_project)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initialize()
        getData()

        setupRecylerViews()
        BottomSheet_addCollaborators()
        BottomSheet_addTags()
    }

    private fun getData() {
        _id = intent.getStringExtra("PROJECT_ID")

        supportActionBar!!.title = if (_id == null) "Create Project" else  "Edit Project"

    }

    private fun initialize() {
        prefs = TinyDB(this@EditProject)
        ProjServ = ApiBuilder.buildService(ProjectApi::class.java, prefs.getString(R.string.tokenname.toString()))
        TaskServ = ApiBuilder.buildService(TaskApi::class.java, prefs.getString(R.string.tokenname.toString()))

        swipe_refresh = findViewById(R.id.swiperefresh)
        title_et = findViewById(R.id.project_title)
        desc_et = findViewById(R.id.project_desc)
        duration_start_tv = findViewById(R.id.project_duration_start)
        duration_end_tv = findViewById(R.id.project_duration_end)
        collaborators_rv = findViewById(R.id.project_collaborators)
        tags_rv = findViewById(R.id.project_tags)
        tasks_rv = findViewById(R.id.project_tasks)
        images_1_iv = findViewById(R.id.project_images_1)
        images_2_iv = findViewById(R.id.project_images_2)
        images_3_iv = findViewById(R.id.project_images_3)

        add_collabs_btn = findViewById(R.id.project_collaborators_add)
        add_tags_btn = findViewById(R.id.project_tags_add)
    }

    private fun BottomSheet_addCollaborators() {
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_add_collabs,null)
        val bs: BottomSheetDialog = BottomSheetDialog(this@EditProject)
        bs.setContentView(v)

        add_collabs_btn.setOnClickListener{
            bs.show()
        }

        val btn_close: ImageButton = v.findViewById(R.id.close_btn)
        btn_close.setOnClickListener{
            bs.dismiss()
        }
    }

    private fun BottomSheet_addTags() {
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_add_tag,null)
        val bs: BottomSheetDialog = BottomSheetDialog(this@EditProject)
        bs.setContentView(v)

        add_tags_btn.setOnClickListener{
            bs.show()
        }

        val btn_close: ImageButton = v.findViewById(R.id.close_btn)
        btn_close.setOnClickListener{
            bs.dismiss()
        }
    }

    private fun setupRecylerViews() {
        tags_adapter = Tag(this@EditProject, tags)
        tags_rv.layoutManager = LinearLayoutManager(this@EditProject, LinearLayoutManager.HORIZONTAL, false)
        tags_rv.adapter = tags_adapter

        task_adapte = TaskAdapter(this@EditProject, true, tasks)
        tasks_rv.layoutManager = LinearLayoutManager(this@EditProject, LinearLayoutManager.VERTICAL, false)
        tasks_rv.adapter = task_adapte
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
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