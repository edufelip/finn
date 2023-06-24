package com.projects.finn.ui.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.projects.finn.R
import com.projects.finn.databinding.ActivitySettingsBinding
import com.projects.finn.domain.models.User
import com.projects.finn.ui.delegators.auth.AuthExecutor
import com.projects.finn.ui.viewmodels.SettingsViewModel
import com.projects.finn.utils.extensions.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var authExecutor: AuthExecutor
    private val mSettingsViewModel by viewModels<SettingsViewModel>()

    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        initializeViewModel()
        setClickListeners()
        setContentView(binding.root)
    }

    fun initializeViewModel() {
        mSettingsViewModel.observeDelete().observe(
            this
        ) { delete: User ->
            if (delete.id == "1") {
                auth.currentUser?.let {
                    it.delete()
                        .addOnCompleteListener {
                            Toast.makeText(
                                this,
                                resources.getString(R.string.successfully_deleted_miss_you),
                                Toast.LENGTH_SHORT
                            ).show()
                            lifecycleScope.launch { authExecutor.logout() }
                            val intent = Intent(this, AuthActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                        }
                }
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.error_try_again_later),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun setClickListeners() {
        binding.backButton.setOnClickListener { finish() }
        binding.darkSwitch.setOnCheckedChangeListener { _, _ ->
            this.shortToast(resources.getString(R.string.option_not_available))
        }
        binding.notificationSwitch.setOnCheckedChangeListener { _, _ ->
            this.shortToast(resources.getString(R.string.option_not_available))
        }
        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setPositiveButton(
                    "Yes"
                ) { _: DialogInterface?, _: Int ->
                    if (auth.currentUser != null) {
                        val id = auth.currentUser!!.uid
                        mSettingsViewModel.deleteUser(id)
                    }
                }
                setNegativeButton(
                    resources.getString(R.string.no)
                ) { _: DialogInterface?, _: Int -> }
                setTitle(resources.getString(R.string.delete_account))
                setMessage(resources.getString(R.string.sure_delete_account))
                create().show()
            }
        }
    }
}