package com.keykat.keykat.brawlkat.common.ui

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.keykat.keykat.brawlkat.R

class PlayerNotFoundDialog {

    companion object {
        fun init(activity: Activity) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_player_not_found)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.findViewById<Button>(R.id.btn_player_not_found_close).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}

class ClubNotFoundDialog {

    companion object {
        fun init(activity: Activity) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_club_not_found)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.findViewById<Button>(R.id.btn_player_not_found_close).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}
