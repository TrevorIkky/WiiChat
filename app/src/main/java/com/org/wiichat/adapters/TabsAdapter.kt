package com.org.wiichat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabsAdapter(fragmentManager : FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    var fragmentList = arrayListOf<Fragment>()
    var fragmentTitles = arrayListOf<String>()

    fun addFragment(fragment : Fragment, title: String){
        fragmentList.add(fragment)
        fragmentTitles.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
      return  fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return  fragmentTitles[position]
    }
}