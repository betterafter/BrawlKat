//package com.keykat.keykat.brawlkat.service.maprecommendservice.ui;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.Priority;
//import com.bumptech.glide.load.DecodeFormat;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.request.RequestOptions;
//import com.keykat.keykat.brawlkat.R;
//import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendContract;
//import com.keykat.keykat.brawlkat.service.model.data.NotificationData;
//import com.keykat.keykat.brawlkat.common.util.KatData;
//import com.keykat.keykat.brawlkat.common.util.parser.kat_eventsParser;
//import com.keykat.keykat.brawlkat.common.util.parser.kat_official_playerInfoParser;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Objects;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.res.ResourcesCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class EventAdapter
//        extends RecyclerView.Adapter<EventAdapter.ViewHolder>
//        implements MapRecommendContract.RecyclerView {
//
//    private final Context context;
//    private ArrayList<kat_eventsParser.pair> eventArrayList;
//    private ArrayList<HashMap<String, Object>> brawlersArrayList;
//    private ArrayList<String> playerBrawlersArrayList;
//    private Boolean isUserRecommend = false;
//
//
//    public EventAdapter(Context context,
//                        ArrayList<kat_eventsParser.pair> EventArrayList,
//                        ArrayList<HashMap<String, Object>> BrawlersArrayList
//    ) {
//        this.context = context;
//        this.eventArrayList = EventArrayList;
//        this.brawlersArrayList = BrawlersArrayList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater
//                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.service_map_event_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.fastImageLoad();
//        holder.onBind(position, eventArrayList, brawlersArrayList);
//        holder.onBrawlerRecommendsBind(position, eventArrayList, brawlersArrayList);
//    }
//
//    @Override
//    public int getItemCount() {
//        return eventArrayList.size();
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    @Override
//    public void updateRecommendState(boolean state) {
//        isUserRecommend = state;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    @SuppressLint("NotifyDataSetChanged")
//    public void refresh(@NonNull NotificationData notificationData) {
//        this.eventArrayList = notificationData.getEventArrayList();
//        this.brawlersArrayList = notificationData.getBrawlerArrayList();
//        notifyDataSetChanged();
//    }
//
//
//    public class viewHolder extends RecyclerView.ViewHolder {
//
//        private final ImageView background;
//        private final ImageView modeType;
//        private final TextView MapName_Time;
//        private final TextView MapType;
//        private final LinearLayout RecommendsLayout;
//        private RequestOptions options;
//
//        public viewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            background = itemView.findViewById(R.id.item_background);
//            modeType = itemView.findViewById(R.id.item_modeType);
//            MapName_Time = itemView.findViewById(R.id.item_mapNameAndTime);
//            MapType = itemView.findViewById(R.id.item_mapType);
//            RecommendsLayout = itemView.findViewById(R.id.item_RecommendsLayout);
//        }
//
//        public void fastImageLoad() {
//            options = new RequestOptions()
//                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                    .centerCrop()
//                    .priority(Priority.HIGH)
//                    .format(DecodeFormat.PREFER_RGB_565);
//        }
//
//        // 기본 이미지 세팅
//        public void onBind(
//                int position,
//                ArrayList<kat_eventsParser.pair> eventArrayList,
//                ArrayList<HashMap<String, Object>> brawlersArrayList
//        ) {
//
//            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//            int height = Math.min(metrics.heightPixels, metrics.widthPixels);
//            int width = Math.max(metrics.heightPixels, metrics.widthPixels);
//
//            String backgroundUrl
//                    = (String) eventArrayList.get(position).getInfo().get("mapTypeImageUrl");
//            String gameModeTypeUrl
//                    = (String) eventArrayList.get(position).getInfo().get("gameModeTypeImageUrl");
//            String mapName = (String) eventArrayList.get(position).getInfo().get("mapName");
//            String name = (String) eventArrayList.get(position).getInfo().get("name");
//
//            MapName_Time.setText(mapName);
//            MapType.setText(name);
//
//            Glide.with(context)
//                    .load(backgroundUrl)
//                    .override(width / 2, height / 5)
//                    .into(background);
//
//            Glide.with(context)
//                    .load(gameModeTypeUrl)
//                    .override((width / 3) / 10, (width / 3) / 10)
//                    .into(modeType);
//        }
//
//        // 전체 브롤러 추천 뷰
//        @SuppressLint("SetTextI18n")
//        public void onBrawlerRecommendsBind(
//                int position,
//                ArrayList<kat_eventsParser.pair> eventArrayList,
//                ArrayList<HashMap<String, Object>> brawlersArrayList
//        ) {
//            RecommendsLayout.removeAllViews();
//
//            if (isUserRecommend) {
//                playerBrawlersArrayList = new ArrayList<>();
//                if (KatData.eventsPlayerData.getValue() != null) {
//                    ArrayList<kat_official_playerInfoParser.playerBrawlerData> data
//                            = KatData.eventsPlayerData.getValue().getBrawlerData();
//
//                    for (int i = 0; i < data.size(); i++) {
//                        playerBrawlersArrayList.add(data.get(i).getName());
//                    }
//                }
//            }
//
//            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//            int width = Math.max(metrics.heightPixels, metrics.widthPixels);
//            // 전체 추천 보여주기
//            LayoutInflater layoutInflater
//                    = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            int i = 0;
//
//            if (eventArrayList.get(position).getWins().size() <= 0) {
//
//                TextView loadingText = new TextView(context);
//                loadingText.setText(context.getString(R.string.overdrawErrorMessage));
//
//                // ResourcesCompat.getFont 를 이용하여 폰트 불러오기.
//                Typeface face = ResourcesCompat.getFont(context, R.font.lilita_one);
//                loadingText.setTypeface(face);
//                loadingText.setTextSize(20);
//                loadingText.setTextColor(Color.BLACK);
//
//                RecommendsLayout.addView(loadingText);
//            }
//
//            while (i < eventArrayList.get(position).getWins().size()) {
//
//                LinearLayout VerticalLayout = new LinearLayout(context);
//
//                for (int j = 0; j < 5; j++) {
//
//                    if (eventArrayList.get(position).getWins().size() <= i) break;
//
//
//                    @SuppressLint("InflateParams")
//                    View brawlerView
//                            = layoutInflater.inflate(
//                            R.layout.service_map_event_item_brawler, null
//                    );
//                    String brawlerID = Objects.requireNonNull(
//                            eventArrayList.get(position).getWins().get(i).get("brawler")
//                    ).toString();
//
//                    int idx = 0;
//                    boolean BrawlerFoundInUserRecommend = false;
//                    while (true) {
//                        // brawlersArrayList 에서 현재 순위의 브롤러를 찾았을 때
//                        if (Objects.requireNonNull(
//                                brawlersArrayList.get(idx).get("id")).toString().equals(brawlerID)
//                        ) {
//                            if (isUserRecommend) {
//                                for (int k = 0; k < playerBrawlersArrayList.size(); k++) {
//                                    String playerBrawler
//                                            = playerBrawlersArrayList.get(k).toLowerCase();
//
//                                    if (playerBrawler.equals(
//                                            Objects.requireNonNull(
//                                                    brawlersArrayList.get(idx).get("name")
//                                            ).toString().toLowerCase())
//                                    ) {
//                                        BrawlerFoundInUserRecommend = true;
//                                        break;
//                                    }
//                                }
//                            } else BrawlerFoundInUserRecommend = true;
//                            break;
//                        }
//                        idx++;
//                    }
//                    if (!BrawlerFoundInUserRecommend) {
//                        j = j - 1;
//                        i++;
//                        continue;
//                    }
//
//                    ImageView brawlerImage
//                            = brawlerView.findViewById(R.id.map_event_item_brawler_img);
//                    TextView brawlerName
//                            = brawlerView.findViewById(R.id.map_event_item_brawler_name);
//                    TextView brawlerWinRate
//                            = brawlerView.findViewById(R.id.map_event_item_brawler_winRate);
//
//                    String brawlersImageUrl = (String) brawlersArrayList.get(idx).get("imageUrl");
//                    String brawlersName = (String) brawlersArrayList.get(idx).get("name");
//
//                    Glide.with(context)
//                            .applyDefaultRequestOptions(options)
//                            .load(brawlersImageUrl)
//                            .override((width / 3) / 7, (width / 3) / 7)
//                            .into(brawlerImage);
//
//                    brawlerName.setText(brawlersName);
//                    double winRate = Double.parseDouble(
//                            Objects.requireNonNull(
//                                    eventArrayList.get(position).getWins().get(i).get("winRate")
//                            ).toString());
//                    double round_winRate = Math.round(winRate * 100) / 100.0;
//
//                    if (round_winRate * 100 % 10 == 0)
//                        brawlerWinRate.setText("" + round_winRate + "0%");
//                    else
//                        brawlerWinRate.setText("" + round_winRate + "%");
//
//                    VerticalLayout.addView(brawlerView);
//                    i++;
//                }
//
//
//                RecommendsLayout.addView(VerticalLayout);
//            }
//        }
//    }
//}
//
