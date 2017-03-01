package com.example.navigationapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.navigationapp.R;

public class traffic_activity extends AppCompatActivity {

    private LinearLayout i_layout1;
    private LinearLayout i_layout2;
    private Button i_analysis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_activity);
        i_layout1=(LinearLayout)findViewById(R.id.i_layout1);
        i_layout2=(LinearLayout)findViewById(R.id.i_layout2);
        i_analysis=(Button)findViewById(R.id.i_analysis);
        i_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i_layout1.setVisibility(View.GONE);
                i_layout2.setVisibility(View.VISIBLE);
            }
        });
    }
}
