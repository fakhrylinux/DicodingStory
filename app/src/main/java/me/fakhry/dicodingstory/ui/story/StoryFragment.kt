package me.fakhry.dicodingstory.ui.story

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.databinding.FragmentStoryBinding
import me.fakhry.dicodingstory.network.model.ListStoryItem
import me.fakhry.dicodingstory.ui.UserSharedViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding
    private val storyListAdapter = StoryAdapter(arrayListOf())
    private lateinit var pref: UserPreferences
    private lateinit var userSharedViewModel: UserSharedViewModel

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
        userSharedViewModel =
            ViewModelProvider(this, StoryViewModelFactory(pref))[UserSharedViewModel::class.java]

        isLoggedIn()
        setupMenu()
        observeViewModel()

        val layoutManager = LinearLayoutManager(context)
        binding?.rvStories?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding?.rvStories?.addItemDecoration(itemDecoration)
        binding?.rvStories?.adapter = storyListAdapter
    }

    private fun isLoggedIn() {
        userSharedViewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                userSharedViewModel.getAllStories(token)
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
                        userSharedViewModel.logout()
                        findNavController().navigate(R.id.loginFragment)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        userSharedViewModel.listStories.observe(viewLifecycleOwner) { listStories ->
            setupStoryList(listStories)
        }
        userSharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.isVisible = isLoading
        }
        userSharedViewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding?.tvErrorMessage?.isVisible = isError
        }
        userSharedViewModel.respondMessage.observe(viewLifecycleOwner) { message ->
            binding?.tvErrorMessage?.text = message
        }
    }

    private fun setupStoryList(listStories: List<ListStoryItem>) {
        storyListAdapter.setData(listStories)
        storyListAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(item: ListStoryItem) {
                val direction = StoryFragmentDirections.actionStoryFragmentToDetailFragment(item)
                findNavController().navigate(direction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}