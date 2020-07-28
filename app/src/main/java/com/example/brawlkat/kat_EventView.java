package com.example.brawlkat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class kat_EventView extends View {

    private         Context                             context;
    private         AttributeSet                        attrs;
    private         int                                 defStyle;
    private         View                                view;
    private         View                                bralwersView;
    private         ArrayList<kat_eventsParser.pair>    EventArrayList = new ArrayList<>();
    private         LayoutInflater                      layoutInflater;
    private         LayoutInflater                      mapEventItemBralwerInflater;
    private         int                                 idx;


    public kat_EventView(Context context, ArrayList<kat_eventsParser.pair> EventArrayList){
        super(context);
        this.context = context;
        this.EventArrayList = EventArrayList;
        init();
    }

    public kat_EventView(Context context, ArrayList<kat_eventsParser.pair> EventArrayList, int idx){
        super(context);
        this.context = context;
        this.EventArrayList = EventArrayList;
        this.idx = idx;
        init(); initView();
    }

    public kat_EventView(Context context, AttributeSet attrs, ArrayList<kat_eventsParser.pair> EventArrayList, int idx){
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        this.EventArrayList = EventArrayList;
        this.idx = idx;
        init(); initView();
    }

    public kat_EventView(Context context, AttributeSet attrs, int defStyle, ArrayList<kat_eventsParser.pair> EventArrayList, int idx){
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        this.EventArrayList = EventArrayList;
        this.idx = idx;
        init(); initView();
    }

    private void init(){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.map_event_item, null);

        mapEventItemBralwerInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bralwersView = mapEventItemBralwerInflater.inflate(R.layout.map_event_item_brawler, null);

    }


    public void initView(){

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        ImageView background = (ImageView) view.findViewById(R.id.item_background);
        ImageView modeType = (ImageView) view.findViewById(R.id.item_modeType);
        TextView MapName_Time = (TextView) view.findViewById(R.id.item_mapNameAndTime);
        LinearLayout RecommendsLayout = (LinearLayout) view.findViewById(R.id.item_RecommendsLayout);
        LinearLayout UserRecommendsLayout = (LinearLayout) view.findViewById(R.id.item_UserRecommendsLayout);

        String backgroundUrl = (String) EventArrayList.get(idx).getInfo().get("mapTypeImageUrl");
        String gameModeTypeUrl = (String) EventArrayList.get(idx).getInfo().get("gamemodeTypeImageUrl");

        Glide.with(context).load(backgroundUrl).override(width / 3,height / 5).into(background);
        Glide.with(context).load(gameModeTypeUrl).override((width / 3) / 5,(width / 3) / 5).into(modeType);



    }

    public void initView(int idx){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        ImageView background = (ImageView) view.findViewById(R.id.item_background);
        ImageView modeType = (ImageView) view.findViewById(R.id.item_modeType);
        TextView MapName_Time = (TextView) view.findViewById(R.id.item_mapNameAndTime);
        LinearLayout RecommendsLayout = (LinearLayout) view.findViewById(R.id.item_RecommendsLayout);
        LinearLayout UserRecommendsLayout = (LinearLayout) view.findViewById(R.id.item_UserRecommendsLayout);

        String backgroundUrl = (String) EventArrayList.get(idx).getInfo().get("mapTypeImageUrl");
        String gameModeTypeUrl = (String) EventArrayList.get(idx).getInfo().get("gamemodeTypeImageUrl");

        Glide.with(context).load(backgroundUrl).override(width / 3,height / 5).into(background);
        Glide.with(context).load(gameModeTypeUrl).override((width / 3) / 5,(width / 3) / 5).into(modeType);



    }

    public View getView(){
        return this.view;
    }
}
