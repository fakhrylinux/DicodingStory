package me.fakhry.dicodingstory.ui.createstory

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.snackbar.Snackbar
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.databinding.FragmentCreateStoryBinding
import me.fakhry.dicodingstory.util.reduceFileImage
import me.fakhry.dicodingstory.util.rotateBitmap
import me.fakhry.dicodingstory.util.showLoading
import me.fakhry.dicodingstory.util.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCreateStoryBinding? = null
    private val binding get() = _binding
    private var result: File? = null
    private var getFile: File? = null
    private val viewModel: CreateStoryViewModel by viewModels()
    private val args: CreateStoryFragmentArgs by navArgs()
    private var isBackCamera: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(CAMERA_X_IS_BACK_CAMERA) { _, bundle ->
            isBackCamera = bundle.getBoolean("isBackCamera")
        }
        setFragmentResultListener("200") { _, bundle ->
            @Suppress("DEPRECATION")
            result = bundle.getSerializable("result") as File
            getFile = result
            val resultImage =
                rotateBitmap(BitmapFactory.decodeFile(getFile?.path ?: ""), isBackCamera)
            binding?.ivPhoto?.load(resultImage)
        }
        binding?.btnTakePhoto?.setOnClickListener(this)
        binding?.btnChoosePhoto?.setOnClickListener(this)
        binding?.btnSubmit?.setOnClickListener(this)

        observeViewModel()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_take_photo -> startCameraX()
            R.id.btn_choose_photo -> startGallery()
            R.id.btn_submit -> submitStory()
        }
    }

    private fun observeViewModel() {
        viewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) findNavController().navigate(R.id.storyFragment)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            enableDisableButton(!isLoading)
            binding?.progressBar?.showLoading(isLoading)
        }
        viewModel.responseMessage.observe(viewLifecycleOwner) { responseMessage ->
            activity?.let { activity ->
                Snackbar.make(
                    activity.findViewById(R.id.create_story_container),
                    responseMessage,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enableDisableButton(loading: Boolean) {
        binding?.btnSubmit?.isEnabled = loading
    }

    private fun submitStory() {
        val token = args.token
        if (getFile == null) {
            Toast.makeText(context, getString(R.string.take_photo_first), Toast.LENGTH_SHORT).show()
        } else if (binding?.etCaption?.text?.isEmpty() == true) {
            binding?.tiLayout?.error = getString(R.string.caption_cannot_be_empty)
        } else {
            binding?.tiLayout?.error = null
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val description =
                (binding?.etCaption?.text.toString()).toRequestBody("text/plain".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            viewModel.addStoryRequest(imageMultipart, description, token)
        }
    }

    private val launcherForResultFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = context?.let { uriToFile(selectedImg, it) }
            getFile = myFile
            binding?.ivPhoto?.load(myFile)
        }
    }

    private fun startCameraX() {
        findNavController().navigate(R.id.cameraFragment)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherForResultFromGallery.launch(chooser)
    }

    companion object {
        const val CAMERA_X_RESULT = "200"
        const val CAMERA_X_IS_BACK_CAMERA = "100"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}