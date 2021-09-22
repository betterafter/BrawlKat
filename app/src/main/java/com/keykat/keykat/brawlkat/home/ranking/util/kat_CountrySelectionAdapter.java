package com.keykat.keykat.brawlkat.home.ranking.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_CountrySelectionPopUpActivity;
import com.keykat.keykat.brawlkat.util.KatData;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class kat_CountrySelectionAdapter extends RecyclerView.Adapter<kat_CountrySelectionAdapter.ViewHolder> {

    ArrayList<kat_CountrySelectionPopUpActivity.Pair> list;
    ArrayList<kat_CountrySelectionPopUpActivity.Pair> rawList = new ArrayList<>();

    kat_CountrySelectionPopUpActivity kat_countrySelectionPopUpActivity;

    public kat_CountrySelectionAdapter(ArrayList<kat_CountrySelectionPopUpActivity.Pair> list){
        this.list = list;
        rawList.addAll(list);
    }

    public kat_CountrySelectionAdapter(ArrayList<kat_CountrySelectionPopUpActivity.Pair> list,
                                       kat_CountrySelectionPopUpActivity kat_countrySelectionPopUpActivity){
        this.list = list;
        this.kat_countrySelectionPopUpActivity = kat_countrySelectionPopUpActivity;
        rawList.addAll(list);
    }

    @NonNull
    @Override
    public kat_CountrySelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.country_select_popup_item, parent, false) ;
        kat_CountrySelectionAdapter.ViewHolder vh = new kat_CountrySelectionAdapter.ViewHolder(view) ;
        return vh;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String key, String value){

            TextView country_select_key = itemView.findViewById(R.id.country_select_popup_key);
            Button country_select_value = itemView.findViewById(R.id.country_select_popup_value);

            final String k = key, v = value;
            country_select_value.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        KatData.kataCountryBase.insert(k, v);
                        onFinishClick(k);
                    }
                    return true;
                }
            });

            country_select_key.setText(key);
            country_select_value.setText(value);
        }
    }

    public void onFinishClick(String key){
        kat_countrySelectionPopUpActivity.ChangeFinish(key);
    }

    @Override
    public void onBindViewHolder(@NonNull kat_CountrySelectionAdapter.ViewHolder holder, int position) {

        String key = list.get(position).getKey();
        String value = list.get(position).getValue();

        key = key.toUpperCase();
        value = value.toLowerCase();

        holder.bind(key, value);
    }


    public void search(String charText){
        charText = charText.toLowerCase(Locale.getDefault());

        list.clear();
        if(charText.length() == 0){
            list.addAll(rawList);
        }
        else{
            for(int i = 0; i < rawList.size(); i++){

                String rawKey = rawList.get(i).getKey().toLowerCase();
                String rawValue = rawList.get(i).getValue().toLowerCase();

                if(rawKey.contains(charText) || rawValue.contains(charText)){
                    list.add(rawList.get(i));
                }
            }
        }

        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
