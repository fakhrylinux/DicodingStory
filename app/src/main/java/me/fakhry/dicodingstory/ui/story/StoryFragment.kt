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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.databinding.FragmentStoryBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding
    private lateinit var storyViewModel: StoryViewModel
    private val storyListAdapter = StoryAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()

        val pref = UserPreferences.getInstance(requireContext().dataStore)
        storyViewModel =
            ViewModelProvider(this, StoryViewModelFactory(pref))[StoryViewModel::class.java]

        val layoutManager = LinearLayoutManager(context)
        binding?.rvStories?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding?.rvStories?.addItemDecoration(itemDecoration)
        binding?.rvStories?.adapter = storyListAdapter

        observeViewModel()
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.logout -> {
                        viewLifecycleOwner.lifecycleScope.launch {
                            storyViewModel.clearToken()
                        }
                        requireView().findNavController()
                            .navigate(R.id.action_storyFragment_to_loginFragment)
//                        activity?.finish()
                    }
                }

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        storyViewModel.listStories.observe(viewLifecycleOwner) { listStories ->
            storyListAdapter.setData(listStories)
        }
        storyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.isVisible = isLoading
        }
        storyViewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding?.tvErrorMessage?.isVisible = isError
        }
        storyViewModel.respondMessage.observe(viewLifecycleOwner) { message ->
            binding?.tvErrorMessage?.text = message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}