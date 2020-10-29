package com.example.brawlkat.katfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.brawlkat.R;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_Player_RecentSearchActivity;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_SearchFragment extends Fragment {


    private             kat_Player_MainActivity                                                 kat_player_mainActivity;
    private             boolean                                                                 touchOutsideOfMyAccount = true;
    private             LinearLayout                                                            inputMyAccount;

    public kat_SearchFragment(kat_Player_MainActivity kat_player_mainActivity){
        this.kat_player_mainActivity = kat_player_mainActivity;
    }

    public static kat_SearchFragment newInstance(kat_Player_MainActivity kat_player_mainActivity){

        kat_SearchFragment fragment = new kat_SearchFragment(kat_player_mainActivity);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.player_main, container, false);
        view.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                System.out.println(    touchOutsideOfMyAccount);
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(touchOutsideOfMyAccount){
                        onMyAccountDecisionClick(inputMyAccount);
                    }
                    touchOutsideOfMyAccount = false;
                }

                return false;
            }
        });

        TextInputEditText player_user_search = (TextInputEditText) view.findViewById(R.id.player_user_searchInput);
        TextInputEditText player_club_search = (TextInputEditText) view.findViewById(R.id.player_club_searchInput);
        LinearLayout player_user_search_layout = (LinearLayout) view.findViewById(R.id.player_user_searchInput_layout);
        LinearLayout player_club_search_layout = (LinearLayout) view.findViewById(R.id.player_club_searchInput_layout);

        inputMyAccount = (LinearLayout) view.findViewById(R.id.player_main_inputMyAccount);
        inputMyAccount.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                touchOutsideOfMyAccount = true;
                return false;
            }
        });


        player_user_search_layout.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(kat_player_mainActivity, kat_Player_RecentSearchActivity.class);
                    intent.putExtra("type", "players");
                    kat_player_mainActivity.startActivity(intent);
                }

                return false;
            }
        });

        player_club_search_layout.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(kat_player_mainActivity, kat_Player_RecentSearchActivity.class);
                    intent.putExtra("type", "clubs");
                    kat_player_mainActivity.startActivity(intent);
                }
                return false;
            }
        });
        return view;
    }




    public void onMyAccountDecisionClick(View view){

        AnimationSet animationSet = new AnimationSet(true);

        Animation moveAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.move_to_corner);
        Animation sizeAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.size_down);

        animationSet.addAnimation(moveAnimation);
        animationSet.addAnimation(sizeAnimation);

        animationSet.setFillAfter(true);

        view.startAnimation(animationSet);

    }




}
