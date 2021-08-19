package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.util.kat_Data;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_BrawlerSelectionPopUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.brawler_select_popup);

        int width = (int) (kat_Data.SCREEN_WIDTH.intValue() * 0.9); //Display 사이즈의 70%
        int height = (int) (kat_Data.SCREEN_HEIGHT.intValue() * 0.7);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;
    }

    @Override
    protected void onStart() {
        super.onStart();

        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        kat_Data.currentActivity = this;
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


        if(kat_Data.BrawlersArrayList.size() > 0){

            int idx = 0;
            while(idx < kat_Data.BrawlersArrayList.size()){

                LinearLayout VerticalLayout = new LinearLayout(getApplicationContext());

                VerticalLayout.setLayoutParams(params);


                for(int i = 0; i < 3; i++, idx++){
                    if(idx >= kat_Data.BrawlersArrayList.size()) break;

                    final String id = kat_Data.BrawlersArrayList.get(idx).get("id").toString();
                    String imageUrl = kat_Data.BrawlersArrayList.get(idx).get("imageUrl").toString();
                    final String name = kat_Data.BrawlersArrayList.get(idx).get("name").toString();

                    ImageButton button = new ImageButton(getApplicationContext());
                    button.setBackgroundColor(getResources().getColor(R.color.semiBlack));
                    button.setLayoutParams(params2);

                    kat_Data.GlideImage(
                            getApplicationContext(),
                            imageUrl,
                            kat_Data.SCREEN_WIDTH.intValue() / 5,
                            kat_Data.SCREEN_WIDTH.intValue() / 5,
                            button
                    );

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
}
