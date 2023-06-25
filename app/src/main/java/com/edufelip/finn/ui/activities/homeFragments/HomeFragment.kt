package com.edufelip.finn.ui.activities.homeFragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.edufelip.finn.R
import com.edufelip.finn.databinding.FragmentHomeBinding
import com.edufelip.finn.domain.models.Like
import com.edufelip.finn.domain.models.Post
import com.edufelip.finn.domain.models.User
import com.edufelip.finn.ui.activities.AuthActivity
import com.edufelip.finn.ui.activities.PostActivity
import com.edufelip.finn.ui.adapters.FeedRecyclerAdapter
import com.edufelip.finn.ui.delegators.auth.AuthExecutor
import com.edufelip.finn.ui.viewmodels.HomeFragmentViewModel
import com.edufelip.finn.ui.viewmodels.SharedLikeViewModel
import com.edufelip.finn.utils.Constants
import com.edufelip.finn.utils.extensions.GlideUtils
import com.edufelip.finn.utils.extensions.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), FeedRecyclerAdapter.RecyclerClickListener {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var glideUtils: GlideUtils

    @Inject
    lateinit var authExecutor: AuthExecutor
    private var handleClick: HandleClick? = null
    private var feedRecyclerAdapter: FeedRecyclerAdapter? = null
    private var user: User? = null
    private var posts: ArrayList<Post>? = null
    private var isLoading = false
    private val isLastPage = false
    private var isScrolling = false
    private var nextPage = 1

    private val mHomeFragmentViewModel by viewModels<HomeFragmentViewModel>()
    private val mSharedLikeViewModel by viewModels<SharedLikeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val ARG_CHECK_USER = "check_user"
        const val ARG_POST = "post"
        const val EXTRA_ERROR = "Error"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_CHECK_USER, 1)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initializeViewModel()
        loadUserPhoto()
        if(savedInstanceState == null) {
            checkLoggedUser();
            mHomeFragmentViewModel.getPosts(auth.currentUser?.uid, nextPage, false);
        }
        initializeRecyclerView()
        setClickListeners()
        setSwipeRefresh()
        return binding.root
    }

    private fun loadUserPhoto() {
        auth.currentUser?.photoUrl?.let {
            glideUtils.load(
                it,
                binding.profilePictureIcon
            )
        }
    }

    private fun checkLoggedUser() {
        val id = auth.currentUser?.uid
        val photo =
            if (auth.currentUser?.photoUrl != null) auth.currentUser?.photoUrl.toString() else ""
        val displayName = auth.currentUser?.displayName
        val tempUser = User(id, displayName, photo)
        mHomeFragmentViewModel.getUser(tempUser)
    }

    private fun initializeViewModel() {

        mHomeFragmentViewModel.observeUser().observe(
            viewLifecycleOwner
        ) { user: User ->
            if (user.id == "-1") {
                forceLogout()
                return@observe
            }
            this.user = user
        }

        mHomeFragmentViewModel.observePosts().observe(
            viewLifecycleOwner
        ) { posts: List<Post> ->
            this.posts = ArrayList(posts)
            feedRecyclerAdapter!!.setPosts(this.posts)
            binding.swipeLayout.isRefreshing = false
            binding.emptyRecyclerLayout.isRefreshing = false
            checkEmptyRecycler(posts.size)
        }

        mHomeFragmentViewModel.observeUpdatedPost().observe(
            viewLifecycleOwner
        ) { updatedPost: Post? ->
            feedRecyclerAdapter!!.updatePost(updatedPost)
            isLoading = false
        }

        mHomeFragmentViewModel.observeNextPage().observe(
            viewLifecycleOwner
        ) { number: Int -> nextPage = number }

        mSharedLikeViewModel.observeLike().observe(
            viewLifecycleOwner
        ) { like: Like ->
            if (like.id == -1) {
                context?.shortToast(resources.getString(R.string.error_try_again_later))
            }
        }
    }

    private fun checkEmptyRecycler(size: Int) {
        if (size == 0) {
            binding.swipeLayout.visibility = View.GONE
            binding.emptyRecyclerLayout.visibility = View.VISIBLE
        } else {
            binding.swipeLayout.visibility = View.VISIBLE
            binding.emptyRecyclerLayout.visibility = View.GONE
        }
    }

    private fun forceLogout() {
        lifecycleScope.launch {
            authExecutor.logout()
        }
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        intent.putExtra(EXTRA_ERROR, resources.getString(R.string.error_occurred_log_later))
        startActivity(intent)
        requireActivity().finish()
    }

    private fun initializeRecyclerView() {
        posts = ArrayList()
        feedRecyclerAdapter = FeedRecyclerAdapter(context, posts, this, glideUtils)
        binding.feedRecyclerView.apply {
            adapter = feedRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager =
                        binding.feedRecyclerView.layoutManager as LinearLayoutManager?
                    val firstVisibleItemPosition =
                        layoutManager?.findFirstVisibleItemPosition() ?: 0
                    val visibleItemCount = layoutManager?.childCount ?: 0
                    val totalItemCount = layoutManager?.itemCount ?: 0
                    val isNotLoadingNotLastPage = !isLoading && !isLastPage
                    val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                    val isNotAtBeginning = firstVisibleItemPosition >= 0
                    val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
                    val isListFullOrOdd = posts!!.size % 10 == 0 || posts!!.size >= 40
                    val shouldPaginate =
                        (isNotLoadingNotLastPage && isAtLastItem && isNotAtBeginning
                                && isTotalMoreThanVisible && isScrolling && isListFullOrOdd)
                    if (shouldPaginate) {
                        mHomeFragmentViewModel.getPosts(user?.id, nextPage, false)
                        isScrolling = false
                        isLoading = true
                    } else {
                        binding.feedRecyclerView.setPadding(0, 0, 0, 0)
                    }
                }
            })
        }
    }

    fun setClickListeners() {
        binding.profilePictureIcon.setOnClickListener { v -> handleClick!!.buttonClicked(v) }
        binding.fakeSearchView.setOnClickListener { v -> handleClick!!.searchClicked(v) }
    }

    private fun setSwipeRefresh() {
        binding.swipeLayout.setOnRefreshListener {
            user?.id.let {
                mHomeFragmentViewModel.getPosts(
                    it,
                    1,
                    true
                )
            }
        }
        binding.emptyRecyclerLayout.setOnRefreshListener {
            user?.id?.let {
                mHomeFragmentViewModel.getPosts(
                    it,
                    1,
                    true
                )
            }
        }
    }

    fun setInterface(handle: HandleClick?) {
        handleClick = handle
    }

    private var postActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val post = data.getParcelableExtra<Post>(ARG_POST)
                feedRecyclerAdapter?.updatePostActivityResult(post)
            }
        }
    }

    override fun onItemClick(position: Int) {
        val post = posts?.getOrNull(position)
        val intent = Intent(context, PostActivity::class.java)
        intent.putExtra(ARG_POST, post)
        postActivityResultLauncher.launch(intent)
    }

    override fun onDeleteClick(position: Int) {
        posts?.removeAt(position)
        feedRecyclerAdapter?.notifyItemRemoved(position)
    }

    override fun onLikePost(position: Int) {
        val post = posts?.getOrNull(position)
        post?.isLiked = true
        post?.likesCount = post?.likesCount?.plus(1) ?: 0
        feedRecyclerAdapter?.updatePost(post)
        feedRecyclerAdapter?.notifyItemChanged(position)
        posts?.getOrNull(position)?.id?.let {
            mSharedLikeViewModel.likePost(it, user?.id)
        }
    }

    override fun onDislikePost(position: Int) {
        val post = posts!![position]
        post.isLiked = false
        post.likesCount = post.likesCount - 1
        feedRecyclerAdapter?.updatePost(post)
        feedRecyclerAdapter?.notifyItemChanged(position)
        posts?.getOrNull(position)?.id?.let {
            mSharedLikeViewModel.dislikePost(it, user)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}