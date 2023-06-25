package com.edufelip.finn.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.edufelip.finn.R
import com.edufelip.finn.databinding.ActivityPostBinding
import com.edufelip.finn.domain.models.Comment
import com.edufelip.finn.domain.models.Like
import com.edufelip.finn.domain.models.Post
import com.edufelip.finn.domain.models.User
import com.edufelip.finn.ui.adapters.CommentsAdapter
import com.edufelip.finn.ui.viewmodels.PostActivityViewModel
import com.edufelip.finn.ui.viewmodels.SharedLikeViewModel
import com.edufelip.finn.utils.RemoteConfigUtils
import com.edufelip.finn.utils.extensions.GlideUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {
    @Inject lateinit var glideUtils: GlideUtils
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var remoteConfigUtils: RemoteConfigUtils
    
    private val mPostActivityViewModel by viewModels<PostActivityViewModel>()
    private val mSharedLikeViewModel by viewModels<SharedLikeViewModel>()
    private var comments: ArrayList<Comment>? = null
    private var adapter: CommentsAdapter? = null
    private var post: Post? = null
    
    private var _binding: ActivityPostBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPostBinding.inflate(layoutInflater)
        postExtras
        loadUserPhoto()
        initializeViewModel()
        initializeRecyclerView()
        setClickListeners()
        setContentView(binding.root)
    }

    private val postExtras: Unit
        get() {
            post = intent.getParcelableExtra("post")
            if (post != null) {
                binding.postCommunity.text = post?.communityTitle
                val text = resources.getString(R.string.posted_by) + " " + post?.userName
                binding.postSource.text = text
                binding.postContent.text = post?.content
                binding.likesCount.text = post?.likesCount.toString()
                binding.commentsCount.text = post?.commentsCount.toString()
                if (post?.communityImage != null) {
                    glideUtils.loadFromServer(
                        post?.communityImage,
                        binding.communityPictureIcon
                    )
                }
                if (post?.image != null) {
                    glideUtils.loadFromServer(post?.image, binding.postImage)
                }
                if (post?.isLiked == true) {
                    binding.likeButton.isChecked = true
                }
            }
            checkAdmin()
        }

    private fun loadUserPhoto() {
        glideUtils.load(auth.currentUser?.photoUrl, binding.userPic)
    }

    private fun initializeViewModel() {
       
        mPostActivityViewModel.observeComments().observe(
            this
        ) { comments: List<Comment>? ->
            this.comments =
                comments?.let { ArrayList(it) }
            adapter?.setComments(this.comments)
        }
        mPostActivityViewModel.observeUpdatedComment().observe(
            this
        ) { comment: Comment? ->
            adapter?.updateComment(
                comment
            )
        }
        mPostActivityViewModel.observeCreatedComment().observe(this) { createdComment: Comment ->
            if (createdComment.id == -1) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.error_try_again_later),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@observe
            }
            binding.commentEdittext.setText("")
            post?.id?.let { mPostActivityViewModel.getComments(it) }
            post?.commentsCount = post?.commentsCount?.plus(1) ?: 0
            binding.commentsCount.text = post?.commentsCount.toString()
        }
        mPostActivityViewModel.observePost().observe(this) { post: Post ->
            if (post.id == -1) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.error_try_again_later),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@observe
            }
            if (post.id == -2) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.successfully_deleted),
                    Toast.LENGTH_SHORT
                ).show()
                this.post?.userId = "-2"
                finish()
            }
        }
        mSharedLikeViewModel.observeLike().observe(
            this
        ) { like: Like ->
            when (like.id) {
                -1 -> {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.error_try_again_later),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                -2 -> {
                    val count = post?.likesCount?.minus(1) ?: 0
                    post?.likesCount = count
                    post?.isLiked = false
                    binding.likesCount.text = count.toString()
                }

                else -> {
                    val count = post?.likesCount?.plus(1) ?: 0
                    post?.likesCount = count
                    post?.isLiked = true
                    binding.likesCount.text = count.toString()
                }
            }
        }
        post?.id?.let { mPostActivityViewModel.getComments(it) }
    }

    private fun initializeRecyclerView() {
        comments = ArrayList()
        adapter = CommentsAdapter(this, comments, glideUtils)
        binding.recyclerComments.adapter = adapter
        binding.recyclerComments.layoutManager = LinearLayoutManager(this)
    }

    private fun checkAdmin() {
        if (auth.currentUser != null) {
            if (post?.userId == auth.currentUser?.uid) {
                binding.textViewOptions.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun setClickListeners() {
        binding.backButton.setOnClickListener { finish() }
        binding.createCommentButton.setOnClickListener { createComment() }
        binding.likeButton.setOnClickListener {
            if (binding.likeButton.isChecked) {
                likePost()
            } else {
                dislikePost()
            }
        }
        binding.shareButton.setOnClickListener {
            Toast.makeText(
                this,
                resources.getString(R.string.not_available_yet),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.textViewOptions.setOnClickListener {
            val menuBuilder = MenuBuilder(this)
            val inflater = MenuInflater(this)
            inflater.inflate(R.menu.community_menu, menuBuilder)
            val optionsMenu =
                MenuPopupHelper(this, menuBuilder, binding.textViewOptions)
            optionsMenu.setForceShowIcon(true)
            menuBuilder.setCallback(object : MenuBuilder.Callback {
                override fun onMenuItemSelected(
                    menu: MenuBuilder,
                    item: MenuItem
                ): Boolean {
                    if (item.itemId == R.id.communityDelete) {
                        val dialog =
                            AlertDialog.Builder(this@PostActivity)
                        dialog.setTitle(resources.getString(R.string.delete_post))
                        dialog.setMessage(resources.getString(R.string.are_you_sure_delete_post))
                        dialog.setPositiveButton(
                            resources.getString(R.string.yes)
                        ) { _: DialogInterface?, _: Int ->
                            mPostActivityViewModel.deletePost(
                                auth.currentUser?.uid, post
                            )
                        }
                        dialog.setNegativeButton(resources.getString(R.string.no), null)
                        dialog.show()
                        return true
                    }
                    return false
                }

                override fun onMenuModeChange(menu: MenuBuilder) {}
            })
            optionsMenu.show()
        }
    }

    private fun likePost() {
        val userId = auth.currentUser?.uid
        post?.id?.let { mSharedLikeViewModel.likePost(it, userId) }
    }

    private fun dislikePost() {
        val userId = auth.currentUser?.uid
        val name = auth.currentUser?.displayName
        val user = User()
        user.id = userId
        user.name = name
        post?.id?.let { mSharedLikeViewModel.dislikePost(it, user) }
    }

    private fun createComment() {
        val etText = binding.commentEdittext.text
        val content = etText?.toString() ?: ""
        if (content.isEmpty()) {
            Toast.makeText(
                this,
                resources.getString(R.string.please_fill_post_field),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val comment = Comment()
        comment.userId = auth.currentUser?.uid
        comment.postId = post?.id ?: 0
        comment.content = content
        mPostActivityViewModel.createComment(comment)
    }

    override fun finish() {
        val returnIntent = Intent()
        returnIntent.putExtra("post", post)
        setResult(RESULT_OK, returnIntent)
        super.finish()
    }
}