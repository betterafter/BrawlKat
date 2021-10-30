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
import com.keykat.keykat.brawlkat.common.util.KatData;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_BrawlerSelectionPopUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.brawler_select_popup);

        int width = (int) (KatData.SCREEN_WIDTH.intValue() * 0.9); //Display 사이즈의 70%
        int height = (int) (KatData.SCREEN_HEIGHT.intValue() * 0.7);  //Display 사이즈의 90%

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
        KatData.currentActivity = this;
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


        if(KatData.BrawlersArrayList.size() > 0){

            int idx = 0;
            while(idx < KatData.BrawlersArrayList.size()){

                LinearLayout VerticalLayout = new LinearLayout(getApplicationContext());

                VerticalLayout.setLayoutParams(params);


                for(int i = 0; i < 3; i++, idx++){
                    if(idx >= KatData.BrawlersArrayList.size()) break;

                    final String id = KatData.BrawlersArrayList.get(idx).get("id").toString();
                    String imageUrl = KatData.BrawlersArrayList.get(idx).get("imageUrl").toString();
                    final String name = KatData.BrawlersArrayList.get(idx).get("name").toString();

                    ImageButton button = new ImageButton(getApplicationContext());
                    button.setBackgroundColor(getResources().getColor(R.color.semiBlack));
                    button.setLayoutParams(params2);

                    KatData.glideImage(
                            getApplicationContext(),
                            imageUrl,
                            KatData.SCREEN_WIDTH.intValue() / 5,
                            KatData.SCREEN_WIDTH.intValue() / 5,
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
