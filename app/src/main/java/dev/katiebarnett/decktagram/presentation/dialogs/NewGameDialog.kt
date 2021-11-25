package dev.katiebarnett.decktagram.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import dev.katiebarnett.decktagram.R
import dev.katiebarnett.decktagram.databinding.NewGameDialogBinding

class NewGameDialog : DialogFragment() {
    
    companion object {
        const val TAG = "NewGameDialog"
    }
    
    internal var listener: DialogListener? = null
    
    private lateinit var binding: NewGameDialogBinding

    interface DialogListener {
        fun onDialogPositiveClick(gameName: String)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            binding = NewGameDialogBinding.inflate(LayoutInflater.from(context))
            builder.setView(binding.root)
                // Add action buttons
                .setPositiveButton(R.string.game_dialog_button_save) { dialog, id ->
                    listener?.onDialogPositiveClick(binding.nameField.editText?.text.toString().trim())
                }
                .setNegativeButton(R.string.game_dialog_button_cancel) { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    
    fun setListener(listener: DialogListener) {
        this.listener = listener
    }
}