package dev.katiebarnett.customdeckbuilder.presentation

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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.customdeckbuilder.R
import dev.katiebarnett.customdeckbuilder.databinding.CameraFragmentBinding

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private lateinit var binding: CameraFragmentBinding
    
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    initCamera()
                } else {
                    displayCameraPermissionError()
                }
            }

        requestCameraPermission()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.camera_fragment, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
    
    private fun requestCameraPermission() {

        context?.let {
            // TODO: Also request storage permissions
            context
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                    initCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    displayCameraPermissionError()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
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
        Log.d("TAG","Camera allowed")
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}