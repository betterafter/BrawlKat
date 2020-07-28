package com.example.brawlkat;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
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
        return new viewHolder(view);
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
        private LinearLayout RecommendsLayout;
        private LinearLayout UserRecommendsLayout;
        private RequestOptions options;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            background = (ImageView) itemView.findViewById(R.id.item_background);
            modeType = (ImageView) itemView.findViewById(R.id.item_modeType);
            MapName_Time = (TextView) itemView.findViewById(R.id.item_mapNameAndTime);
            RecommendsLayout = (LinearLayout) itemView.findViewById(R.id.item_RecommendsLayout);
            UserRecommendsLayout = (LinearLayout) itemView.findViewById(R.id.item_UserRecommendsLayout);
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
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;

            String backgroundUrl = (String) EventArrayList.get(position).getInfo().get("mapTypeImageUrl");
            String gameModeTypeUrl = (String) EventArrayList.get(position).getInfo().get("gamemodeTypeImageUrl");
            String name = (String) EventArrayList.get(position).getInfo().get("name");
            String startTime = (String) EventArrayList.get(position).getInfo().get("startTime");
            String endTime = (String) EventArrayList.get(position).getInfo().get("endTime");

            MapName_Time.setText(name + '\n' + endTime);

            Glide.with(context)
                    .applyDefaultRequestOptions(options)
                    .load(backgroundUrl).override(width / 3,height / 5)
                    .into(background);

            Glide.with(context)
                    .applyDefaultRequestOptions(options)
                    .load(gameModeTypeUrl)
                    .override((width / 3) / 10,(width / 3) / 10)
                    .into(modeType);
        }

        public void onBrawlerRecommendsBind(int position){

            RecommendsLayout.removeAllViews();

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            // 전체 추천 보여주기
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int i = 0;
            while(i < 10){
                LinearLayout VerticalLayout = new LinearLayout(context);

                for(int j = 0; j < 5; j++){

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
                            .override((width / 3) / 5,(width / 3) / 5)
                            .into(brawlerImage);

                    brawlerName.setText(brawlersName);
                    brawlerWinRate.setText(EventArrayList.get(position).getWins().get(i).get("winRate").toString());

                    VerticalLayout.addView(brawlerView);
                    i++;
                }


                RecommendsLayout.addView(VerticalLayout);
            }
        }
    }
}

