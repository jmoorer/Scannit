package com.moor.scannit.ui.ocr


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moor.scannit.BuildConfig
import com.moor.scannit.R
import com.moor.scannit.exportFolder
import java.io.File
import java.io.FileWriter
import java.util.*

class DownloadDialog(private val context: Context){

    private lateinit var dialog: AlertDialog

    fun startDialog(text: String){


        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_export, null)
        val editText:EditText? =dialogView.findViewById(R.id.file_name_text_view)
        editText?.setText("${UUID.randomUUID()}.txt")
        dialog=MaterialAlertDialogBuilder(context)
            .setTitle("File name")
            .setView(dialogView)
            .setPositiveButton("Save"){_,_->
                if (!editText?.text.isNullOrBlank()){
                    val fileName= editText?.text.toString()
                    if(!fileName.endsWith(".txt"))
                        fileName.plus(".text")

                    val file = File(context.exportFolder, fileName)
                    val writer = FileWriter(file)
                    writer.write(text)
                    writer.flush()
                    writer.close()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val uri: Uri? = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file)
                    intent.setDataAndType(uri, "text/plain")
                    context.startActivity(intent)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->

            }
            .create()


            dialog.show()

    }


}