package com.edufelip.finn.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.edufelip.finn.R
import com.edufelip.finn.databinding.ActivityHomePageBinding
import com.edufelip.finn.databinding.NavHeaderBinding
import com.edufelip.finn.ui.activities.homeFragments.HandleClick
import com.edufelip.finn.ui.activities.homeFragments.HomeFragment
import com.edufelip.finn.ui.activities.homeFragments.NotificationsFragment
import com.edufelip.finn.ui.activities.homeFragments.SearchFragment
import com.edufelip.finn.ui.delegators.auth.AuthExecutor
import com.edufelip.finn.utils.Constants.PRIVACY_POLICY_URL
import com.edufelip.finn.utils.extensions.GlideUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomePageActivity : AppCompatActivity(), HandleClick {
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var glide: RequestManager
    @Inject lateinit var authExecutor: AuthExecutor
    @Inject lateinit var glideUtils: GlideUtils
    private var homeFragment: HomeFragment? = null
    private var searchFragment: SearchFragment? = null
    private var notificationsFragment: NotificationsFragment? = null
    private var activeFragment: Fragment? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val toggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open_nav_drawer,
            R.string.close_nav_drawer
        )
    }

    private var _binding: ActivityHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomePageBinding.inflate(layoutInflater)
        initializeComponents()
        setupBottomNavigationView()
        setupNavigationDrawer()
        updateNavUserInfo()
        setupClickListeners()
        setContentView(binding.root)
    }

    private fun initializeComponents() {
        homeFragment = HomeFragment().apply {
            setInterface(this@HomePageActivity)
        }
        searchFragment = SearchFragment().apply {
            setInterface(this@HomePageActivity)
        }
        notificationsFragment = NotificationsFragment()
        activeFragment = homeFragment
    }

    private fun updateNavUserInfo() {
        auth.currentUser?.let {
            val headerBinding: NavHeaderBinding =
                NavHeaderBinding.bind(binding.navView.getHeaderView(0))
            headerBinding.displayName.text = auth.currentUser!!.displayName
            headerBinding.displayEmail.text = auth.currentUser!!.email
            val photoUrl = if (auth.currentUser!!.photoUrl != null) auth.currentUser!!
                .photoUrl.toString() else ""
            glideUtils.load(photoUrl, headerBinding.profilePictureIv)
        }
    }

    private fun setupNavigationDrawer() {
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.drawer_profile, R.id.drawer_posts -> Intent(
                    this@HomePageActivity,
                    ProfileActivity::class.java
                )

                R.id.drawer_saved -> Intent(
                    this@HomePageActivity,
                    SavedActivity::class.java
                )

                R.id.drawer_settings -> Intent(
                    this@HomePageActivity,
                    SettingsActivity::class.java
                )

                R.id.drawer_privacy_policy -> Intent(
                    Intent.ACTION_VIEW
                ).apply {
                    data = Uri.parse(PRIVACY_POLICY_URL)
                }

                else -> return@setNavigationItemSelectedListener false
            }
            startActivity(intent)
            true
        }
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.iconHome -> setCurrentFragment(homeFragment)
                R.id.iconAdd -> {
                    val sheetView: View =
                        LayoutInflater.from(applicationContext).inflate(
                            R.layout.bottom_sheet_layout,
                            findViewById(R.id.bottom_sheet)
                        )
                    bottomSheetDialog = BottomSheetDialog(this@HomePageActivity).apply {
                        setContentView(sheetView)
                        show()
                    }
                    setDialogClickListeners()
                    return@setOnItemSelectedListener false
                }

                R.id.iconChat -> setCurrentFragment(searchFragment)
                R.id.iconNotification -> setCurrentFragment(notificationsFragment)
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        supportFragmentManager.beginTransaction().apply {
            add(R.id.flFragment, homeFragment!!)
            add(R.id.flFragment, searchFragment!!).hide(searchFragment!!)
            add(R.id.flFragment, notificationsFragment!!).hide(notificationsFragment!!)
            commit()
        }
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                authExecutor.logout()
            }
            startActivity(Intent(this@HomePageActivity, AuthActivity::class.java))
            finish()
        }
    }

    private fun setDialogClickListeners() {
        bottomSheetDialog?.findViewById<View>(R.id.create_bottom_dialog_community_button)
            ?.setOnClickListener {
                val intent = Intent(this@HomePageActivity, CreateCommunityActivity::class.java)
                startActivity(intent)
                bottomSheetDialog!!.dismiss()
            }
        bottomSheetDialog?.findViewById<View>(R.id.create_bottom_dialog_post_button)
            ?.setOnClickListener {
                val intent = Intent(this@HomePageActivity, CreatePostActivity::class.java)
                startActivity(intent)
                bottomSheetDialog!!.dismiss()
            }
    }

    private fun setCurrentFragment(fragment: Fragment?) {
        supportFragmentManager
            .beginTransaction()
            .hide(activeFragment!!)
            .show(fragment!!)
            .commit()
        activeFragment = fragment
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun buttonClicked(v: View) {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun searchClicked(v: View) {
        binding.bottomNavigationView.selectedItemId = R.id.iconChat
        searchFragment!!.clickSearchView()
    }
}