package com.keykat.keykat.brawlkat;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class kat_ad {

    private                             AdLoader.Builder                        builder;
    private                             LayoutInflater                          layoutInflater;
    private                             Context                                 context;
    private                             RequestOptions                          options;

    public                              static int                              height;
    public                              static int                              width;

    public kat_ad(Context context){
        this.context = context;
    }

    public void init(){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;
    }

    public void setUnifiedNativeAdView(int unitId, ViewGroup parent, UnifiedNativeAd ad, int iconWidth, int iconHeight){

        UnifiedNativeAdView adView = (UnifiedNativeAdView)layoutInflater.inflate(unitId, null);

        ImageView iconView = adView.findViewById(R.id.kat_ad_layout_icon);
        TextView headView = adView.findViewById(R.id.kat_ad_layout_head);
        TextView bodyView = adView.findViewById(R.id.kat_ad_layout_body);
        TextView storeView = adView.findViewById(R.id.kat_ad_layout_store);
        TextView starView = adView.findViewById(R.id.kat_ad_layout_star);

        MediaView mediaView = adView.findViewById(R.id.kat_ad_layout_media);

        Button ActionButton;
        ImageView ActionButton2;
        if(unitId != R.layout.kat_ad_unified_battle_item_like_layout){
            ActionButton = adView.findViewById(R.id.kat_ad_layout_action);
            if(ad.getCallToAction() != null){
                ActionButton.setText(ad.getCallToAction().toLowerCase());
                adView.setCallToActionView(ActionButton);
            }
        }
        else{
            ActionButton2 = adView.findViewById(R.id.kat_ad_layout_action);
            if(ad.getCallToAction() != null){
                adView.setCallToActionView(ActionButton2);
            }
        }

        if(ad.getIcon() != null && iconView != null){
            iconView.setImageDrawable(ad.getIcon().getDrawable());
            adView.setIconView(iconView);
        }

        if(ad.getHeadline() != null && headView != null){
            headView.setText(ad.getHeadline());
            adView.setHeadlineView(headView);
        }

        if(ad.getBody() != null && bodyView != null){
            bodyView.setText(ad.getBody());
            adView.setBodyView(bodyView);
        }

        if(ad.getMediaContent() != null && mediaView != null){
            mediaView.setMediaContent(ad.getMediaContent());
            adView.setMediaView(mediaView);
        }

        if(ad.getStore() != null && storeView != null){
            storeView.setText(ad.getStore());
            adView.setStoreView(storeView);
        }

        if (ad.getStarRating() != null && starView != null) {
            starView.setText(ad.getStarRating().toString());
            adView.setStarRatingView(starView);
        }


        adView.setNativeAd(ad);

        if(unitId == R.layout.kat_ad_unified_battle_item_like_layout){

            LinearLayout layout = adView.findViewById(R.id.heightMediateLayout);

            LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT
            );

            int addDp1 = (int)PxToDp(SpToPx(18));
            int addDp2 = (int)PxToDp(SpToPx(14));

            adParams.setMargins(0,(width / 10) / 2 + 10 + addDp1,0,(width / 10) / 2 + 10 + addDp2);
            adParams.weight = 8.5f;
            layout.setLayoutParams(adParams);
        }

        parent.removeAllViews();

        parent.addView(adView);

        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        adView.setLayoutParams(mainParams);
    }

    public void build(final int unitId, final ViewGroup viewGroup){

        int iconWidth = 10, iconHeight = 10;
        if(unitId == R.layout.kat_ad_unified_banner_layout){
            iconWidth = 5; iconHeight = 5;
        }

        builder = new AdLoader.Builder(
                context, "ca-app-pub-5909086836185335/4388126945");


        final int decidedIConWidth = iconWidth; final int decideIconHeight = iconHeight;
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {


                setUnifiedNativeAdView(unitId, viewGroup, unifiedNativeAd, decidedIConWidth, decideIconHeight);
            }
        });
    }

    public void load(){
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private float SpToPx(float sp){
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }

    private float PxToDp(float px){
        return px / context.getResources().getDisplayMetrics().density;
    }
}
