package com.urwork.mobile.pagers

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.urwork.mobile.R
import com.urwork.mobile.adapters.TaskAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.TaskApi
import com.urwork.mobile.models.TaskModel
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Tasks : Fragment() {
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
    lateinit var loadmore_btn: Button
    lateinit var task_type_rg: RadioGroup

    lateinit var prefs: TinyDB
    lateinit var TaskServ: TaskApi

    lateinit var result_rv: RecyclerView
    lateinit var taskAdapter: TaskAdapter

    var tasks: ArrayList<TaskModelData> = ArrayList()
    var search_hasNextPage = false
    var search_value: String = ""
    var search_page: Int = 1

    lateinit var placeholder_iv: ImageView
    lateinit var placeholder_tv: TextView

    var MODE: String = "ONGOING"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        val actionbar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar

        if (actionbar != null) {
            actionbar.show()
            actionbar.title = "My Tasks"
            actionbar.setDisplayShowHomeEnabled(false)
        }

        val v: View = inflater.inflate(R.layout.tasks, container, false)

        placeholder_iv = v.findViewById(R.id.placeholder_image)
        placeholder_tv = v.findViewById(R.id.placeholder_msg)

        prefs = TinyDB(requireContext())
        TaskServ = ApiBuilder.buildService(
            TaskApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        swipe_refresh = v.findViewById(R.id.swiperefresh)
        loadmore_btn = v.findViewById(R.id.load_more)
        task_type_rg = v.findViewById(R.id.task_type)

        taskAdapter = TaskAdapter(requireContext(), false, tasks, false, true)

        result_rv = v.findViewById(R.id.result)
        result_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        result_rv.setHasFixedSize(true)
        result_rv.isNestedScrollingEnabled = false
        result_rv.adapter = taskAdapter

        swipe_refresh.setOnRefreshListener { }

        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener {
            if (search_hasNextPage) {
                search_page += 1
                getTasks()
            }
        }

        taskAdapter.setOnItemClickListener(object: TaskAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("SKIP", "none")
            }
        })

        task_type_rg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.task_type_ongoing -> MODE = "ONGOING"
                R.id.task_type_done -> MODE = "DONE"
            }

            search_page = 1
            tasks.clear()
            taskAdapter.filterList(tasks)

            getTasks()
        }

        swipe_refresh.setOnRefreshListener {
            search_page = 1
            search_value = ""
            tasks.clear()
            taskAdapter.filterList(tasks)

            getTasks()
        }

        getTasks()

        return v
    }

    private fun getTasks() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(
            requireContext(),
            TaskServ.myTasks(MODE.lowercase(), search_value, search_page)
        ) { res, code, err ->
            placeholder_tv.isVisible = true
            placeholder_iv.isVisible = true

            if (code == 200 && res != null) {
                res.data?.forEach { d -> tasks.add(d) }

                taskAdapter.filterList(tasks)

                loadmore_btn.isVisible = res.nextPage != null
                search_hasNextPage = res.nextPage != null

                placeholder_tv.isVisible = res.data?.isEmpty() == true
                placeholder_iv.isVisible = res.data?.isEmpty() == true
            }

            swipe_refresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            tasks.clear()
                            search_value = query
                            getTasks()
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Tasks().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}