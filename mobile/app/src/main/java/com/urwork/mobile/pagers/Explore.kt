package com.urwork.mobile.pagers

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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
import com.urwork.mobile.adapters.UserAdapter
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.AuthApi
import com.urwork.mobile.api.ProjectApi
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.models.UserModelData
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
    lateinit var UserServ: AuthApi

    lateinit var projects_rv: RecyclerView
    lateinit var projectsAdapter: Project1
    lateinit var users_rv: RecyclerView
    lateinit var usersAdapter: UserAdapter

    var projects: ArrayList<ProjectModelData> = ArrayList()
    var users: ArrayList<UserModelData> = ArrayList()
    var search_value: String = ""
    var search_page: Int = 1
    var search_hasNextPage: Boolean = false

    lateinit var placeholder_iv: ImageView
    lateinit var placeholder_tv: TextView

    var MODE: String = "PROJECTS"

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
            actionbar.setDisplayShowHomeEnabled(false)
        }

        val v: View = inflater.inflate(R.layout.explore, container, false)

        placeholder_iv = v.findViewById(R.id.placeholder_image)
        placeholder_tv = v.findViewById(R.id.placeholder_msg)

        prefs = TinyDB(requireContext())
        ProjServ = ApiBuilder.buildService(
            ProjectApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )
        UserServ = ApiBuilder.buildService(
            AuthApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        projectsAdapter = Project1(requireContext(), false, projects)
        usersAdapter = UserAdapter(requireContext(), users)

        swipe_refresh = v.findViewById(R.id.swiperefresh)
        explore_type_rg = v.findViewById(R.id.explore_type)
        loadmore_btn = v.findViewById(R.id.projects_more)

        projects_rv = v.findViewById(R.id.projects_result)
        projects_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        projects_rv.setHasFixedSize(true)
        projects_rv.isNestedScrollingEnabled = false
        projects_rv.adapter = projectsAdapter

        users_rv = v.findViewById(R.id.users_result)
        users_rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        users_rv.setHasFixedSize(true)
        users_rv.isNestedScrollingEnabled = false
        users_rv.adapter = usersAdapter

        projectsAdapter.setOnItemClickListener(object : Project1.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e("CLICKED", projects.get(position).title.toString())
            }
        })

        usersAdapter.setOnItemClickListener(object : UserAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.e("CLICKED", users.get(position).firstName.toString())
            }
        })

        swipe_refresh.setOnRefreshListener {
            search_page = 1

            projects.clear()
            projectsAdapter.filterList(projects)

            users.clear()
            usersAdapter.filterList(users)

            if (search_value.isNotEmpty()) getData()
        }

        loadmore_btn.isVisible = false
        loadmore_btn.setOnClickListener {
            if (search_hasNextPage) {
                search_page += 1
                getData()
            }
        }

        explore_type_rg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.explore_type_projects -> {
                    MODE = "PROJECTS"
                }
                R.id.explore_type_users -> {
                    MODE = "USERS"
                }
            }
            search_page = 1

            projects.clear()
            projectsAdapter.filterList(projects)

            users.clear()
            usersAdapter.filterList(users)

            if (search_value.isNotEmpty()) getData()
        }

        return v
    }

    private fun getData() {
        if (MODE == "PROJECTS") {
            getProjects()
        } else if (MODE == "USERS") {
            getUsers()
        }
    }

    private fun getProjects() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(
            requireContext(),
            ProjServ.getProjects(search_page, search_value)
        ) { res, code, err ->
            placeholder_tv.text = res?.msg ?: "Something wrong with app"
            placeholder_tv.isVisible = true
            placeholder_iv.isVisible = true

            if (code == 200 && res != null) {
                res.data?.forEach { d -> projects.add(d) }

                projectsAdapter.filterList(projects)

                loadmore_btn.isVisible = res.nextPage != null
                search_hasNextPage = res.nextPage != null

                placeholder_tv.isVisible = res.data?.isEmpty() == true
                placeholder_iv.isVisible = res.data?.isEmpty() == true
            }

            swipe_refresh.isRefreshing = false
        }
    }

    private fun getUsers() {
        swipe_refresh.isRefreshing = true

        ApiEnqueue.enqueue(
            requireContext(),
            UserServ.getUsers(search_value, search_page)
        ) { res, code, err ->
            placeholder_tv.text = res?.msg ?: "Something wrong with app"
            placeholder_tv.isVisible = true
            placeholder_iv.isVisible = true

            if (code == 200 && res != null) {
                res.data?.forEach { d -> users.add(d) }

                usersAdapter.filterList(users)

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
                            search_page = 1

                            projects.clear()
                            projectsAdapter.filterList(projects)

                            users.clear()
                            usersAdapter.filterList(users)

                            search_value = query

                            if (search_value.isNotEmpty()) getData()

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