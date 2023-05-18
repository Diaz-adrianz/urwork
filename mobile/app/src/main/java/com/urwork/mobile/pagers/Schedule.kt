package com.urwork.mobile.pagers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.urwork.mobile.R
import com.urwork.mobile.adapters.Project1
import com.urwork.mobile.adapters.ProjectAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB
import com.urwork.mobile.services.formatDate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Schedule : Fragment() {
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

    lateinit var datePicker_tv: TextView

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi

    lateinit var projects_rv: RecyclerView
    lateinit var projectsAdapter: ProjectAdapter

    lateinit var startDate: String
    lateinit var endDate: String

    var projects: ArrayList<ProjectModelData> = ArrayList()
    var search_page: Int = 1
    var search_hasNextPage: Boolean = false
    var search_value: String = ""

    val calendar = Calendar.getInstance()

    lateinit var placeholder_iv: ImageView
    lateinit var placeholder_tv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val actionbar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar

        if (actionbar != null) {
            actionbar.show()
            actionbar.title = "Schedule"
            actionbar.setDisplayShowHomeEnabled(false)

        }

        val v: View = inflater.inflate(R.layout.schedule, container, false)

        placeholder_iv = v.findViewById(R.id.placeholder_image)
        placeholder_tv = v.findViewById(R.id.placeholder_msg)

        prefs = TinyDB(requireContext())
        ProjServ = ApiBuilder.buildService(
            ProjectApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        swipe_refresh = v.findViewById(R.id.swiperefresh)
        loadmore_btn = v.findViewById(R.id.projects_more)
        datePicker_tv = v.findViewById(R.id.schedule_datepicker)

        projectsAdapter = ProjectAdapter(requireContext(), R.layout.item_project_2, projects)

        projects_rv = v.findViewById(R.id.projects_result)
        projects_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        projects_rv.setHasFixedSize(true)
        projects_rv.isNestedScrollingEnabled = false
        projects_rv.adapter = projectsAdapter

        projectsAdapter.setOnItemClickListener(object : ProjectAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e("CLICKED", projects.get(position).title.toString())
            }
        })

        swipe_refresh.setOnRefreshListener {
            initialState()
            getProjects()
        }

        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener {
            if (search_hasNextPage) {
                search_page += 1
            }
        }

        datePicker_tv.setOnClickListener {
            pickTheDate()
        }

        initialState()
        getProjects()

        return v
    }

    private fun initialState() {
        search_page = 1
        search_value = ""
        startDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(Calendar.DAY_OF_MONTH)
        }"
        endDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(Calendar.DAY_OF_MONTH) + 1
        }"

        datePicker_tv.text = formatDate(startDate, "dd MMMM yyyy")

        projects.clear()
        projectsAdapter.filterList(projects)
    }

    @SuppressLint("ResourceAsColor")
    private fun getProjects() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(
            requireContext(),
            ProjServ.myProjects(search_page, search_value, startDate, endDate)
        ) { res, code, err ->
            placeholder_tv.text = res?.msg ?: "Something wrong with app"
            placeholder_tv.isVisible = true
            placeholder_iv.isVisible = true

            if (code == 200 && res != null) {
                res.data?.forEach { d -> projects.add(d) }

                projectsAdapter.filterList(projects)

                loadmore_btn.isVisible = res.nextPage != null
                search_hasNextPage =  res.nextPage !=null

                placeholder_tv.isVisible = res.data?.isEmpty() == true
                placeholder_iv.isVisible = res.data?.isEmpty() == true
            }

            swipe_refresh.isRefreshing = false
        }
    }

    private fun pickTheDate() {
        DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->

                search_value = ""
                startDate = "${year}-${monthOfYear + 1}-${dayOfMonth}"
                endDate = "${year}-${monthOfYear + 1}-${dayOfMonth + 1}"

                datePicker_tv.text = formatDate(startDate, "dd MMMM yyyy")

                projects.clear()
                getProjects()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
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
                            projects.clear()
                            search_value = query
                            getProjects()
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
            Schedule().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}