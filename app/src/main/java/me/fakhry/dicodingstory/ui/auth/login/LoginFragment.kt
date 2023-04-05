package me.fakhry.dicodingstory.ui.auth.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.databinding.FragmentLoginBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private lateinit var loginViewModel: LoginViewModel

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

        val pref = UserPreferences.getInstance(requireContext().dataStore)

        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(pref))[LoginViewModel::class.java]

        observeViewModel()

        binding?.btnLogin?.setOnClickListener {
            val parentView = activity?.findViewById<View>(R.id.frame_container) as View
            hideSoftKeyboard(parentView)
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()
            if (loginViewModel.isFormValid(email, password)) {
                loginViewModel.loginRequest(email, password)
            }
        }
        binding?.tvRegisterHere?.setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun observeViewModel() {
        loginViewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token != null) {
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_storyFragment)
            }
        }
        loginViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.isVisible = isLoading
        }
        loginViewModel.responseMessage.observe(viewLifecycleOwner) { responseMessage ->
            Snackbar.make(
                requireActivity().findViewById(R.id.container),
                responseMessage,
                Snackbar.LENGTH_LONG
            ).show()
        }
        loginViewModel.isFormValid.observe(viewLifecycleOwner) { isFormValid ->
            binding?.btnLogin?.isEnabled = isFormValid
        }
        loginViewModel.isLoginSuccess.observe(viewLifecycleOwner) { isLoginSuccess ->
            if (isLoginSuccess) {
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_storyFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}