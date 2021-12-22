package dev.katiebarnett.decktagram.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.CameraFragmentBinding
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class CameraFragment : Fragment() {

    companion object {
        private const val TAG = "CameraFragment"
        private val PERMISSIONS_INTERNAL_APP_STORAGE = arrayOf(
            Manifest.permission.CAMERA
        )
        private val PERMISSIONS_GALLERY_STORAGE = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    private val navigationId = R.id.CameraFragment

    private val viewModel: CameraViewModel by viewModels()

    private lateinit var binding: CameraFragmentBinding

    private val args: CameraFragmentArgs by navArgs()
    
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService
    
    private lateinit var requiredPermissions: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        binding = DataBindingUtil.inflate(inflater, R.layout.camera_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.cameraCaptureButton.setOnClickListener { takePhoto() }

        viewModel.snackbar.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.photoSaveResponse.observe(viewLifecycleOwner, {
            if (it != -1L) {
                findNavController().navigateUp()
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
                    findNavController().navigateUp()
                }
                .setPositiveButton(R.string.error_camera_permission_settings) { _, _ ->
                    navigateToAppSystemSettings()
                    findNavController().navigateUp()
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
                        findNavController().navigateUp()
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
                val outputOptions = viewModel.getOutputOptions(_context, args.deckId)
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
                                viewModel.saveImageToDatabase(args.deckId, it)
                            }
                        }
                    })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
