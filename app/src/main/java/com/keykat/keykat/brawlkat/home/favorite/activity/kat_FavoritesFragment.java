package com.keykat.keykat.brawlkat.home.favorite.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.util.kat_ad;
import com.keykat.keykat.brawlkat.searchresult.player.activity.kat_Player_PlayerDetailActivity;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.database.kat_favoritesDatabase;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_FavoritesFragment extends Fragment {


    private RequestOptions options;
    public static int height;
    public static int width;

    private GridView gridView;
    public static gridAdapter gridAdapter;

    private ArrayList<ArrayList<String>> databaseItem;


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

        final View view = inflater.inflate(R.layout.fragment_player_favorites, null);

        // 광고 ....................................................................................

        LinearLayout test = view.findViewById(R.id.testlayout);
        kat_ad ad = new kat_ad(getActivity().getApplicationContext());
        ad.init();
        ad.build(R.layout.kat_ad_unified_banner_layout, test);
        ad.load();

        //..........................................................................................


        kat_favoritesDatabase database = KatData.kataFavoritesBase;
        databaseItem = database.getItem();

        gridView = view.findViewById(R.id.player_favorites_gridview);
        gridAdapter = new gridAdapter(databaseItem);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        return view;
    }

    public void refresh() {
        gridAdapter.refreshData();
        gridAdapter.notifyDataSetChanged();
    }


    private class gridAdapter extends BaseAdapter {

        public ArrayList<ArrayList<String>> databaseItem;
        private String url_icon_trophies = KatData.CdnRootUrl + "/assets/icon/trophy.png";

        public gridAdapter(ArrayList<ArrayList<String>> databaseItem) {
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

        public void refreshData() {
            databaseItem = KatData.kataFavoritesBase.getItem();
        }

        // 아이템 뷰 디자인
        @SuppressLint({"SetTextI18n", "ViewHolder"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater =
                    (LayoutInflater) requireActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_player_favorites, viewGroup, false);
            LinearLayout backgroundLayout = view.findViewById(R.id.player_favorites_background_layout);

            backgroundLayout.setBackgroundColor(Color.parseColor("#282830"));


            // 그리드뷰 아이템 클릭
            final int moveIdx = i;
            view.setOnClickListener(view12 -> {
                if(KatData.dialog != null) KatData.dialog.show();

                String tag = databaseItem.get(moveIdx).get(2).substring(1);
                kat_SearchThread kset = new kat_SearchThread(getActivity(),
                        kat_Player_PlayerDetailActivity.class);
                kset.SearchStart(tag, "players", requireActivity().getApplicationContext());
            });


            ImageView player_image = view.findViewById(R.id.player_favorites_image);
            TextView player_level = view.findViewById(R.id.player_favorites_level);
            TextView player_name = view.findViewById(R.id.player_favorites_name);
            ImageView player_trophies_image = view.findViewById(R.id.player_favorites_trophies_image);
            TextView player_trophies = view.findViewById(R.id.player_favorites_trophies);
            final TextView player_tag = view.findViewById(R.id.player_favorites_tag);
            ImageView player_close = view.findViewById(R.id.player_favorites_close);

            String url_profile = KatData.WebRootUrl + "/assets/profile/" + databaseItem.get(i).get(6) + ".png?v=1";
            KatData.glideImageWithRoundCorner(
                    requireActivity().getApplicationContext(),
                    url_profile,
                    KatData.SCREEN_WIDTH.intValue() / 8,
                    KatData.SCREEN_WIDTH.intValue() / 8,
                    player_image
            );

            player_level.setText(databaseItem.get(i).get(7));
            player_name.setText(databaseItem.get(i).get(3));
            KatData.glideImageWithRoundCorner(
                    requireActivity().getApplicationContext(),
                    url_icon_trophies,
                    KatData.SCREEN_WIDTH.intValue() / 30,
                    KatData.SCREEN_HEIGHT.intValue() / 30,
                    player_trophies_image
            );

            player_trophies.setText(databaseItem.get(i).get(4) + " / " + databaseItem.get(i).get(5));
            player_tag.setText(databaseItem.get(i).get(2));

            player_name.setTextColor(getResources().getColor(R.color.Color1));
            Drawable drawable = player_tag.getBackground();
            drawable.setTint(getResources().getColor(R.color.semiBlack));


            // 그리드뷰 닫기 버튼 클릭
            final int removeIdx = i;
            player_close.setOnClickListener(view1 -> {
                String tag = databaseItem.get(removeIdx).get(2);
                databaseItem.remove(removeIdx);
                KatData.kataFavoritesBase.delete(tag);
                gridAdapter.this.notifyDataSetChanged();
            });

            return view;
        }
    }
}
