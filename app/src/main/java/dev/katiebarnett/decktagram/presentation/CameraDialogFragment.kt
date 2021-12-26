package dev.katiebarnett.decktagram.presentation

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.CameraDialogFragmentBinding
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class CameraDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "CameraDialogFragment"
        
        private const val ARG_GAME_ID = "gameId"
        
        private val PERMISSIONS_INTERNAL_APP_STORAGE = arrayOf(
            Manifest.permission.CAMERA
        )
        private val PERMISSIONS_GALLERY_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        
        fun newInstance(gameId: Long): CameraDialogFragment {
            val dialog = CameraDialogFragment()
            val args = Bundle()
            args.putLong(ARG_GAME_ID, gameId)
            dialog.arguments = args
            return dialog
        }
    }

    interface DialogListener {
        fun onDone(imagePaths: List<String>)
    }

    internal var listener: DialogListener? = null

    private val viewModel: CameraViewModel by viewModels()

    private lateinit var binding: CameraDialogFragmentBinding
    
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService
    
    private lateinit var requiredPermissions: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Decktagram_FullScreenDialog)
        
        viewModel.gameId = arguments?.getLong(ARG_GAME_ID)

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGranted ->
                if (isGranted.containsValue(false)) {
                    displayCameraPermissionError()
                } else {
                    initCamera()
                }
            }
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        lifecycleScope.launch { 
            requiredPermissions = if (viewModel.storeImagesInGallery) {
                PERMISSIONS_GALLERY_STORAGE
            } else {
                PERMISSIONS_INTERNAL_APP_STORAGE
            }
            requestCameraPermission()
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.camera_dialog_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }
    
    override fun onStart() {
        super.onStart()
        
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.imageBufferCount.observe(viewLifecycleOwner, {})
        
        binding.captureButton.setOnClickListener { takePhoto() }

        binding.doneButton.setOnClickListener { 
            listener?.onDone(viewModel.imageBuffer.value ?: listOf())
            dismiss()
        }

        viewModel.snackbar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun requestCameraPermission() {
        context?.let { context ->
            val permissionsGranted = requiredPermissions.firstOrNull {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            } == null
            if (permissionsGranted) {
                initCamera()
                return
            }
            val showRationale = requiredPermissions.firstOrNull {
                shouldShowRequestPermissionRationale(it)
            } != null
            if (showRationale) {
                displayCameraPermissionError()
                return
            }
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }
    
    private fun displayCameraPermissionError() {
        context?.let {
            AlertDialog.Builder(it).setMessage(R.string.error_camera_permission)
                .setNegativeButton(R.string.error_button_negative) { _, _ ->
                    dismiss()
                }
                .setPositiveButton(R.string.error_camera_permission_settings) { _, _ ->
                    navigateToAppSystemSettings()
                    dismiss()
                }.create().show()
        } ?: throw IllegalStateException("Context cannot be null")
    }

    private fun navigateToAppSystemSettings() {
        context?.let {
            val appPackageName = it.packageName
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$appPackageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                it.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                AlertDialog.Builder(it).setMessage(R.string.error_settings)
                    .setNegativeButton(R.string.error_button_negative) { _, _ ->
                        dismiss()
                    }
            }
        } ?: throw IllegalStateException("Context cannot be null")
    }
    
    private fun initCamera() {

        context?.let {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
            
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }
                imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(it))
        }
    }

    private fun takePhoto() {
        context?.let { _context ->
            val imageCapture = imageCapture ?: return
            lifecycleScope.launch {
                val outputOptions = viewModel.getOutputOptions(_context)
                    .build()
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(_context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            Log.d(TAG, "Photo capture succeeded: ${output.savedUri}")
                            val path = output.savedUri?.let {
                                viewModel.getRealPathFromURI(_context, it)
                            } ?: viewModel.internalAppFilePath
                            path?.let {
                                viewModel.addImageToGalleryIfRequired(_context, it)
                                viewModel.saveImageToBuffer(it)
                            }
                        }
                    })
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        val imageCount = viewModel.imageBufferCount.value ?: 0
        if (imageCount > 0) {
            context?.let {
                AlertDialog.Builder(it).setMessage(
                    resources.getQuantityString(R.plurals.camera_cancel_message, imageCount))
                    .setNegativeButton(R.string.camera_cancel_message_button_negative) { _, _ ->
                        dismiss()
                    }
                    .setPositiveButton(R.string.camera_cancel_message_button_positive) { _, _ ->
                        listener?.onDone(viewModel.imageBuffer.value ?: listOf())
                        dismiss()
                    }.create().show()
            } ?: throw IllegalStateException("Context cannot be null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }
}
