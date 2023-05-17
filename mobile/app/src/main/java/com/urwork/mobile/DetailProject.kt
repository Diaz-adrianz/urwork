package com.urwork.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.urwork.mobile.adapters.Tag
import com.urwork.mobile.adapters.TaskAdapter
import com.urwork.mobile.adapters.UserAdapter
import com.urwork.mobile.adapters.UserPhotoAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.NotifApi
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.api.TaskApi
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB
import com.urwork.mobile.services.formatDate

class DetailProject : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi
    lateinit var TaskServ: TaskApi

    lateinit var progress_tv: TextView
    lateinit var title_tv: TextView
    lateinit var desc_tv: TextView
    lateinit var count_percentage_tv: TextView
    lateinit var count_collabs_tv: TextView
    lateinit var collabs_box: LinearLayout
    lateinit var tasks_box: LinearLayout
    lateinit var startat_tv: TextView
    lateinit var deadline_tv: TextView
    lateinit var image1_iv: ImageView
    lateinit var image2_iv: ImageView
    lateinit var image3_iv: ImageView
    var star_icon: ImageView? = null
    var stars_tv: TextView? = null
    lateinit var tags_rv: RecyclerView
    lateinit var author_rv: RecyclerView
    lateinit var collabs_rv: RecyclerView
    lateinit var tasks_rv: RecyclerView
    var btn_edit: MenuItem? = null
    var btn_delete: MenuItem? = null
    var btn_report: MenuItem? = null
    lateinit var options_menu: Menu

    lateinit var tagsAdapter: Tag
    lateinit var authAdapter: UserAdapter
    lateinit var collabsAdapter: UserAdapter
    lateinit var taskAdapter: TaskAdapter

    var _id: String? = ""
    var _tags: List<String> = listOf()
    var _authors: ArrayList<UserModelData> = ArrayList()
    var _collabs: ArrayList<UserModelData> = ArrayList()
    var _tasks: ArrayList<TaskModelData> = ArrayList()
    var _starCount: Int = 0
    var _stars: List<String> = listOf()

    lateinit var bs_collabs: BottomSheetDialog
    lateinit var bs_tasks: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_project)

        supportActionBar!!.title = "Detail"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        prefs = TinyDB(this@DetailProject)
        _id = intent.getStringExtra("PROJECT_ID")

        prepareWidgets()
        prepareAdapters()
        prepareListeners()

        getData()
    }

    private fun initialState() {
        _tags = listOf()
        tagsAdapter.filterList(_tags)

        _authors.clear()
        authAdapter.filterList(_authors)

        _collabs.clear()
        collabsAdapter.filterList(_collabs)

        _tasks.clear()
        taskAdapter.filterList(_tasks)

        _stars = listOf()
    }

    private fun prepareListeners() {
        tasks_box.setOnClickListener { bs_tasks.show() }
        collabs_box.setOnClickListener { bs_collabs.show() }

        authAdapter.setOnItemClickListener(object : UserAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("SKIP", _authors[position].firstName.toString())
            }
        })
        collabsAdapter.setOnItemClickListener(object : UserAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("SKIP", _collabs[position].firstName.toString())
            }
        })
        tagsAdapter.setOnItemClickListener(object : Tag.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("SKIP", _tags[position])
            }
        })
        taskAdapter.setOnItemClickListener(object: TaskAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("SKIP", "none")
            }
        })

        swipe_refresh.setOnRefreshListener {
            initialState()
            getData()
        }

        btn_edit?.setOnMenuItemClickListener{
            val intent = Intent(this@DetailProject, EditProject::class.java)
            intent.putExtra("PROJECT_ID", _id)

            startActivity(intent)
            finish()
            true
        }

        btn_delete?.setOnMenuItemClickListener {
            Toast.makeText(this@DetailProject, _id, Toast.LENGTH_SHORT).show()
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        ProjServ = ApiBuilder.buildService(
            ProjectApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        TaskServ = ApiBuilder.buildService(
            TaskApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        swipe_refresh.setRefreshing(true)

        ApiEnqueue.enqueue(this@DetailProject, ProjServ.getProject(_id)) { res, code, err ->
            if (res != null && code == 200) {
                progress_tv.text = if (res.data?.percentage == 100) "Completed" else "On progress"
                title_tv.text = res.data?.title
                desc_tv.text = res.data?.description
                count_percentage_tv.text = "${if (res.data?.percentage == null) "-" else res.data?.percentage}%"
                count_collabs_tv.text = res.data?.collaborators?.size.toString()
                startat_tv.text =
                    res.data?.durationStart?.let { formatDate(it, "EEEE, dd MMMM yyyy") }
                deadline_tv.text =
                    res.data?.durationEnd?.let { formatDate(it, "EEEE, dd MMMM yyyy") }

                if (res.data?.tags != "") {
                    _tags = res.data?.tags?.split(", ")!!
                }
                res.data?.author?.let { _authors.add(it) }
                _collabs = res.data?.collaborators as ArrayList<UserModelData>
                _stars = res.data?.stars!!

                stars_tv?.text = res.data?.stars?.size.toString()
                if (res.data?.stars?.contains(prefs.getString("user_id")) == true) {
                    star_icon?.imageTintList =
                        ContextCompat.getColorStateList(this@DetailProject, R.color.yellow)
                } else {
                    star_icon?.imageTintList =
                        ContextCompat.getColorStateList(this@DetailProject, R.color.silver)
                }

                tagsAdapter.filterList(_tags)
                authAdapter.filterList(_authors)
                collabsAdapter.filterList(_collabs)

                if (prefs.getString("user_id") != res.data?.author?.Id) {
                    options_menu.removeItem(R.id.action_delete_proj)
                    options_menu.removeItem(R.id.action_edit_proj)
                }
            }

            swipe_refresh.setRefreshing(false)
        }

        swipe_refresh.setRefreshing(true)
        ApiEnqueue.enqueue(this@DetailProject, TaskServ.getTasksInProject(_id)) { res, code, err ->
            if (code == 200 && res != null) {
                _tasks = res.data!!
                taskAdapter.filterList(_tasks)
            }

            swipe_refresh.setRefreshing(false)
        }
    }

    private fun starProjects() {
        val option: String = if (_stars.contains(prefs.getString("user_id"))) "no" else "yes"

        swipe_refresh.isRefreshing = true
        Log.e("STAR", option)
        ApiEnqueue.enqueue(this@DetailProject, ProjServ.giveStar(_id, option)) { res, code, err ->
            if (res != null && code == 200) {
                val _prevcount = stars_tv?.text.toString().toInt()
                stars_tv?.text =
                    if (option == "yes") (_prevcount + 1).toString() else (_prevcount - 1).toString()
                star_icon?.imageTintList = ContextCompat.getColorStateList(
                    this@DetailProject,
                    if (option == "yes") R.color.yellow else R.color.silver
                )
            }
            swipe_refresh.isRefreshing = false
        }
    }

    private fun prepareAdapters() {
        tagsAdapter = Tag(this@DetailProject, _tags)
        authAdapter = UserAdapter(this@DetailProject, _authors)
        collabsAdapter = UserAdapter(this@DetailProject, _collabs)
        taskAdapter = TaskAdapter(this@DetailProject, true, _tasks)

        tags_rv.adapter = tagsAdapter
        author_rv.adapter = authAdapter
        collabs_rv.adapter = collabsAdapter
        tasks_rv.adapter = taskAdapter
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    fun prepareWidgets() {
        swipe_refresh = findViewById(R.id.swiperefresh)

        progress_tv = findViewById(R.id.proj_status)
        title_tv = findViewById(R.id.proj_title)
        desc_tv = findViewById(R.id.proj_desc)
        count_percentage_tv = findViewById(R.id.proj_count_percentage)
        count_collabs_tv = findViewById(R.id.proj_count_collabs)
        collabs_box = findViewById(R.id.proj_box_collabs)
        tasks_box = findViewById(R.id.proj_box_tasks)
        startat_tv = findViewById(R.id.proj_start)
        deadline_tv = findViewById(R.id.proj_deadline)
        image1_iv = findViewById(R.id.proj_images_1)
        image2_iv = findViewById(R.id.proj_images_2)
        image3_iv = findViewById(R.id.proj_images_3)

        tags_rv = findViewById(R.id.proj_tags)
        tags_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tags_rv.setHasFixedSize(true)

        author_rv = findViewById(R.id.proj_author)
        author_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        author_rv.setHasFixedSize(true)

        val bs_collabs_v: View = layoutInflater.inflate(R.layout.bottom_sheet_simple_list, null)
        val bs_tasks_v: View = layoutInflater.inflate(R.layout.bottom_sheet_simple_list, null)

        bs_collabs = BottomSheetDialog(this@DetailProject)
        bs_tasks = BottomSheetDialog(this@DetailProject)

        bs_collabs.setContentView(bs_collabs_v)
        bs_tasks.setContentView(bs_tasks_v)

        val btn_cls_collabs_v: ImageButton = bs_collabs_v.findViewById(R.id.close_btn)
        val btn_cls_tasks_v: ImageButton = bs_tasks_v.findViewById(R.id.close_btn)
        val title_collabs_v: TextView = bs_collabs_v.findViewById(R.id.title)
        val title_tasks_v: TextView = bs_tasks_v.findViewById(R.id.title)

        collabs_rv = bs_collabs_v.findViewById(R.id.result)
        collabs_rv.layoutManager =
            LinearLayoutManager(this@DetailProject, LinearLayoutManager.VERTICAL, false)

        tasks_rv = bs_tasks_v.findViewById(R.id.result)
        tasks_rv.layoutManager =
            LinearLayoutManager(this@DetailProject, LinearLayoutManager.VERTICAL, false)

        title_collabs_v.text = "Collaborators"
        title_tasks_v.text = "Tasks progress"

        btn_cls_collabs_v.setOnClickListener {
            bs_collabs.dismiss()
        }

        btn_cls_tasks_v.setOnClickListener {
            bs_tasks.dismiss()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val menuStarItem: MenuItem = menu.findItem(R.id.action_starring)
            val menuStarItem_v : View?= menuStarItem.actionView

            options_menu = menu
            btn_edit = menu.findItem(R.id.action_edit_proj)
            btn_delete = menu.findItem(R.id.action_delete_proj)
            btn_report = menu.findItem(R.id.action_report_proj)

            if (menuStarItem_v != null) {
                stars_tv = menuStarItem_v.findViewById(R.id.star_counter)
                star_icon = menuStarItem_v.findViewById(R.id.star_icon)

                menuStarItem_v.setOnClickListener {
                    starProjects()
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
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
                val intent = Intent(this@DetailProject, EditProject::class.java)
                intent.putExtra("PROJECT_ID", _id)

                startActivity(intent)
                finish()
                true
            }
            R.id.action_delete_proj -> {
                Toast.makeText(this@DetailProject, _id, Toast.LENGTH_SHORT).show()
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