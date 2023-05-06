package com.urwork.mobile.pagers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.urwork.mobile.Profile
import com.urwork.mobile.R
import com.urwork.mobile.adapters.Project1
import com.urwork.mobile.adapters.TaskAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.api.TaskApi
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Home : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var swipe_refresh: SwipeRefreshLayout

    lateinit var photo_iv: ImageView
    lateinit var name_tv: TextView
    lateinit var greeting_tv: TextView

    lateinit var go_notif_ib: ImageButton
    lateinit var go_all_projects_tv: TextView
    lateinit var go_all_tasks_tv: TextView

    lateinit var projects_rv: RecyclerView
    lateinit var tasks_rv: RecyclerView

    lateinit var projectsAdapter: Project1
    lateinit var tasksAdapter: TaskAdapter

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi
    lateinit var TaskServ: TaskApi

    var projects: ArrayList<ProjectModelData> = ArrayList()
    var tasks: ArrayList<TaskModelData> = ArrayList()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()

        val v = inflater.inflate(R.layout.home, container, false)

        prefs = TinyDB(requireContext())
        TaskServ = ApiBuilder.buildService(
            TaskApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        photo_iv = v.findViewById(R.id.profil_photo)
        greeting_tv = v.findViewById(R.id.greeting)
        name_tv = v.findViewById(R.id.name)

        go_notif_ib = v.findViewById(R.id.goto_notif)
        go_all_projects_tv = v.findViewById(R.id.home_section_project_seeall)
        go_all_tasks_tv = v.findViewById(R.id.home_section_task_seeall)

        projects_rv = v.findViewById(R.id.home_section_project_contents)
        tasks_rv = v.findViewById(R.id.home_section_task_contents)

        swipe_refresh = v.findViewById(R.id.swiperefresh)

        tasksAdapter = TaskAdapter(requireContext(), false, tasks)

        tasks_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        tasks_rv.setHasFixedSize(true)
        tasks_rv.adapter = tasksAdapter

        getMyTasks()


        val name: String? = prefs.getString("first_name")
        val photo: String? = prefs.getString("photo")

        if (name != null || photo != null) {
            name_tv.text = name

            Glide
                .with(requireContext())
                .load(photo)
                .placeholder(R.drawable.blank_profilepic)
                .error(R.drawable.blank_profilepic)
                .centerCrop()
                .into(photo_iv);
        }

        photo_iv.setOnClickListener{
            startActivity(Intent(requireContext(), Profile::class.java))
        }

        name_tv.setOnClickListener{
            startActivity(Intent(requireContext(), Profile::class.java))
        }

        return v
    }

    private fun getMyTasks() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(requireContext(), TaskServ.myTasks()) { res, code, err ->
            if (code == 200 && res != null) {
                res.data?.forEach { d -> tasks.add(d) }

                tasksAdapter.filterList(tasks)
            }

            swipe_refresh.isRefreshing = false
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}