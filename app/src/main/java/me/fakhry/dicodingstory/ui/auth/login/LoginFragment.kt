package me.fakhry.dicodingstory.ui.auth.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.databinding.FragmentLoginBinding
import me.fakhry.dicodingstory.ui.UserSharedViewModel
import me.fakhry.dicodingstory.ui.story.StoryViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private lateinit var pref: UserPreferences
    private val userSharedViewModel: UserSharedViewModel by activityViewModels { StoryViewModelFactory(pref) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finishAffinity()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = UserPreferences.getInstance(requireContext().dataStore)

        observeViewModel()
        binding?.btnLogin?.setOnClickListener {
            val parentView = activity?.findViewById(R.id.frame_container) as View
            hideSoftKeyboard(parentView)

            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()
            login(email, password)
        }
        binding?.tvRegisterHere?.setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun login(email: String, password: String) {
        userSharedViewModel.loginRequest(email, password)
    }

    private fun observeViewModel() {
        userSharedViewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                findNavController().popBackStack()
            }
        }
        userSharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.isVisible = isLoading
        }
        userSharedViewModel.isFormValid.observe(viewLifecycleOwner) { isFormValid ->
            binding?.btnLogin?.isEnabled = isFormValid
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}