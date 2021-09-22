package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;

import com.google.android.material.textfield.TextInputEditText;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.ranking.util.kat_CountrySelectionAdapter;
import com.keykat.keykat.brawlkat.util.KatData;

import java.util.ArrayList;
import java.util.Iterator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class kat_CountrySelectionPopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2020.11.20 둘이 무슨 차이?
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.country_select_popup);

        int width = (int) (KatData.SCREEN_WIDTH.intValue() * 0.9); //Display 사이즈의 70%
        int height = (int) (KatData.SCREEN_HEIGHT.intValue() * 0.7);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        ArrayList<Pair> data = MapToString();
        setView(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KatData.currentActivity = this;
    }

    public class Pair{
        String key, value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private ArrayList<Pair> MapToString(){

        ArrayList<Pair> resultData = new ArrayList<>();

        Iterator<String> keys = KatData.countryCodeMap.keySet().iterator();
        while(keys.hasNext()){

            String CountryCode = keys.next();
            String CountryName = KatData.countryCodeMap.get(CountryCode);

            CountryCode = CountryCode.toUpperCase();
            CountryName = CountryName.toLowerCase();

            Pair pair = new Pair();
            pair.setKey(CountryCode);
            pair.setValue(CountryName);

            resultData.add(pair);
        }

        return resultData;
    }

    private void setView(ArrayList<Pair> data){

        RecyclerView recyclerView = findViewById(R.id.country_select_popup_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        final kat_CountrySelectionAdapter countrySelectionAdapter
                = new kat_CountrySelectionAdapter(data, this);
        recyclerView.setAdapter(countrySelectionAdapter);

        TextInputEditText editText = findViewById(R.id.country_select_editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                countrySelectionAdapter.search(editable.toString());
            }
        });
    }

    public void ChangeFinish(String changedCountryCode){
        Intent intent = new Intent();
        intent.putExtra("changedCountryCode", changedCountryCode);
        setResult(1112, intent);
        finish();
    }
}
