package com.keykat.keykat.brawlkat.home.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.keykat.keykat.brawlkat.R;

import java.io.Serializable;

public class kat_LoadingDialog implements Serializable {

    private                 Context                 context;
    private                 Dialog                  dialog;

    public kat_LoadingDialog(Context context){
        this.context = context;

        SetDialog();
    }

    public void SetDialog(){

        LayoutInflater layoutInflater
                = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = layoutInflater.inflate(R.layout.loading_dialog, null);
        ImageView loadIcon = dialogView.findViewById(R.id.load_icon);

        Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.loading_rotation);
        loadIcon.startAnimation(animation);


        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);


    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
