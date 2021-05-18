package com.sun.hero_01.ui.favorite

import com.sun.hero_01.R
import com.sun.hero_01.base.BaseFragment

class FavoriteFragment : BaseFragment() {

    override val layoutResourceId = R.layout.fragment_favorite

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}
