package com.example.navigationapp.Activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationapp.R;
import com.example.navigationapp.model.MyIp;
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


    private List<Timecar> times;//解析出来的数据
    private int myacceleration=0;//用于求出当前最大加速度
    private static final int SHOW_PESPONSE=1;
    private static final int SHOW_CHANGE=2;
    private int result=0;
    private TextView acceleration_date;
    private TextView time_date;
    private TextView speed_date;
    private ImageButton acceleration_button;

    private TextView security_name;
    private TextView security_detail;
    private ImageView security_image;
    private Handler handler=new Handler(){
      public void handleMessage(Message msg){
          switch (msg.what){
              case SHOW_PESPONSE:
                  String response=(String)msg.obj;
                  parseXMLWithSAX(response);//已经解析完成，数据存在times里面,开一个线程，每秒钟修改一下数据
                  createMyThread(times.size());//开始开线程执行数据
                  break;
              case SHOW_CHANGE:
                  String change=(String)msg.obj;
                  Log.d("setTheChange",change);
                  int num=Integer.parseInt(change);
                  //通过当前的num，来计算当前的速度，加速度，和时间
                  setAllText(num);
                  break;
          }
      }
    };
    private void setAllText(int index){
        int mytime=times.get(index).getTime();
        int hour=mytime/3600;
        int minute=(mytime%3600)/60;
        int second=mytime%60;
        int speed=(int)times.get(index).getSpeed();
        String strspeed=String.format("%d",speed);
        //如果是第一个的话，就不设置加速度
        if(index==0){
            time_date.setText("00:00:01");
            speed_date.setText(strspeed);
        }
        else {
            //先求出加速度
            Log.d("xxxxxxxxxxxxxxx","into the >1");
            int a=((int)times.get(index-1).getSpeed())-((int)times.get(index).getSpeed());
            time_date.setText(hour+":"+minute+":"+second);
            speed_date.setText(strspeed);
            acceleration_date.setText(String.format("%d",a));
            if(a>myacceleration){
                myacceleration=a;
            }
            Log.d("xxxxxxxxxxxxxxx","index:"+index+",");
            Log.d("xxxxxxxxxxxxxxxxxxxx",a+","+times.get(index).getSpeed());
        }
        //如果当前数据已经读完了，那么自动停止
        if(index==(times.size()-1)){
            acceleration_button.setImageResource(R.mipmap.stop_butto);
            //这里需要加测试的结果
            acceleration(myacceleration,50);//把当前最大的加速度和原来的加速度比较
            showAddDialog();
        }
    }
    private void createMyThread(final int tnum){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int mynumber=0;
                while (mynumber<tnum){
                    //先sleep一秒，然后再发送数据
                    try {
                        Log.d("Thread","once");
                        Thread.sleep(1000);
                        Message message=new Message();
                        message.what=SHOW_CHANGE;
                        message.obj=String.format("%d",mynumber);
                        handler.sendMessage(message);
                        mynumber++;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.acceleration_activity);
        acceleration_date=(TextView)findViewById(R.id.acceleration_date);
        time_date=(TextView)findViewById(R.id.time_date);
        speed_date=(TextView)findViewById(R.id.speed_date);
        acceleration_button=(ImageButton)findViewById(R.id.acceleration_button);
        acceleration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myacceleration==0) {
                    acceleration_button.setImageResource(R.mipmap.pause_button);
                    sendRequestWithHttpURLConnection(Person.car_id);//发送请求，得到数据
                }else{
                    acceleration(myacceleration,50);//把当前最大的加速度和原来的加速度比较
                    Log.d("xxxzxxxxxxxxxxxxxxxxxx",result+"ss");
                    showAddDialog();
                }
                 }
        });
        //这里也可以手动加测试的结果
    }
    public void showAddDialog(){
        LayoutInflater factory=LayoutInflater.from(AccelerationActivity.this);
        final View view=factory.inflate(R.layout.acceleration_dialog,null);
        security_name=(TextView)view.findViewById(R.id.security_name);
        security_detail=(TextView)view.findViewById(R.id.security_detail);
        security_image=(ImageView)view.findViewById(R.id.security_image);
        //调用方法，修改参数
        changeRank(result);
        AlertDialog.Builder ad1=new AlertDialog.Builder(AccelerationActivity.this);
        ad1.setTitle("提示：");
        ad1.setView(view);
        ad1.setPositiveButton("重测", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                myacceleration=0;
                time_date.setText("00:00:00");
                acceleration_date.setText("00");
                speed_date.setText("00");
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        });
        ad1.show();
    }
    private void changeRank(int num){
        if(num==1){
            //安全
            security_name.setText("安全");
            security_detail.setText("你的刹车性能是最佳性能时的98%，保养非常好，请继续努力");
            security_name.setTextColor(Color.rgb(0,255,0));
            security_image.setImageResource(R.mipmap.happy);
        }else if(num==2){
            //良好
            security_name.setText("良好");
            security_detail.setText("您的刹车性能是最佳性能的80%，继续保持");
            security_name.setTextColor(Color.rgb(0,162,232));
            security_image.setImageResource(R.mipmap.speechless);
        }else if(num==3){
            //中等
            security_name.setText("中等");
            security_detail.setText("您的刹车性能是最佳性能的60%，可以考虑维修");
            security_name.setTextColor(Color.rgb(0,0,0));
            security_image.setImageResource(R.mipmap.distress);
        }else if(num==4){
            //危险
            security_name.setText("危险");
            security_detail.setText("您的刹车已经严重失灵了，请务必马上维修");
            security_name.setTextColor(Color.rgb(255,0,0));
            security_image.setImageResource(R.mipmap.angry);
        }
    }
    public int acceleration(int now, int old){

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
    //这个是用来得到车辆的运动状况的
    private void sendRequestWithHttpURLConnection(final int car_id){
        HttpUtil.sendHttpRequest("http://"+ MyIp.ip+":8080/CarSafe/TimecarServlet?car_id="+car_id, new HttpCallbackListener() {
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
    //解析数据
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
        for(Timecar time:times){
            Log.d("wannima",time.getSpeed()+","+time.getTime());
        }
        //result=acceleration(times,60);
    }
}
