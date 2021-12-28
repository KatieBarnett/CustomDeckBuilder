package dev.katiebarnett.decktagram.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.ViewImageDialogFragmentBinding
import dev.katiebarnett.decktagram.util.ViewImageScreen
import dev.katiebarnett.decktagram.util.logScreenView
import javax.inject.Inject

@AndroidEntryPoint
class ViewImageDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "ViewImageDialogFragment"
        
        private const val ARG_IMAGE_URL = "imageUrl"
        private const val ARG_IMAGE_NAME = "imageName"
        
        fun newInstance(imageUrl: String, imageName: String?): ViewImageDialogFragment {
            val dialog = ViewImageDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            args.putString(ARG_IMAGE_NAME, imageName)
            dialog.arguments = args
            return dialog
        }
    }

    @Inject
    lateinit var analytics: FirebaseAnalytics

    private lateinit var binding: ViewImageDialogFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Decktagram_FullScreenDialog)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_image_dialog_fragment, container, false)
        binding.imageUrl = arguments?.getString(ARG_IMAGE_URL)
        binding.imageName = arguments?.getString(ARG_IMAGE_NAME)
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
        
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(ViewImageScreen)
    }
}
