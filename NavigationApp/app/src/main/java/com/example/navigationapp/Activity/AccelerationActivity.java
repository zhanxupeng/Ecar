package com.example.navigationapp.Activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationapp.R;
import com.example.navigationapp.model.Person;
import com.example.navigationapp.model.Timecar;
import com.example.navigationapp.util.ContentHandler;
import com.example.navigationapp.util.HttpCallbackListener;
import com.example.navigationapp.util.HttpUtil;

import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class AccelerationActivity extends AppCompatActivity {

    private LinearLayout f_layout_one;
    private LinearLayout f_layout_two;
    private LinearLayout f_layout_three;
    private Button f_start;
    private Button f_finish;
    private TextView f_rank;
    private TextView f_detail;
    private List<Timecar> times;
    private static final int SHOW_PESPONSE=1;
    private int result=0;
    private Handler handler=new Handler(){
      public void handleMessage(Message msg){
          switch (msg.what){
              case SHOW_PESPONSE:
                  String response=(String)msg.obj;
                  parseXMLWithSAX(response);
          }
      }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acceleration_activity);
        f_layout_one=(LinearLayout)findViewById(R.id.f_layout_one);
        f_layout_two=(LinearLayout)findViewById(R.id.f_layout_two);
        f_layout_three=(LinearLayout)findViewById(R.id.f_layout_three);
        f_start=(Button)findViewById(R.id.f_start);
        f_finish=(Button)findViewById(R.id.f_finish);
        f_rank=(TextView)findViewById(R.id.f_rank);
        f_detail=(TextView)findViewById(R.id.f_detail);
        f_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f_layout_one.setVisibility(View.GONE);
                f_layout_two.setVisibility(View.VISIBLE);
                f_layout_three.setVisibility(View.GONE);
                sendRequestWithHttpURLConnection(Person.car_id);
            }
        });
        f_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result==0){
                    Toast.makeText(AccelerationActivity.this,"解析未完成，请耐心等待",Toast.LENGTH_SHORT).show();
                }else {
                    f_layout_one.setVisibility(View.GONE);
                    f_layout_two.setVisibility(View.GONE);
                    f_layout_three.setVisibility(View.VISIBLE);
                    if (result == 1) {
                        f_rank.setText("安全");
                        f_rank.setTextColor(Color.rgb(0, 255, 0));
                        f_detail.setText("您的刹车很好，请放心驾驶。");
                    } else if (result == 2) {
                        f_rank.setText("良好");
                        f_rank.setTextColor(Color.rgb(0, 0, 255));
                        f_detail.setText("您的刹车状态还行，可以继续使用");
                    } else if (result == 3) {
                        f_rank.setText("中等");
                        f_rank.setTextColor(Color.rgb(255, 255, 0));
                        f_detail.setText("您的刹车状态欠佳，建议维修");
                    } else if (result == 4) {
                        f_rank.setText("危险");
                        f_rank.setTextColor(Color.rgb(255, 0, 0));
                        f_detail.setText("您的刹车严重失灵，请那没事维修");
                    }
                }
                }
        });
    }
    public int acceleration(List<Timecar> times, double old){
        double now=0;
        int result=0;
        for(int i=0;i<times.size()-1;i++){
            double mynow=(times.get(i).getSpeed()-times.get(i+1).getSpeed())/1.0;
            if(mynow>now){
                now=mynow;
            }
        }
        if(now>=old){
            result=1;//代表安全
        }else if(now>=old*0.9){
            result=2;//良好
        }else if(now>=old*0.8){
            result=3;//中等
        }else{
            result=4;//危险，建议维修
        }
        return result;
    }
    private void sendRequestWithHttpURLConnection(final int car_id){
        HttpUtil.sendHttpRequest("http://115.196.159.247:8080/CarSafe/TimecarServlet?car_id="+car_id, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message message=new Message();
                message.what=SHOW_PESPONSE;
                message.obj=response;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }
    private void parseXMLWithSAX(String xmlData){
        times=new ArrayList<Timecar>();
        try {
            SAXParserFactory factory=SAXParserFactory.newInstance();
            XMLReader xmlReader=factory.newSAXParser().getXMLReader();
            ContentHandler handler=new ContentHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
            times=handler.getTimes();
            Toast.makeText(AccelerationActivity.this,"一共有"+times.size()+"条数据！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result=acceleration(times,60);
    }
}
