package com.urwork.mobile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.urwork.mobile.adapters.*
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.api.TaskApi
import com.urwork.mobile.models.*
import com.urwork.mobile.services.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.internal.concurrent.Task
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditProject : AppCompatActivity() {
    lateinit var swipe_refresh: SwipeRefreshLayout
    lateinit var task_loader: ProgressBar
    lateinit var image_loader: ProgressBar

    lateinit var title_et: EditText
    lateinit var desc_et: EditText
    lateinit var duration_start_tv: TextView
    lateinit var duration_end_tv: TextView
    lateinit var collaborators_rv: RecyclerView
    lateinit var tags_rv: RecyclerView
    lateinit var tasks_rv: RecyclerView
    lateinit var images_rv: RecyclerView

    var tags: ArrayList<String> = ArrayList()
    var imagess: ArrayList<String> = ArrayList()
    var collaborators: ArrayList<UserModelData> = ArrayList()
    var tasks: ArrayList<TaskModelData> = ArrayList()

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi
    lateinit var TaskServ: TaskApi
    lateinit var UserServ: AuthApi

    lateinit var collab_adapter: UserPhotoAdapter
    lateinit var tags_adapter: Tag
    lateinit var task_adapte: TaskAdapter
    lateinit var images_adapter: ImageListAdapter

    //    MODE
    var mode: String = "CREATE"
    var _id: String? = null
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    //    BOTTOMSHEETS
    lateinit var add_collabs_btn: TextView
    lateinit var add_tags_btn: TextView
    lateinit var add_task_btn: TextView
    lateinit var add_image_btn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_project)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initialize()
        setupRecylerViews()

        getData()

        setupListeners()
        BottomSheet_addCollaborators()
        BottomSheet_addTags()
        BottomSheet_addTask()
    }

    private fun getData() {
        _id = intent.getStringExtra("PROJECT_ID")

        supportActionBar!!.title = if (_id == null) "Create Project" else "Edit Project"

        if (_id != null) {
            mode = "EDIT"
            getProject()
            getTasks()
        }
    }

    private fun deleteTask(pos: Int) {
        task_loader.isVisible = true

        ApiEnqueue.enqueue(this@EditProject, TaskServ.deleteTask(_id, tasks[pos].Id))
        { res, code, err ->
            getTasks()
            if (res != null) {
                Toast.makeText(this@EditProject, res.msg, Toast.LENGTH_SHORT).show()
            }

            task_loader.isVisible = false
        }
    }

    private fun createTask(title: String) {
        task_loader.isVisible = true

        val body: CreateTaskModel = CreateTaskModel(title, "yellow")
        ApiEnqueue.enqueue(this@EditProject, TaskServ.createTask(_id, body))
        { res, code, err ->
            if (res != null && code == 200) {
                getTasks()
                Toast.makeText(this@EditProject, res.msg, Toast.LENGTH_SHORT).show()
            }

            task_loader.isVisible = false
        }
    }

    private fun getTasks() {
        task_loader.isVisible = true

        ApiEnqueue.enqueue(this@EditProject, TaskServ.getTasksInProject(_id))
        { res, code, err ->
            if (code == 200 && res != null) {
                tasks = res.data!!
                task_adapte.filterList(tasks)
            }

            task_loader.isVisible = false
        }
    }

    private fun getProject() {
        swipe_refresh.isRefreshing = true
        image_loader.isVisible = true

        ApiEnqueue.enqueue(this@EditProject, ProjServ.getProject(_id))
        { res, code, err ->
            if (res != null && code == 200) {
                title_et.setText(res.data?.title)
                desc_et.setText(res.data?.description)
                duration_start_tv.text = res.data?.durationStart?.let {
                    formatDate(
                        it,
                        "yyyy-MM-dd"
                    )
                }
                duration_end_tv.text = res.data?.durationEnd?.let { formatDate(it, "yyyy-MM-dd") }

                if (res.data?.tags != "") {
                    tags = ArrayList(res.data?.tags?.split(",")!!)
                }

                collaborators = res.data?.collaborators as ArrayList<UserModelData>
                imagess = res.data?.images as ArrayList<String>

                tags_adapter.filterList(tags)
                collab_adapter.filterList(collaborators)
                images_adapter.filterList(imagess)
            }

            swipe_refresh.isRefreshing = false
            image_loader.isVisible = false

        }
    }

    private fun initialize() {
        prefs = TinyDB(this@EditProject)
        ProjServ = ApiBuilder.buildService(
            ProjectApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        TaskServ = ApiBuilder.buildService(
            TaskApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        UserServ = ApiBuilder.buildService(
            AuthApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        swipe_refresh = findViewById(R.id.swiperefresh)
        task_loader = findViewById(R.id.task_loaderr)
        task_loader.isVisible = false
        image_loader = findViewById(R.id.image_loaderr)
        image_loader.isVisible = false
        title_et = findViewById(R.id.project_title)
        desc_et = findViewById(R.id.project_desc)
        duration_start_tv = findViewById(R.id.project_duration_start)
        duration_end_tv = findViewById(R.id.project_duration_end)
        collaborators_rv = findViewById(R.id.project_collaborators)
        tags_rv = findViewById(R.id.project_tags)
        tasks_rv = findViewById(R.id.project_tasks)
        images_rv = findViewById(R.id.project_images)


        add_collabs_btn = findViewById(R.id.project_collaborators_add)
        add_tags_btn = findViewById(R.id.project_tags_add)
        add_task_btn = findViewById(R.id.project_tasks_add)
        add_image_btn = findViewById(R.id.project_images_add)
    }

    private fun setupListeners() {
        swipe_refresh.setOnRefreshListener { getData() }

        duration_start_tv.setOnClickListener { setDateRange() }

        duration_end_tv.setOnClickListener { setDateRange() }

        collab_adapter.setOnItemClickListener(object : UserPhotoAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                collaborators.removeAt(position)
                collab_adapter.filterList(collaborators)
            }
        })

        tags_adapter.setOnItemClickListener(object : Tag.onItemClickListener {
            override fun onItemClick(position: Int) {
                tags.removeAt(position)
                tags_adapter.filterList(tags)
            }
        })

        task_adapte.setOnItemClickListener(object : TaskAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                deleteTask(position)
            }
        })

        images_adapter.setOnItemClickListener(object : ImageListAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                removeImage(position)
            }
        })

        add_image_btn.setOnClickListener {
            if (_id == null) {
                Toast.makeText(
                    this@EditProject,
                    "Add image only work in edit project",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (imagess.size == 4) {
                Toast.makeText(this@EditProject, "Image exceeds the limit of 4", Toast.LENGTH_SHORT)
                    .show()

                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data

            if (selectedImage != null) {
                val image = FileUtils.getFile(this@EditProject, data.data)
                addImage(image)
            }
        }
    }

    private fun addImage(file: File) {
        var requestBody = file.asRequestBody(getMimeType(file)?.toMediaTypeOrNull());
        var body = MultipartBody.Part.createFormData("image", file.name, requestBody)

        image_loader.isVisible = true

        ApiEnqueue.enqueue(this@EditProject, ProjServ.addImage(_id, body))
        { res, code, err ->
            if (res != null && code == 200) {
                getProject()
            }
            image_loader.isVisible = false
        }
    }

    private fun removeImage(pos: Int) {
        image_loader.isVisible = true

        ApiEnqueue.enqueue(
            this@EditProject,
            ProjServ.removeImage(_id, removeImageModel(imagess[pos]))
        )
        { res, code, err ->
            if (res != null && code == 200) {
                getProject()
            }

            image_loader.isVisible = false
        }
    }

    private fun setDateRange() {
        val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        val picker = datePickerBuilder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            duration_start_tv.text = dateFormat.format(selection.first)
            duration_end_tv.text = dateFormat.format(selection.second)
        }

        picker.show(supportFragmentManager, picker.toString())
    }

    @SuppressLint("MissingInflatedId")
    private fun BottomSheet_addCollaborators() {
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_add_collabs, null)
        val bs: BottomSheetDialog = BottomSheetDialog(this@EditProject)
        bs.setContentView(v)

        bs.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        add_collabs_btn.setOnClickListener {
            bs.show()
        }

        val btn_close: ImageButton = v.findViewById(R.id.close_btn)
        btn_close.setOnClickListener {
            bs.dismiss()
        }

        val search_et: EditText = v.findViewById(R.id.search_input)
        val loader_this: ProgressBar = v.findViewById(R.id.loaderr)
        val result_rv: RecyclerView = v.findViewById(R.id.search_result)
        val result_users: ArrayList<UserModelData> = ArrayList()

        val result_adapter: UserAdapter = UserAdapter(this@EditProject, result_users, false)
        result_rv.layoutManager =
            LinearLayoutManager(this@EditProject, LinearLayoutManager.VERTICAL, false)
        result_rv.setHasFixedSize(true)
        result_rv.adapter = result_adapter

        loader_this.isVisible = false

        search_et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                loader_this.isVisible = true

                ApiEnqueue.enqueue(
                    this@EditProject,
                    UserServ.getUsers(search_et.text.toString(), 1, 7)
                ) { res, code, err ->
                    if (res != null && code == 200) {
                        result_users.clear()

                        res.data?.forEach { d -> result_users.add(d) }

                        result_adapter.filterList(result_users)
                    }
                    loader_this.isVisible = false
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        result_adapter.setOnItemClickListener(object : UserAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                if (!collaborators.contains(result_users.get(position))) {
                    collaborators.add(result_users.get(position))
                    collab_adapter.filterList(collaborators)

                    search_et.text = null
                    result_users.clear()
                    result_adapter.filterList(result_users)
                    bs.dismiss()
                } else {
                    Toast.makeText(this@EditProject, "User already added", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun BottomSheet_addTask() {
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_add_task, null)
        val bs: BottomSheetDialog = BottomSheetDialog(this@EditProject)
        bs.setContentView(v)

        bs.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        add_task_btn.setOnClickListener {
            if (_id == null)
                Toast.makeText(
                    this@EditProject,
                    "Add task only work in edit project",
                    Toast.LENGTH_SHORT
                ).show()
            else
                bs.show()
        }

        val btn_close: ImageButton = v.findViewById(R.id.close_btn)
        btn_close.setOnClickListener {
            bs.dismiss()
        }

        val input_et: EditText = v.findViewById(R.id.new_task_input)
        val input_add_btn: TextView = v.findViewById(R.id.new_task_add_btn)

        input_add_btn.setOnClickListener {
            val value: String = input_et.text.toString()

            if (value != "") {
                bs.dismiss()
                input_et.setText("")
                createTask(value)
            }
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun BottomSheet_addTags() {
        val v: View = layoutInflater.inflate(R.layout.bottom_sheet_add_tag, null)
        val bs: BottomSheetDialog = BottomSheetDialog(this@EditProject)
        bs.setContentView(v)

        bs.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        add_tags_btn.setOnClickListener {
            bs.show()
        }

        val btn_close: ImageButton = v.findViewById(R.id.close_btn)
        btn_close.setOnClickListener {
            bs.dismiss()
        }

        val input_et: EditText = v.findViewById(R.id.new_tag_input)
        val input_add_btn: TextView = v.findViewById(R.id.new_tag_add_btn)

        input_add_btn.setOnClickListener {
            val value: String = input_et.text.toString()

            if (value != "") {
                if (!tags.contains(value)) {
                    tags.add(value)
                    tags_adapter.filterList(tags)

                    input_et.text = null
                    bs.dismiss()
                } else {
                    Toast.makeText(this@EditProject, "Tag already added", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecylerViews() {
        tags_adapter = Tag(this@EditProject, tags)
        tags_rv.layoutManager =
            LinearLayoutManager(this@EditProject, LinearLayoutManager.HORIZONTAL, false)
        tags_rv.adapter = tags_adapter

        task_adapte = TaskAdapter(this@EditProject, true, tasks, true)
        tasks_rv.layoutManager =
            LinearLayoutManager(this@EditProject, LinearLayoutManager.VERTICAL, false)
        tasks_rv.adapter = task_adapte

        collab_adapter = UserPhotoAdapter(this@EditProject, true, collaborators)
        collaborators_rv.layoutManager =
            LinearLayoutManager(this@EditProject, LinearLayoutManager.HORIZONTAL, false)
        collaborators_rv.adapter = collab_adapter

        images_adapter =
            ImageListAdapter(this@EditProject, imagess, true, R.drawable.ic_round_image_upload_24)
        images_rv.layoutManager = GridLayoutManager(this@EditProject, 2)
        images_rv.adapter = images_adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveProj()
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

    private fun saveProj() {
        val _title = title_et.text.toString()
        val _desc = desc_et.text.toString()
        val _tags = tags
        val _collaborators = ArrayList(collaborators.map { it.Id })
        val _start = duration_start_tv.text.toString()
        val _end = duration_end_tv.text.toString()

        if (_start == "yyyy-MM-dd" || _end == "yyyy-MM-dd" || _title == "" || _desc == "") {
            Toast.makeText(this@EditProject, "Some field cannot be blank", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val body = CreateProjectModel(_title, _desc, _collaborators, _tags, _start, _end)

        swipe_refresh.isRefreshing = true

//        NEED TEST!!!
        ApiEnqueue.enqueue(
            this@EditProject, ProjServ.createProject(body)
        ) { res, code, err ->

            if (res != null && code == 200) {
                if (res.data != null) {
                    Toast.makeText(
                        this@EditProject,
                        "New project added succes!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@EditProject, EditProject::class.java)
                    intent.putExtra("PROJECT_ID", res.data?.Id)

                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this@EditProject, res?.msg, Toast.LENGTH_SHORT).show()
            }

            swipe_refresh.isRefreshing = false
        }
//        NEED TEST!!!

    }
}