package com.urwork.mobile.pagers

import android.os.Bundle
import android.view.*
import android.widget.Button
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

    lateinit var prefs: TinyDB
    lateinit var TaskServ: TaskApi

    lateinit var result_rv: RecyclerView
    lateinit var taskAdapter: TaskAdapter

    var tasks: ArrayList<TaskModelData> = ArrayList()
    var search_hasNextPage = false
    var search_value: String = ""
    var search_page: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        val actionbar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar

        if (actionbar != null) {
            actionbar.show()
            actionbar.title = "My Tasks"
        }

        val v: View = inflater.inflate(R.layout.tasks, container, false)

        prefs = TinyDB(requireContext())
        TaskServ = ApiBuilder.buildService(
            TaskApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        swipe_refresh = v.findViewById(R.id.swiperefresh)
        loadmore_btn = v.findViewById(R.id.load_more)

        taskAdapter = TaskAdapter(requireContext(), false, tasks)

        result_rv = v.findViewById(R.id.result)
        result_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        result_rv.setHasFixedSize(true)
        result_rv.adapter = taskAdapter

        swipe_refresh.setOnRefreshListener { }

        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener{
            if (search_hasNextPage) {
                search_page += 1
                getTasks()
            }
        }

        getTasks()

        return v
    }

    private fun getTasks() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(
            requireContext(),
            TaskServ.myTasks(search_value, search_page)
        ) { res, code, err ->
            if (code == 200 && res != null) {
                res.data?.forEach { d -> tasks.add(d) }

                taskAdapter.filterList(tasks)

                loadmore_btn.isVisible = res.nextPage != null
                search_hasNextPage = res.nextPage != null
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