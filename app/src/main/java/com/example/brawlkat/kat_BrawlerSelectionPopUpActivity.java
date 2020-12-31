package com.example.brawlkat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.kat_Fragment.kat_RankingFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_BrawlerSelectionPopUpActivity extends AppCompatActivity {

    public                          int                                  ScreenHeight;
    public                          int                                  ScreenWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.brawler_select_popup);


        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.9); //Display 사이즈의 70%
        int height = (int) (display.getHeight() * 0.7);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        ScreenHeight = metrics.heightPixels;
        ScreenWidth = metrics.widthPixels;
    }

    @Override
    protected void onStart() {
        super.onStart();

        setView();
    }

    public void setView(){

        LinearLayout linearLayout = findViewById(R.id.brawler_select_popup_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(5,5,5,5);


        if(kat_LoadBeforeMainActivity.BrawlersArrayList.size() > 0){

            int idx = 0;
            while(idx < kat_LoadBeforeMainActivity.BrawlersArrayList.size()){

                LinearLayout VerticalLayout = new LinearLayout(getApplicationContext());

                VerticalLayout.setLayoutParams(params);


                for(int i = 0; i < 3; i++, idx++){
                    if(idx >= kat_LoadBeforeMainActivity.BrawlersArrayList.size()) break;

                    final String id = kat_LoadBeforeMainActivity.BrawlersArrayList.get(idx).get("id").toString();
                    String imageUrl = kat_LoadBeforeMainActivity.BrawlersArrayList.get(idx).get("imageUrl").toString();
                    final String name = kat_LoadBeforeMainActivity.BrawlersArrayList.get(idx).get("name").toString();

                    ImageButton button = new ImageButton(getApplicationContext());
                    button.setBackgroundColor(getResources().getColor(R.color.semiBlack));
                    button.setLayoutParams(params2);

                    GlideImage(imageUrl, ScreenWidth / 5, ScreenWidth / 5, button);

                    button.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View view){
                            ChangeFinish(id, name);
                        }
                    });

                    VerticalLayout.addView(button);
                }

                linearLayout.addView(VerticalLayout);
            }
        }
    }

    public void ChangeFinish(String BrawlerId, String BrawlerName){
        Intent intent = new Intent();
        intent.putExtra("BrawlerId", BrawlerId);
        intent.putExtra("BrawlerName", BrawlerName);
        setResult(1113, intent);
        finish();
    }

    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(kat_RankingFragment.options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    public void GlideImageWithRoundCorner(String url, int width, int height, ImageView view){
        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(kat_RankingFragment.options)
                .load(url)
                .apply(new RequestOptions().circleCrop().circleCrop())
                .override(width, height)
                .into(view);


    }

}
