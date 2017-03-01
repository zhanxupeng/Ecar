package com.example.navigationapp.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.navigationapp.R;
import com.example.navigationapp.model.Nowcar;
import com.example.navigationapp.model.Person;
import com.example.navigationapp.model.Pileup;
import com.example.navigationapp.model.StopStory;
import com.example.navigationapp.model.Timecar;
import com.example.navigationapp.util.ContentHandler;
import com.example.navigationapp.util.HttpCallbackListener;
import com.example.navigationapp.util.HttpUtil;
import com.example.navigationapp.util.NowcarHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private MyReceiver myReceiver;
    private String music="music.mp3";//定义一个变量名，用来存储音频名字
    private int tableWidth;
    private int tableHeight;
    private final int BALL_SIZE=15;
    private float ballX[]=new float[20];
    private float ballY[]=new float[20];

    boolean stopThread=false;

    private double beforeX;
    private double beforeY;
    private static final int SHOW_PESPONSE=0;
    private static final int SHOW_NOWCAR=1;

    private static final int SHOW_RESPONSE=2;
    List<Nowcar> cars=null;
    List<Timecar> times=null;

    private boolean first=true;//设置一个变量用来检测当前是否是车子第一次动；
    private Nowcar myold,mynew;

    private MediaPlayer mediaPlayer=new MediaPlayer();

    private static GameView gameView;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case SHOW_RESPONSE:
                    if(cars!=null) {
                        int i=0;
                        for (Nowcar car : cars) {
                            ballX[i] = (float) car.getXx();
                            ballY[i] = (float) car.getYy();
                            i++;
                        }

                        //看看速度是否为零，要不要进行播放音乐新闻等
                        for(Nowcar car:cars){
                            if(car.getCar_id()==Person.car_id){
                                if(StopStory.flag!=0){
                                if(car.getSpeed()<=1) {
                                    Toast.makeText(MainActivity.this, "当前正在播放休闲", Toast.LENGTH_LONG).show();
                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.start();
                                    }
                                }
                                }
                            }
                        }
                        //看看是否有追尾危险~
                        Pileup check=new Pileup();
                        boolean result=check.checkSafe(Person.car_id,cars);
                        if(result){
                            Toast.makeText(MainActivity.this,"危险，请紧急刹车！",Toast.LENGTH_SHORT).show();
                           if(!mediaPlayer.isPlaying()){
                                mediaPlayer.start();
                           }
                        }
                        //如果是雾霾天气，开启提醒
                        if(StopStory.flag>5){
                            double lengths=30;
                            int sresult=check.Danger(cars,Person.car_id,lengths);
                            if(sresult == 1){
                                //System.out.println("您前方30米有车辆在行驶，请谨慎驾驶！");
                                Toast.makeText(MainActivity.this,"您前方30米有车辆在行驶，请谨慎驾驶！",Toast.LENGTH_SHORT).show();
                            }
                            if(sresult == 11){
                                //System.out.println("您前方30米有车辆在行驶且车速较快，请谨慎驾驶！");
                                Toast.makeText(MainActivity.this,"您前方30米有车辆在行驶且车速较快，请谨慎驾驶！",Toast.LENGTH_SHORT).show();
                            }
                            if(sresult == 2){
                                //System.out.println("您前方30米有逆向来车，请谨慎驾驶！");
                                Toast.makeText(MainActivity.this,"您前方30米有逆向来车，请谨慎驾驶！",Toast.LENGTH_SHORT).show();
                            }
                            if(sresult == 22){
                                //System.out.println("您前方30米有逆向来车且车速较快，请谨慎驾驶！");
                                Toast.makeText(MainActivity.this,"您前方30米有逆向来车且车速较快，请谨慎驾驶！",Toast.LENGTH_SHORT).show();
                            }
                            if(sresult == 3){
                                //System.out.println("前方交叉路口有来车，请谨慎驾驶！");
                                Toast.makeText(MainActivity.this,"前方交叉路口有来车，请谨慎驾驶！",Toast.LENGTH_SHORT).show();
                            }
                            if(sresult == 33){
                                //System.out.println("前方交叉路口有来车且车速较快，请谨慎驾驶！");
                                Toast.makeText(MainActivity.this,"前方交叉路口有来车且车速较快，请谨慎驾驶！",Toast.LENGTH_SHORT).show();
                            }
                        }
                        //检测车辆是否偏移，并且速度大于10
                        if(first){
                            myold=check.selectcar(cars,Person.car_id);
                            mynew=check.selectcar(cars,Person.car_id);
                            first=false;
                        }else{
                            mynew=check.selectcar(cars,Person.car_id);
                        }
                        int result2=check.PianYi(myold,mynew);
                        if(result2==-1){
                            Toast.makeText(MainActivity.this,"当前已经偏离方向，请注意行驶安全！",Toast.LENGTH_SHORT).show();
                            if(!mediaPlayer.isPlaying()){
                                mediaPlayer.start();
                            }
                        }
                        myold=check.selectcar(cars,Person.car_id);
                        //看看是否有岔路口相撞风险
                        //boolean crossroads;
                        int crossroads=0;
                        crossroads=check.crosssafe(Person.car_id,cars);
                        if(crossroads==1){
                            Toast.makeText(MainActivity.this,"前方左边有车辆高速驶过，注意安全！",Toast.LENGTH_SHORT).show();
                           // if(!mediaPlayer.isPlaying()){
                               // mediaPlayer.start();
                            //}
                        }else if(crossroads==2){
                            Toast.makeText(MainActivity.this,"前方右边有车辆高速驶过，注意安全！",Toast.LENGTH_SHORT).show();
                           // if(!mediaPlayer.isPlaying()){
                               // mediaPlayer.start();
                           // }
                        }else if(crossroads==3){
                            Toast.makeText(MainActivity.this,"前方左右都有车辆高速驶过，注意安全！",Toast.LENGTH_SHORT).show();
                            //if(!mediaPlayer.isPlaying()){
                               // mediaPlayer.start();
                           // }
                        }
                        //岔路口相撞部分未编译
                        //看看弯倒口是否有车迎面驶来
                        boolean corner=false;
                        corner=check.corner(Person.car_id,cars);
                        if(corner){
                            Toast.makeText(MainActivity.this,"前方弯路口有车迎面驶来，请小心驾驶！",Toast.LENGTH_SHORT).show();

                            //换歌
                            if(!"caijie.mp3".equals(music)) {
                                mediaPlayer.reset();
                                music="caijie.mp3";
                                checkmusic();
                            }
                            if(!mediaPlayer.isPlaying()){
                                mediaPlayer.start();
                            }
                        }
                    }
                    gameView.invalidate();
                    break;
                case SHOW_PESPONSE:
                    String response=(String) msg.obj;
                    parseXMLWithSAX(response);
                    break;
                case SHOW_NOWCAR:
                    String response1=(String)msg.obj;
                    parseXMLNowCar(response1);
                    break;
            }
        }
    };
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
            StringBuilder str=new StringBuilder();
            for(Nowcar car:cars){
                str.append(car.getCar_id()+","+car.getXx()+","+car.getYy()+"\n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
            Toast.makeText(MainActivity.this,"一共有"+times.size()+"条数据！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        beforeX=times.get(0).getXx();
        beforeY=times.get(0).getYy();
            threadtest(times);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //用来提示当前开车状态不能玩手机
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        myReceiver=new MyReceiver();
        registerReceiver(myReceiver,intentFilter);
        //设置语音提醒功能

        checkmusic();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
       gameView=new GameView(this);
        setContentView(gameView);
        WindowManager windowManager=getWindowManager();
        Display display=windowManager.getDefaultDisplay();
        DisplayMetrics metrics=new DisplayMetrics();
        display.getMetrics(metrics);
        tableWidth=metrics.widthPixels;
        tableHeight=metrics.heightPixels;
        sendRequestWithHttpURLConnection(Person.car_id);
    }
    private void checkmusic(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }else{
            //初始化MediaPlayer
            initMediaPlayer(music);
        }
    }
    class GameView extends View{
        Paint paint=new Paint();
        public GameView(Context context){
            super(context);
            setFocusable(true);
        }
        public void onDraw(Canvas canvas){
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.map_page),0,0,paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            paint.setColor(Color.rgb(255,255,255));
            if(cars!=null) {
                int i = 0;
                //canvas.drawRect(0,0,tableWidth,tableHeight-200,paint);
                canvas.drawRect(0,0,1080,100,paint);
                canvas.drawRect(400,0,500,1800,paint);
                canvas.drawRect(980,0,1080,1800,paint);
                paint.setColor(Color.rgb(255,0,0));
                Log.d("database","tableWidth="+tableWidth+"tableHeight="+tableHeight);
                int ii=0;//用来标记自己的球
                for (Nowcar car : cars) {
                    if(car.getCar_id()==Person.car_id){
                        ii=i;
                    }else {
                        canvas.drawCircle(ballX[i], ballY[i], BALL_SIZE, paint);
                    }
                    i++;
                }
                paint.setColor(Color.rgb(0,0,0));
                canvas.drawCircle(ballX[ii],ballY[ii],BALL_SIZE,paint);
            }
        }
    }
    private void threadtest(final List< Timecar> times){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!stopThread) {
                    for(Timecar time:times) {
                        try {
                            int flag=0;
                            double direction=0;
                            //确定flag和direction
                            if(time.getXx()>=0&&time.getXx()<=1080&&time.getYy()>=0&&time.getYy()<=100){
                                flag=1;
                            }
                            else if(time.getYy()>100&&time.getYy()<=1800&&time.getXx()>=400&&time.getXx()<=500){
                                flag=2;
                            }
                            else if(time.getYy()>100&&time.getYy()<=1800&&time.getXx()>=980&&time.getXx()<=1080){
                                flag=3;
                            }else{
                                flag=-1;
                            }

                            if(flag==1&&(beforeX<time.getXx())){
                                direction=1;
                            }
                            else if(flag==1&&(beforeX>=time.getXx())){
                                direction=2;
                            }else if((flag==2||flag==3)&&(beforeY<time.getYy())){
                                direction=1;
                            }else if((flag==2||flag==3)&&(beforeY>=time.getYy())){
                                direction=2;
                            }else{
                                direction=0;
                            }
                            Thread.sleep(2000);
                            Log.d("mainactivity", "2000");
                            Message message = new Message();
                            message.what = SHOW_RESPONSE;
                            sendRequestNowcar(time.getCar_id(),flag,time.getXx(),time.getYy(),time.getSpeed(),direction);
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();
    }
    @Override
    public void onBackPressed(){
        finish();
        stopThread=true;
    }

    private void sendRequestNowcar(final int car_id,final int flag,final double xx,final double yy,final double speed,final  double direction){
        String src="http://115.196.159.159:8080/CarSafe/NowcarServlet?car_id="+car_id+"&&flag="+flag+
                "&&xx="+xx+"&&yy="+yy+"&&speed="+speed+"&&direction="+direction;
        HttpUtil.sendHttpRequest(src, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Message message=new Message();
                message.what=SHOW_NOWCAR;
                message.obj=response;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    private void sendRequestWithHttpURLConnection(final int car_id){
        HttpUtil.sendHttpRequest("http://115.196.159.159:8080/CarSafe/TimecarServlet?car_id="+car_id, new HttpCallbackListener() {
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
    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Toast.makeText(context, "正在开车，请勿使用手机！", Toast.LENGTH_LONG).show();
        }
    }
    private void initMediaPlayer(String musics){
        try{
            File file=new File(Environment.getExternalStorageDirectory(),musics);
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer(music);
                }else {
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_LONG).show();
                    finish();
                }break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
