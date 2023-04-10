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
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.databinding.FragmentCreateStoryBinding
import me.fakhry.dicodingstory.util.rotateBitmap
import me.fakhry.dicodingstory.util.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryFragment : Fragment() {

    private var _binding: FragmentCreateStoryBinding? = null
    private val binding get() = _binding
    private var result: File? = null
    private var getFile: File? = null
    private var isBackCamera: Boolean? = null
    private val viewModel: CreateStoryViewModel by viewModels()
    private val args: CreateStoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("200") { _, bundle ->
            @Suppress("DEPRECATION")
            result = bundle.getSerializable("result") as File
            getFile = result
            val resultImage =
                rotateBitmap(BitmapFactory.decodeFile(getFile?.path ?: ""), true)
            binding?.ivPhoto?.load(resultImage)
        }
        binding?.btnTakePhoto?.setOnClickListener { startCameraX() }
        binding?.btnChoosePhoto?.setOnClickListener { startGallery() }
        binding?.btnSubmit?.setOnClickListener { submitStory() }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) findNavController().popBackStack()
        }
    }

    private fun submitStory() {
        val token = args.token
        if (getFile != null) {
            val file = getFile as File
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val description =
                (binding?.etCaption?.text.toString()).toRequestBody("text/plain".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            viewModel.addStoryRequest(imageMultipart, description, token)
        } else {
            Toast.makeText(context, "Take photo first", Toast.LENGTH_SHORT).show()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}