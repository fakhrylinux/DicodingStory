package me.fakhry.dicodingstory.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.UserPreferences
import me.fakhry.dicodingstory.databinding.FragmentLoginBinding
import me.fakhry.dicodingstory.ui.UserSharedViewModel
import me.fakhry.dicodingstory.ui.story.StoryViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private lateinit var pref: UserPreferences
    private val userSharedViewModel: UserSharedViewModel by activityViewModels {
        StoryViewModelFactory(pref, requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().actionBar?.hide()
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
        binding?.btnLogin?.setOnClickListener(this)
        binding?.tvSignup?.setOnClickListener(this)
        playAnimation()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().actionBar?.hide()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> login()
            R.id.tv_signup -> findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.tvHeader, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding?.etEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding?.etPassword, View.ALPHA, 1f).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playSequentially(email, password, loginButton)
            start()
        }
    }

    private fun login() {
        hideSoftKeyboard()
        val email = binding?.etEmail?.text.toString()
        val password = binding?.etPassword?.text.toString()
        if (isInputValid(email, password)) {
            userSharedViewModel.loginRequest(email, password)
        }
    }

    private fun isInputValid(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding?.etEmail?.error = getString(R.string.email_error)
            binding?.etEmail?.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding?.etEmail?.error = getString(R.string.valid_email)
            binding?.etEmail?.requestFocus()
        } else if (password.isEmpty()) {
            binding?.etPassword?.requestFocus()
        } else {
            binding?.etPassword?.error = null
            binding?.etEmail?.clearFocus()
            binding?.etPassword?.clearFocus()
            return true
        }
        return false
    }

    private fun observeViewModel() {
        userSharedViewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                findNavController().popBackStack()
            }
        }
        userSharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            updateView(!isLoading)
        }
        userSharedViewModel.responseMessage.observe(viewLifecycleOwner) { responseMessage ->
            activity?.let { activity ->
                Snackbar.make(
                    activity.findViewById(R.id.frame_container),
                    responseMessage,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateView(isLoading: Boolean) {
        if (!isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.btnLogin?.isEnabled = isLoading
        binding?.tvSignup?.isEnabled = isLoading
    }

    private fun hideSoftKeyboard() {
        val parentView = activity?.findViewById(R.id.frame_container) as View
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(parentView.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().actionBar?.show()
        _binding = null
    }
}