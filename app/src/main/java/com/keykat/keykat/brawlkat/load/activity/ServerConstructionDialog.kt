package com.keykat.keykat.brawlkat.load.activity
import android.app.Dialog
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.keykat.keykat.brawlkat.R


class ServerConstructionDialog {

    companion object {

        fun init(activity: Activity) {

            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.load_server_construction_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.findViewById<Button>(R.id.finishbutton).setOnClickListener {
                android.os.Process.killProcess(android.os.Process.myPid())
            }

            dialog.show()
        }
    }


}