package me.fakhry.dicodingstory.ui.auth.register

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

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

        binding?.btnLogin?.setOnClickListener {
            val parentView = activity?.findViewById(R.id.frame_container) as View
            hideSoftKeyboard(parentView)
            val name = binding?.etName?.text.toString()
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()
            Log.d("RegisterFragment", "$name, $email, $password")
            registerViewModel.register(name, email, password)
        }
    }

    private fun observeViewModel() {
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
            Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show()
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