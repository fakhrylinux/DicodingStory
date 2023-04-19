package me.fakhry.dicodingstory.ui.auth.register

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding?.btnRegister?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> register()
        }
    }

    private fun register() {
        hideSoftKeyboard()
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmail?.text.toString()
        val password = binding?.etPassword?.text.toString()
        if (isInputValid(name, email, password)) {
            registerViewModel.register(name, email, password)
        }
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding?.etNameLayout?.error = getString(R.string.name_error)
        } else if (email.isEmpty()) {
            binding?.etEmailLayout?.error = getString(R.string.email_error)
            binding?.etEmail?.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding?.etEmailLayout?.error = getString(R.string.valid_email)
            binding?.etEmail?.requestFocus()
        } else if (password.isEmpty()) {
            binding?.etPasswordLayout?.error = getString(R.string.password_error)
            binding?.etPassword?.requestFocus()
        } else {
            binding?.etEmailLayout?.error = null
            binding?.etPasswordLayout?.error = null
            binding?.etPassword?.error = null
            binding?.etEmail?.clearFocus()
            binding?.etPassword?.clearFocus()
            return true
        }
        return false
    }

    private fun observeViewModel() {
        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            enableDisableButton(!isLoading)
            binding?.progressBar?.isVisible = isLoading
        }
        registerViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (!isError) {
                binding?.apply {
                    etName.text?.clear()
                    etEmail.text?.clear()
                    etPassword.text?.clear()
                }
            }
        }
        registerViewModel.responseMessage.observe(viewLifecycleOwner) { responseMessage ->
            activity?.let { activity ->
                Snackbar.make(
                    activity.findViewById(R.id.registerContainer),
                    responseMessage,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enableDisableButton(loading: Boolean) {
        binding?.btnRegister?.isEnabled = loading
    }

    private fun hideSoftKeyboard() {
        val parentView = activity?.findViewById(R.id.registerContainer) as View
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(parentView.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}