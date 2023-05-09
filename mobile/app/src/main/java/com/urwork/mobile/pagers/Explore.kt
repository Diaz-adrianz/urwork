package com.urwork.mobile.pagers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.urwork.mobile.R
import com.urwork.mobile.adapters.Project1
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Explore : Fragment() {

    // TODO: Rename and change types of parameters
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
    lateinit var explore_type_rg: RadioGroup
    lateinit var explore_type_selected_rb: RadioButton
    lateinit var loadmore_btn: Button

    lateinit var prefs: TinyDB
    lateinit var ProjServ: ProjectApi

    lateinit var projects_rv: RecyclerView
    lateinit var projectsAdapter: Project1

    var projects: ArrayList<ProjectModelData> = ArrayList()
    var search_value: String = ""
    var search_page: Int = 1
    var search_hasNextPage: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val actionbar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar

        if (actionbar != null) {
            actionbar.show()
            actionbar.title = "Explore"
        }

        val v: View = inflater.inflate(R.layout.explore, container, false)

        prefs = TinyDB(requireContext())
        ProjServ = ApiBuilder.buildService(
            ProjectApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        projectsAdapter = Project1(requireContext(), false, projects)

        swipe_refresh = v.findViewById(R.id.swiperefresh)
        explore_type_rg = v.findViewById(R.id.explore_type)
        loadmore_btn = v.findViewById(R.id.projects_more)

        projects_rv = v.findViewById(R.id.projects_result)
        projects_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        projects_rv.setHasFixedSize(true)
        projects_rv.adapter = projectsAdapter

        projectsAdapter.setOnItemClickListener(object : Project1.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e("CLICKED", projects.get(position).title.toString())
            }
        })

        swipe_refresh.setOnRefreshListener {
            projects.clear()
            search_page = 1

            getProjects()
        }

        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener {
            if (search_hasNextPage) {
                search_page += 1
                getProjects()
            }
        }

        return v
    }

    @SuppressLint("ResourceAsColor")
    private fun getProjects() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(
            requireContext(),
            ProjServ.getProjects(search_page, search_value)
        ) { res, code, err ->
            if (code == 200 && res != null) {
                res.data?.forEach { d -> projects.add(d) }

                projectsAdapter.filterList(projects)

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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Explore().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}