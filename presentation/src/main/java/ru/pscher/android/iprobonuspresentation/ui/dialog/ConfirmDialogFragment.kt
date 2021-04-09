package ru.pscher.android.iprobonuspresentation.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import ru.pscher.android.iprobonuspresentation.R

class ConfirmDialogFragment() : DialogFragment() {

    companion object {
        private val TITLE_STRING_KEY = "TITLE_STRING"
        private val MESSAGE_STRING_KEY = "MESSAGE_STRING"
        private val BUTTON_YES_RES_KEY = "BUTTON_YES_RES_ID"
        private val BUTTON_NO_RES_KEY = "BUTTON_NO_RES_ID"
        private val IS_MESSAGE_HTML_KEY = "IS_MESSAGE_HTML_KEY"

        fun newInstance(
            titleString: String?,
            messageString: String?,
            buttonYes: Int,
            buttonNo: Int?,
            isMessageHtml: Boolean = false
        ): ConfirmDialogFragment {
            return ConfirmDialogFragment (titleString, messageString, buttonYes, buttonNo, isMessageHtml)
        }
    }

    private constructor(
        titleString: String?,
        messageString: String?,
        buttonYes: Int,
        buttonNo: Int?,
        isMessageHtml: Boolean = false
    ): this() {
        val arguments = Bundle()
        arguments.putString(TITLE_STRING_KEY, titleString)
        arguments.putString(MESSAGE_STRING_KEY, messageString)
        arguments.putInt(BUTTON_YES_RES_KEY, buttonYes)
        buttonNo?.let {
            arguments.putInt(BUTTON_NO_RES_KEY, it)
        }
        arguments.putBoolean(IS_MESSAGE_HTML_KEY, isMessageHtml)
        this.arguments = arguments
    }

    private val titleString: String?
        get() = requireArguments().getString(TITLE_STRING_KEY)
    private val messageString: String?
        get() = requireArguments().getString(MESSAGE_STRING_KEY)
    private val buttonYes: Int
        get() = requireArguments().getInt(BUTTON_YES_RES_KEY)
    private val buttonNo: Int?
        get() = if(requireArguments().containsKey(BUTTON_NO_RES_KEY)) requireArguments().getInt(BUTTON_NO_RES_KEY) else null
    private val isMessageHtml: Boolean
        get() = if(requireArguments().containsKey(IS_MESSAGE_HTML_KEY)) requireArguments().getBoolean(IS_MESSAGE_HTML_KEY) else false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_confirm, null)

        val title = dialogView.findViewById<TextView>(R.id.title)
        if(!titleString.isNullOrBlank()) {
            title.text = titleString
        } else {
            title.visibility = View.GONE
        }

        val message = dialogView.findViewById<TextView>(R.id.message)
        message.movementMethod = LinkMovementMethod.getInstance()
        if(!messageString.isNullOrBlank()) {
            //message.text = if(isMessageHtml) fromHtml(messageString) else messageString
            message.text = messageString
        } else {
            message.visibility = View.GONE
        }

        val positiveButton = dialogView.findViewById<MaterialButton>(R.id.buttonYes)
        positiveButton.setText(buttonYes)

        val negativeButton = dialogView.findViewById<MaterialButton>(R.id.buttonNo)
        if(buttonNo != null) {
            val buttonNoResId = buttonNo!!
            negativeButton.setText(buttonNoResId)
        } else {
            negativeButton.visibility = View.GONE
        }

        positiveButton.setOnClickListener {
            this.dismiss()
            sendResult(Activity.RESULT_OK)
        }
        negativeButton.setOnClickListener {
            this.dismiss()
            sendResult(Activity.RESULT_CANCELED)
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
    }

    private fun sendResult(resultCode: Int) {
        targetFragment?.let {
            val intent = Intent()
            it.onActivityResult(targetRequestCode, resultCode, intent)
        }
    }
}