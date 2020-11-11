package com.example.brawlkat.kat_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.R;
import com.example.brawlkat.kat_Database.kat_favoritesDatabase;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_LoadingDialog;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_Player_PlayerDetailActivity;
import com.example.brawlkat.kat_Thread.kat_SearchThread;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_FavoritesFragment extends Fragment {

    private kat_Player_MainActivity kat_player_mainActivity;

    private             RequestOptions                                                          options;
    public              static int                                                              height;
    public              static int                                                              width;
    private             GridView                                                                gridView;


    public kat_FavoritesFragment(kat_Player_MainActivity kat_player_mainActivity){
        this.kat_player_mainActivity = kat_player_mainActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_favorites, null);
        kat_favoritesDatabase database = kat_LoadBeforeMainActivity.kataFavoritesBase;
        ArrayList<ArrayList<String>> databaseItem = database.getItem();

        gridView = view.findViewById(R.id.player_favorites_gridview);
        gridAdapter gridAdapter = new gridAdapter(databaseItem);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        return view;
    }


    private class gridAdapter extends BaseAdapter{

        private ArrayList<ArrayList<String> >   databaseItem;
        private String                          url_icon_trophies = "https://www.starlist.pro/assets/icon/trophy.png";

        public gridAdapter(ArrayList<ArrayList<String>> databaseItem){
            this.databaseItem = databaseItem;
        }

        // count 0으로 냅두면 화면에 표시가 안됨
        @Override
        public int getCount() {
            return databaseItem.size();
        }

        @Override
        public Object getItem(int i) {
            return databaseItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.player_favorites_item, viewGroup, false);

            final int moveIdx = i;
            view.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    kat_LoadingDialog dialog = new kat_LoadingDialog(getActivity());
                    dialog.show();

                    String tag = databaseItem.get(moveIdx).get(2).substring(1);
                    kat_SearchThread kset = new kat_SearchThread(getActivity(),
                            kat_Player_PlayerDetailActivity.class, dialog);
                    kset.SearchStart(tag, "players");
                }
            });

            ImageView player_image = view.findViewById(R.id.player_favorites_image);
            TextView player_level = view.findViewById(R.id.player_favorites_level);
            TextView player_name = view.findViewById(R.id.player_favorites_name);
            ImageView player_trophies_image = view.findViewById(R.id.player_favorites_trophies_image);
            TextView player_trophies = view.findViewById(R.id.player_favorites_trophies);
            final TextView player_tag = view.findViewById(R.id.player_favorites_tag);
            ImageView player_close = view.findViewById(R.id.player_favorites_close);

            String url_profile = "https://www.starlist.pro/assets/profile/" + databaseItem.get(i).get(6) + ".png?v=1";
            GlideImageWithRoundCorner(url_profile, width / 5, height / 5, player_image);
            player_level.setText(databaseItem.get(i).get(7));
            player_name.setText(databaseItem.get(i).get(3));
            GlideImageWithRoundCorner(url_icon_trophies, width / 30, height / 30, player_trophies_image);
            player_trophies.setText(databaseItem.get(i).get(4) + " / " + databaseItem.get(i).get(5));
            player_tag.setText(databaseItem.get(i).get(2));

            final int removeIdx = i;
            player_close.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    String tag = databaseItem.get(removeIdx).get(2);
                    databaseItem.remove(removeIdx);
                    kat_LoadBeforeMainActivity.kataFavoritesBase.delete(tag);
                    gridAdapter.this.notifyDataSetChanged();
                }
            });

            return view;
        }
    }


    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getActivity().getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    public void GlideImageWithRoundCorner(String url, int width, int height, ImageView view){
        Glide.with(getActivity().getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .apply(new RequestOptions().circleCrop().circleCrop())
                .override(width, height)
                .into(view);


    }
}
