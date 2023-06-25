package com.edufelip.finn.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.edufelip.finn.R
import com.edufelip.finn.databinding.ActivityAuthBinding
import com.edufelip.finn.ui.viewmodels.SignInViewModel
import com.edufelip.finn.utils.GoogleAuthUiClient
import com.edufelip.finn.utils.RemoteConfigUtils
import com.edufelip.finn.utils.Verify
import com.edufelip.finn.utils.extensions.shortToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var remoteConfigUtils: RemoteConfigUtils

    @Inject
    lateinit var gso: GoogleSignInOptions

    @Inject
    lateinit var googleAuthClient: GoogleAuthUiClient

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var callbackManager: CallbackManager? = null
    private var _binding: ActivityAuthBinding? = null
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        binding = _binding!!

        initializeComponents()
        createGoogleRequest()
//        createFacebookRequest()
        setClickListeners()
        checkRemoteConfig()
        captureErrorExtra()
        setContentView(binding.root)
    }

    private fun initializeComponents() {
        binding.googleSignInButton.setSize(SignInButton.SIZE_WIDE)
        binding.facebookSignInButton.setPermissions("email", "public_profile")
        callbackManager = create()
        val tv = binding.googleSignInButton.getChildAt(0) as? TextView
        tv?.text = getString(R.string.signgoogle)
    }

    fun setClickListeners() {
        binding.fbFakeButton.setOnClickListener { binding.facebookSignInButton.performClick() }
        binding.googleSignInButton.setOnClickListener {
            val googleSignInClient = this.let { GoogleSignIn.getClient(it, gso) }
            googleSignInClient.signOut().addOnCompleteListener {
                val intentSignInGoogle = googleSignInClient.signInIntent
                intentSignInGoogle.putExtra("REQUEST_CODE", 0)
                resultLauncher.launch(intentSignInGoogle)
            }
        }
        binding.loginButton.setOnClickListener { authenticateUser() }
        binding.redirectRegister.setOnClickListener {
            startActivity(
                Intent(
                    this@AuthActivity,
                    RegisterActivity::class.java
                )
            )
        }
        binding.forgotPassButton.setOnClickListener {
            startActivity(
                Intent(
                    this@AuthActivity,
                    ForgotPassActivity::class.java
                )
            )
        }
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account = task.getResult(ApiException::class.java)
                handleGoogleAccessToken(account.idToken)
            } catch (e: ApiException) {
                this.shortToast(e.message.toString())
            }
        }
    }

    private fun authenticateUser() {
        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(
                this,
                resources.getString(R.string.please_enter_email),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!Verify.isEmailValid(email)) {
            Toast.makeText(this, resources.getString(R.string.email_is_invalid), Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(
                this,
                resources.getString(R.string.please_enter_password),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    mainPageRedirect()
                } else {
                    val exception: String
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        exception = resources.getString(R.string.user_not_registered)
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        exception = resources.getString(R.string.password_incorrect)
                    } catch (e: Exception) {
                        exception = "Error " + e.message
                        e.printStackTrace()
                    }
                    this.shortToast(resources.getString(R.string.error) + ": " + exception)
                }
            }
    }

    private fun captureErrorExtra() {
        val error = intent.getStringExtra("Error")
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createGoogleRequest() {
        mGoogleSignInClient = gso.let { GoogleSignIn.getClient(this, it) }
    }

//    fun createFacebookRequest() {
//        binding.facebookSignInButton.registerCallback(
//            callbackManager,
//            object : FacebookCallback<RESULT?> {
//                override fun onSuccess(loginResult: LoginResult) {
//                    handleFacebookAccessToken(loginResult.accessToken)
//                }
//
//                override fun onCancel() {
//                    // App code
//                }
//
//                override fun onError(exception: FacebookException) {
//                    // App code
//                }
//            })
//    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleGoogleAccessToken(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    mainPageRedirect()
                } else {
                    Log.d("Google Auth", "User couldn't log in")
                }
            }
    }

    fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    mainPageRedirect()
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun mainPageRedirect() {
        startActivity(Intent(this, HomePageActivity::class.java))
        finish()
    }

    private fun checkRemoteConfig() {
        val isFacebookAuthEnabled = remoteConfigUtils.isFacebookAuthEnabled
        if (isFacebookAuthEnabled) binding.flFacebookAuthButton.visibility = View.VISIBLE
    }
}