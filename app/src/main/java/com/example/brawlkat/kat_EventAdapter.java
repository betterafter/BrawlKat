package com.example.brawlkat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class kat_EventAdapter extends RecyclerView.Adapter<kat_EventAdapter.viewHolder> {


    private                 Context                                             context;
    private                 ArrayList<kat_eventsParser.pair>                    EventArrayList;
    private                 ArrayList<HashMap<String, Object>>                  BrawlersArrayList;


    public kat_EventAdapter(Context context, ArrayList<kat_eventsParser.pair> EventArrayList,
                            ArrayList<HashMap<String, Object>> BrawlersArrayList){
        this.context = context;
        this.EventArrayList = EventArrayList;
        this.BrawlersArrayList = BrawlersArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.map_event_item, parent, false) ;

        System.out.println("EventArrayList size : " + EventArrayList.size());
        System.out.println("BralwerArrayList size : " + BrawlersArrayList.size());

        return new viewHolder(view, EventArrayList, BrawlersArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.fastImageLoad();
        holder.onBind(position);
        holder.onBrawlerRecommendsBind(position);
    }

    @Override
    public int getItemCount() {
        return EventArrayList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder{

        private View view;

        private ImageView background;
        private ImageView modeType;
        private TextView MapName_Time;
        private TextView MapType;
        private LinearLayout RecommendsLayout;
        private LinearLayout UserRecommendsLayout;
        private RequestOptions options;

        private ArrayList<kat_eventsParser.pair> EventArrayList;
        private ArrayList<HashMap<String, Object>> BrawlersArrayList;

        public viewHolder(@NonNull View itemView, ArrayList<kat_eventsParser.pair> EventArrayList,
                          ArrayList<HashMap<String, Object>> BrawlersArrayList) {
            super(itemView);

            view = itemView;
            background = (ImageView) itemView.findViewById(R.id.item_background);
            modeType = (ImageView) itemView.findViewById(R.id.item_modeType);
            MapName_Time = (TextView) itemView.findViewById(R.id.item_mapNameAndTime);
            MapType = (TextView) itemView.findViewById(R.id.item_mapType);
            RecommendsLayout = (LinearLayout) itemView.findViewById(R.id.item_RecommendsLayout);
            UserRecommendsLayout = (LinearLayout) itemView.findViewById(R.id.item_UserRecommendsLayout);
            this.EventArrayList = EventArrayList;
            this.BrawlersArrayList = BrawlersArrayList;
        }



        public void fastImageLoad(){
            options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .priority(Priority.HIGH)
                    .format(DecodeFormat.PREFER_RGB_565);
        }

        public void onBind(int position){

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int height = Math.min(metrics.heightPixels, metrics.widthPixels);
            int width = Math.max(metrics.heightPixels, metrics.widthPixels);

            String backgroundUrl = (String) EventArrayList.get(position).getInfo().get("mapTypeImageUrl");
            String gameModeTypeUrl = (String) EventArrayList.get(position).getInfo().get("gamemodeTypeImageUrl");
            String mapName = (String) EventArrayList.get(position).getInfo().get("mapName");
            String name = (String) EventArrayList.get(position).getInfo().get("name");

            MapName_Time.setText(mapName);
            MapType.setText(name);

            Glide.with(context)
                    .load(backgroundUrl).override(width / 2,height / 5)
                    .into(background);

            Glide.with(context)
                    .load(gameModeTypeUrl)
                    .override((width / 3) / 10,(width / 3) / 10)
                    .into(modeType);
        }

        public void onBrawlerRecommendsBind(int position){

            RecommendsLayout.removeAllViews();

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int height = Math.min(metrics.heightPixels, metrics.widthPixels);
            int width = Math.max(metrics.heightPixels, metrics.widthPixels);
            // 전체 추천 보여주기
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int i = 0;

            System.out.println(EventArrayList.get(position).getWins().size());

            if(EventArrayList.get(position).getWins().size() <= 0){

                TextView loadingText = new TextView(context);
                loadingText.setText("something wrong with datas or maybe lack of data!" + '\n' + "we are sorry for the inconveniences.");

                // ResourcesCompat.getFont 를 이용하여 폰트 불러오기.
                Typeface face = ResourcesCompat.getFont(context, R.font.lilita_one);
                loadingText.setTypeface(face);
                loadingText.setTextSize(20);
                loadingText.setTextColor(Color.BLACK);

                RecommendsLayout.addView(loadingText);
            }

            while(i < EventArrayList.get(position).getWins().size()){

                LinearLayout VerticalLayout = new LinearLayout(context);
                System.out.println(EventArrayList.get(position).getWins());

                for(int j = 0; j < 5; j++){

                    System.out.println("i : " + i + "   position : " + position);
                    //if(EventArrayList.get(position).getWins().size() == i) return;
                    if(EventArrayList.get(position).getWins().size() <= i) break;


                    View brawlerView = layoutInflater.inflate(R.layout.map_event_item_brawler, null);
                    String brawlerID = EventArrayList.get(position).getWins().get(i).get("brawler").toString();

                    int idx = 0;
                    while(true){
                        if(BrawlersArrayList.get(idx).get("id").toString().equals(brawlerID)) break;
                        idx++;
                    }

                    ImageView brawlerImage = brawlerView.findViewById(R.id.map_event_item_brawler_img);
                    TextView brawlerName = brawlerView.findViewById(R.id.map_event_item_brawler_name);
                    TextView brawlerWinRate = brawlerView.findViewById(R.id.map_event_item_brawler_winRate);

                    String brawlersImageUrl = (String) BrawlersArrayList.get(idx).get("imageUrl");
                    String brawlersName = (String) BrawlersArrayList.get(idx).get("name");

                    Glide.with(context)
                            .applyDefaultRequestOptions(options)
                            .load(brawlersImageUrl)
                            .override((width / 3) / 7,(width / 3) / 7)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    System.out.println("glide failed");
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    System.out.println("glide success");
                                    return false;
                                }
                            })
                            .into(brawlerImage);

                    brawlerName.setText(brawlersName);
                    double winRate = Double.parseDouble(EventArrayList.get(position).getWins().get(i).get("winRate").toString());
                    double round_winRate = Math.round(winRate * 100) / 100.0;
                    if(round_winRate * 100 % 10 == 0)
                        brawlerWinRate.setText("" + round_winRate + "0%");
                    else
                        brawlerWinRate.setText("" + round_winRate + "%");

                    VerticalLayout.addView(brawlerView);
                    i++;
                }


                RecommendsLayout.addView(VerticalLayout);
            }
        }
    }
}

