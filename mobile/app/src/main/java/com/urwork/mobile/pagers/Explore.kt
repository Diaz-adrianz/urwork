package com.urwork.mobile.pagers

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.urwork.mobile.R


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val actionbar: ActionBar? =  (activity as AppCompatActivity?)!!.supportActionBar

        if (actionbar != null) {
            actionbar.show()
            actionbar.title = "Explore"
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.explore, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Toast.makeText(context, query, Toast.LENGTH_SHORT).show()
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