package com.org.wiichat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.org.wiichat.R
import com.org.wiichat.adapters.RecentAdapter
import com.org.wiichat.core.room.WiiDatabase
import com.org.wiichat.databinding.FragmentRecentBinding

/**
 * A simple [Fragment] subclass.
 */
class RecentFragment : Fragment() {

    lateinit var recentBinding: FragmentRecentBinding
    lateinit var dbInstance: WiiDatabase
    lateinit var adapter: RecentAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        recentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_recent, container, false)
        dbInstance = WiiDatabase.getInstance(requireActivity())

        recentBinding.devicesRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        dbInstance.userDao().getAllUsers().observe(viewLifecycleOwner, Observer { user ->
            if (user.isNotEmpty()) {
                recentBinding.loadingIndicator.hide()
                recentBinding.infoContainer.visibility = View.GONE
                adapter = RecentAdapter(requireActivity(), user) {

                }
                recentBinding.devicesRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            } else {
                recentBinding.loadingIndicator.hide()
                recentBinding.infoContainer.visibility = View.VISIBLE
            }
        })

        return recentBinding.root
    }

}
