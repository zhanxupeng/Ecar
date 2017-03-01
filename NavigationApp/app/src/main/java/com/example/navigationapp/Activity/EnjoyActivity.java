package com.example.navigationapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.navigationapp.R;
import com.example.navigationapp.model.StopStory;

public class EnjoyActivity extends AppCompatActivity implements View.OnClickListener{
    private Button l_news;
    private Button l_story;
    private Button l_music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enjoy_activity);
        l_news=(Button)findViewById(R.id.l_news);
        l_news.setOnClickListener(this);
        l_story=(Button)findViewById(R.id.l_story);
        l_story.setOnClickListener(this);
        l_music=(Button)findViewById(R.id.l_music);
        l_music.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.l_news:
                if(StopStory.flag!=1){
                    StopStory.flag=1;
                    l_news.setText("已开启");
                    l_story.setText("开启");
                    l_music.setText("开启");
                }else{
                    StopStory.flag=0;
                    l_news.setText("开启");
                }
                break;
            case R.id.l_story:
                if(StopStory.flag!=2){
                    StopStory.flag=2;
                    l_news.setText("开启");
                    l_story.setText("已开启");
                    l_music.setText("开启");
                }else{
                    StopStory.flag=0;
                    l_story.setText("开启");
                }
                break;
            case R.id.l_music:
                if(StopStory.flag!=3){
                    StopStory.flag=3;
                    l_news.setText("开启");
                    l_story.setText("开启");
                    l_music.setText("已开启");
                }else {
                    StopStory.flag=0;
                    l_music.setText("开启");
                }
                break;
            default:
                break;
        }
    }
}
