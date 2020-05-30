package com.moor.scannit.ui.ocr

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import com.moor.scannit.R

class LoadingDialog(val context: Context) {


    private var dialog: AlertDialog? = null

    fun startLoadingDialog(){
        val builder = AlertDialog.Builder(context)
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
        dialog= builder.create().also {
            it.show()
        }
    }

    fun stopLoadingDialog(){
        dialog?.dismiss()
    }
}