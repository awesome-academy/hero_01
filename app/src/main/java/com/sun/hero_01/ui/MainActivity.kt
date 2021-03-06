package com.sun.hero_01.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sun.hero_01.R
import com.sun.hero_01.ui.`class`.ClassFragment
import com.sun.hero_01.ui.champion.ChampionFragment
import com.sun.hero_01.ui.favorite.FavoriteFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initEvent()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val fragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.frameContainer)
            when (fragment) {
                is ChampionFragment, is ClassFragment, is FavoriteFragment -> {
                    showAlertDialog(getString(R.string.notify_exit_app)){
                        finish()
                    }
                }
                else -> {
                    super.onBackPressed()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun initView() {
        toolbarTop.apply {
            setLogo(R.mipmap.ic_app)
            setNavigationIcon(R.mipmap.ic_return)
            setSupportActionBar(this)
            setNavigationOnClickListener {
                super.onBackPressed()
            }
        }
        loadFragment(ChampionFragment.newInstance())
    }

    private fun initEvent() {
        navigationBottom.setOnNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.menuChampion -> {
                    loadFragment(ChampionFragment.newInstance())
                    true
                }
                R.id.menuClass -> {
                    loadFragment(ClassFragment.newInstance())
                    true
                }
                R.id.menuFavorite -> {
                    loadFragment(FavoriteFragment.newInstance())
                    true
                }
                else -> false
            }
        }
        navigationBottom.setOnNavigationItemReselectedListener {
            Toast.makeText(this, getString(R.string.notify_reselect_item), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().run {
            replace(R.id.frameContainer, fragment)
            addToBackStack(null)
            commit()
        }
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        navigationBottom.visibility = visibility
    }

    private fun showAlertDialog(msg: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name_toolbar)
            .setMessage(msg)
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes) { _, _ -> onConfirm() }
            .create()
            .show()
    }

    companion object {
        fun getMainIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
