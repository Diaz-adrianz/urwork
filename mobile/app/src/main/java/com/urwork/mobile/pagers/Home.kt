package com.urwork.mobile.pagers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.urwork.mobile.MainActivity
import com.urwork.mobile.Notifications
import com.urwork.mobile.Profile
import com.urwork.mobile.R
import com.urwork.mobile.adapters.ProjectAdapter
import com.urwork.mobile.adapters.TaskAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.NotifApi
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.api.TaskApi
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB
import kotlinx.coroutines.*

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

    var notifcount_tv: TextView? = null

    lateinit var go_all_projects_tv: TextView
    lateinit var go_all_tasks_tv: TextView

    lateinit var projects_rv: RecyclerView
    lateinit var tasks_rv: RecyclerView

    lateinit var projectsAdapter: ProjectAdapter
    lateinit var tasksAdapter: TaskAdapter

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi
    lateinit var TaskServ: TaskApi
    lateinit var NotifsServ: NotifApi

    var projects: ArrayList<ProjectModelData> = ArrayList()
    var tasks: ArrayList<TaskModelData> = ArrayList()

    var countNotif: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val actionbar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar

        if (actionbar != null) {
            actionbar.show()
            actionbar.title = ""
            actionbar.setIcon(R.drawable.logo_dark_minii)

            actionbar.setDisplayShowHomeEnabled(true)
        }

        val v = inflater.inflate(R.layout.home, container, false)

        prefs = TinyDB(requireContext())
        TaskServ = ApiBuilder.buildService(
            TaskApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        ProjServ = ApiBuilder.buildService(
            ProjectApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        NotifsServ = ApiBuilder.buildService(
            NotifApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        go_all_projects_tv = v.findViewById(R.id.home_section_project_seeall)
        go_all_tasks_tv = v.findViewById(R.id.home_section_task_seeall)

        projects_rv = v.findViewById(R.id.home_section_project_contents)
        tasks_rv = v.findViewById(R.id.home_section_task_contents)

        swipe_refresh = v.findViewById(R.id.swiperefresh)

        tasksAdapter = TaskAdapter(requireContext(), false, tasks, false, false)
        projectsAdapter = ProjectAdapter(requireContext(), R.layout.item_project_3, projects)

        tasks_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        tasks_rv.setHasFixedSize(true)
        tasks_rv.isNestedScrollingEnabled = false
        tasks_rv.adapter = tasksAdapter

        projects_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        projects_rv.setHasFixedSize(true)
        projects_rv.adapter = projectsAdapter

        projectsAdapter.setOnItemClickListener(object : ProjectAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(requireContext(), projects.get(position).title, Toast.LENGTH_SHORT)
                    .show()
            }
        })

        tasksAdapter.setOnItemClickListener(object: TaskAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("SKIP", "none")
            }
        })

        swipe_refresh.setOnRefreshListener {
            initialState()
            getDatas()
        }

        go_all_tasks_tv.setOnClickListener {
            val parentActivity = activity as? MainActivity
            parentActivity?.loadFragment(Tasks(), R.id.nav_tasks)
        }

        go_all_projects_tv.setOnClickListener {
            val parentAct = activity as? MainActivity
            parentAct?.loadFragment(Schedule(), R.id.nav_schedule)
        }

        getDatas()

        return v
    }

    private fun initialState() {
        projects.clear()
        projectsAdapter.filterList(projects)
        tasks.clear()
        tasksAdapter.filterList(tasks)

        countNotif = 0
    }

    private fun getDatas() {
//        NEED TEST
        swipe_refresh.isRefreshing = true
        getOngoingProjects()
//        NEED TEST
    }

    private fun getMyTasks() {
        ApiEnqueue.enqueue(requireContext(), TaskServ.myTasks("ongoing")) { res, code, err ->
            if (code == 200 && res != null) {
                res.data?.forEach { d -> tasks.add(d) }

                tasksAdapter.filterList(tasks)
            }

            swipe_refresh.isRefreshing = false
        }
    }

    private fun getOngoingProjects  () {
        ApiEnqueue.enqueue(requireContext(), ProjServ.myOngoingProjects()) { res, code, err ->
            if (code == 200 && res != null) {
                res.data?.forEach { d -> projects.add(d) }
                projectsAdapter.filterList(projects)
            }

            countMyNotifs()
        }
    }

    private fun countMyNotifs() {
        ApiEnqueue.enqueue(requireContext(), NotifsServ.countMyNotifs()) { res, code, err ->
            if (code == 200 && res != null) {
                countNotif = res.data!!

                if (countNotif != 0) {
                    notifcount_tv?.text  = countNotif.toString()
                } else {
                    notifcount_tv?.isVisible = false
                }
            }

            getMyTasks()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val menuNotifItem: MenuItem = menu.findItem(R.id.action_notif)
        val menuNotifItemView: View? = menuNotifItem.actionView

        val menuAccItem: MenuItem = menu.findItem(R.id.action_account)
        val menuAccItemView: View? = menuAccItem.actionView

        if (menuNotifItemView != null && menuAccItemView != null) {
            notifcount_tv = menuNotifItemView.findViewById(R.id.notif_badge)
            photo_iv = menuAccItemView.findViewById(R.id.profil_photo)

            val photo: String? = prefs.getString("photo")

            if (photo != null) {
                Glide
                    .with(requireContext())
                    .load(photo)
                    .placeholder(R.drawable.blank_profilepic)
                    .error(R.drawable.blank_profilepic)
                    .centerCrop()
                    .into(photo_iv);
            }

            photo_iv.setOnClickListener {
                startActivity(Intent(requireContext(), Profile::class.java))
            }

            menuNotifItemView.setOnClickListener {
                startActivity(Intent(requireContext(), Notifications::class.java))
            }

        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notif -> {
                startActivity(Intent(requireContext(), Notifications::class.java))
                return false
            }
            R.id.action_account -> {
                startActivity(Intent(requireContext(), Profile::class.java))
                return false
            }
            else -> super.onOptionsItemSelected(item)
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