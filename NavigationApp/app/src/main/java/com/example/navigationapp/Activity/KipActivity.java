package com.example.navigationapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.navigationapp.R;

public class KipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kip_activity);
        ImageView k1 = (ImageView)findViewById(R.id.k1);			//获得图片控件
        k1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){							//置顶图片点击、跳转
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.autohome.com.cn/dealer/201512/47308605.html"));
                startActivity(intent);
            }
        });

        //返回首页按钮
        Button btn_1 = (Button)findViewById(R.id.btn_1);
        btn_1.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

    }
}
