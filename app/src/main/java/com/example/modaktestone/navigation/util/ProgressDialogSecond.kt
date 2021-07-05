package com.example.modaktestone.navigation.util

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.example.modaktestone.R

class ProgressDialogSecond(context: Context): Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.item_progress_dialog)
    }

}