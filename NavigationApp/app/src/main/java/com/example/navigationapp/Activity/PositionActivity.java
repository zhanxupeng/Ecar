package com.example.navigationapp.Activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.navigationapp.R;
import com.example.navigationapp.model.Nowcar;
import com.example.navigationapp.model.Time;
import com.example.navigationapp.util.HttpCallbackListener;
import com.example.navigationapp.util.HttpUtil;
import com.example.navigationapp.util.NowcarHandler;

import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class PositionActivity extends AppCompatActivity {

    private static final int SHOW_RESPONSE=2;
    private LinearLayout h_layout1;
    private LinearLayout h_layout2;
    private EditText h_nowcar;
    private Button h_select;
    private TextView h_result;
    private Time me=new Time(3,980,1000,1);//得到我当前的位置
    private Time now;
    List<Nowcar> cars=null;
    private Handler handler=new Handler(){
       @Override
        public void handleMessage(Message msg){
           switch (msg.what){
               case SHOW_RESPONSE:
                   String response=(String)msg.obj;
                   parseXMLNowCar(response);
           }
       }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_activity);
        h_layout1=(LinearLayout)findViewById(R.id.h_layout1);
        h_layout2=(LinearLayout)findViewById(R.id.h_layout2);
        h_nowcar=(EditText)findViewById(R.id.h_nowcar);
        h_select=(Button)findViewById(R.id.h_select);
        h_result=(TextView)findViewById(R.id.h_result);
        requestXml();
        h_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int car_id=Integer.parseInt(h_nowcar.getText().toString());
                for(Nowcar car:cars){
                    if(car.getCar_id()==car_id){
                        now=sureTime(car, me);
                        String result="该车在"+now.getFlag()+"号线路的("+now.getXx()+","+now.getYy()+")"+"位置上，"+"距离你大约还有"+
                                now.getTime()+"分钟";
                        h_result.setText(result);
                        h_layout1.setVisibility(View.GONE);
                        h_layout2.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }
    private void requestXml(){
        HttpUtil.sendHttpRequest("http://192.168.1.106:8080/CarSafe/PositionServlet", new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message message=new Message();
                message.what=SHOW_RESPONSE;
                message.obj=response;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    private void parseXMLNowCar(String xmlData){
        cars=new ArrayList<Nowcar>();
        try{
            SAXParserFactory factory1=SAXParserFactory.newInstance();
            XMLReader xmlReader1=factory1.newSAXParser().getXMLReader();
            NowcarHandler handler1=new NowcarHandler();
            xmlReader1.setContentHandler(handler1);
            xmlReader1.parse(new InputSource(new StringReader(xmlData)));
            cars=handler1.getTimes();
            //Toast.makeText(MainActivity.this, "一共有"+cars.size()+"条数据！", Toast.LENGTH_SHORT).show();
            //StringBuilder str=new StringBuilder();
           // for(Nowcar car:cars){
              //  str.append(car.getCar_id()+","+car.getXx()+","+car.getYy()+"\n");
           // }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Time sureTime(Nowcar car, Time me){
        Time time=new Time();
        time.setFlag(car.getFlag());
        time.setXx(car.getXx());
        time.setYy(car.getYy());
        double distance=0;
        if((car.getFlag()==me.getFlag())||(car.getFlag()==1&&me.getFlag()==2)
                ||(car.getFlag()==2&&me.getFlag()==1)||(car.getFlag()==1&&me.getFlag()==3)
                ||(car.getFlag()==3&&me.getFlag()==1)){
            distance=Math.abs(car.getXx()-me.getXx())+Math.abs(car.getYy()-me.getYy());
        }else if((car.getFlag()==2&&me.getFlag()==3)||(car.getFlag()==3&&me.getFlag()==2)){
            distance=530+car.getYy()-50+me.getYy()-50;
        }
        time.setTime((distance/car.getSpeed()));
        return time;
    }
}
