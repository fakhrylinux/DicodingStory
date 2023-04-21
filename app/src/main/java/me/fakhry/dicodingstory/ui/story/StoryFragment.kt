package me.fakhry.dicodingstory.ui.story

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.databinding.FragmentStoryBinding
import me.fakhry.dicodingstory.network.model.StoryItem

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class StoryFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding
    private val storyListAdapter = StoryAdapter()
    private lateinit var pref: UserPreferences
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = UserPreferences.getInstance(requireContext().dataStore)
        storyViewModel = ViewModelProvider(
            this,
            StoryViewModelFactory(pref, requireContext())
        )[StoryViewModel::class.java]
        token = runBlocking { pref.getToken().first() }

        isLoggedIn()
        setupMenu()
        observeViewModel()
        setupRecyclerView()

        binding?.fabCreate?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_create -> {
                val direction =
                    StoryFragmentDirections.actionStoryFragmentToCreateStoryFragment(token)
                findNavController().navigate(direction)
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding?.rvStories?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding?.rvStories?.addItemDecoration(itemDecoration)
        binding?.rvStories?.adapter = storyListAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyListAdapter.retry()
            }
        )
        viewLifecycleOwner.lifecycleScope.launch {
            storyListAdapter.loadStateFlow.collectLatest { loadState ->
                binding?.progressBar?.isVisible = loadState.refresh is LoadState.Loading
                binding?.tvErrorMessage?.isVisible = loadState.refresh is LoadState.Error
            }
        }
        storyListAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(item: StoryItem) {
                val direction = StoryFragmentDirections.actionStoryFragmentToDetailFragment(item)
                findNavController().navigate(direction)
            }
        })
    }

    private fun isLoggedIn() {
        storyViewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                storyViewModel.getAllStories().observe(viewLifecycleOwner) { stories ->
                    storyListAdapter.submitData(lifecycle, stories)
                }
            } else {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.logout -> {
                        storyViewModel.logout()
                        findNavController().navigate(R.id.loginFragment)
                    }

                    R.id.map -> {
                        val direction =
                            StoryFragmentDirections.actionStoryFragmentToMapsFragment(token)
                        findNavController().navigate(direction)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        storyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.isVisible = isLoading
        }
        storyViewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding?.tvErrorMessage?.isVisible = isError
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}