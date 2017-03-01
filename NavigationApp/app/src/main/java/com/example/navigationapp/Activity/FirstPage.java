package com.example.navigationapp.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;



import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationapp.R;
import com.example.navigationapp.model.Person;

import java.util.ArrayList;
import java.util.List;

public class FirstPage extends AppCompatActivity {

    private ViewPager mViewPager;
    //图片都存放在这里
    private List<ImageView> imageViewList;
    private ImageView iv;
    private TextView imgDes;
    //线程开关，当activity 销毁后，线程也应该停止运行
    private boolean isStop=false;
    private int previousPoint=0;
    //存放小点的布局文件
    private LinearLayout layoutPGroup;
    private String[] imageDescription = { "淮左名都，竹西佳处，解鞍少驻初程。", "过春风十里。尽荠麦青青。",
            "自胡马窥江去后，废池乔木，犹厌言兵。", "渐黄昏，清角吹寒。都在空城。", "杜郎俊赏，算而今、重到须惊。" };


    private ImageView a_start;
    private ImageView icons_one;
    private ImageView icons_two;
    private ImageView icons_three;
    private ImageView icons_four;
    private ImageView icons_five;
    private ImageView icons_six;
    private ImageView a_car;
    private ImageView a_shop;
    private ImageView a_mymessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.first_page);
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isStop){
                    SystemClock.sleep(5000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                        }
                    });
                }
            }
        }).start();


        //获取id
        icons_one=(ImageView)findViewById(R.id.icons_one);
        //勿扰模式
        icons_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstPage.this,PositionActivity.class);
                startActivity(intent);
            }
        });
        icons_two=(ImageView)findViewById(R.id.icons_two);
        icons_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Person.flag) {
                    Intent intent = new Intent(FirstPage.this, AccelerationActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(FirstPage.this,"该功能仅对登录用户开放，请先登录！",Toast.LENGTH_LONG).show();
                }
            }
        });
        icons_three=(ImageView) findViewById(R.id.icons_three);
        icons_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FirstPage.this,"改功能暂未开放，请谅解！",Toast.LENGTH_SHORT).show();
            }
        });
        icons_four=(ImageView) findViewById(R.id.icons_four);
        icons_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstPage.this,KipActivity.class);
                startActivity(intent);
            }
        });
        icons_five=(ImageView) findViewById(R.id.icons_five);
        icons_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstPage.this,traffic_activity.class);
                startActivity(intent);
            }
        });
        icons_six=(ImageView) findViewById(R.id.icons_six);
        icons_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Person.flag) {
                    Intent intent = new Intent(FirstPage.this, EnjoyActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(FirstPage.this,"该功能仅对登录用户开放，请先登录！",Toast.LENGTH_LONG).show();
                }
            }
        });
        //行车
        a_car=(ImageView)findViewById(R.id.a_car);
        //商店
        a_shop=(ImageView)findViewById(R.id.a_shop);
        a_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(FirstPage.this,ShopActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //我的信息页面
        a_mymessage=(ImageView)findViewById(R.id.a_mymessage);
        a_mymessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstPage.this,MymessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
            a_car.setImageResource(R.mipmap.che_two);
        //运动按钮
        a_start=(ImageView) findViewById(R.id.a_start);
        a_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Person.flag) {
                    Intent intent = new Intent(FirstPage.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(FirstPage.this,"该功能仅对登录用户开放，请先登录！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void init(){
        mViewPager=(ViewPager)this.findViewById(R.id.viewpager);
        layoutPGroup=(LinearLayout)this.findViewById(R.id.show_pointer);
        imgDes=(TextView)this.findViewById(R.id.image_description);
        imageViewList=new ArrayList<ImageView>();
        int[] ivIDs=new int[]{
                R.mipmap.a,R.mipmap.b,R.mipmap.c,R.mipmap.d,R.mipmap.e
        };
        for(int id:ivIDs){
            iv=new ImageView(this);
            iv.setBackgroundResource(id);
            imageViewList.add(iv);
            View v=new View(this);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(8,8);
            params.leftMargin=12;
            v.setLayoutParams(params);
            v.setEnabled(false);
            v.setBackgroundResource(R.drawable.pointer_selector);
            layoutPGroup.addView(v);
        }
        int index=Integer.MAX_VALUE/2-3;
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mViewPager.setCurrentItem(index);

    }
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //开始
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //正在进行时
        }

        @Override
        public void onPageSelected(int position) {
            //结束
            position=position%imageViewList.size();
            imgDes.setText(imageDescription[position]);
            layoutPGroup.getChildAt(previousPoint).setEnabled(false);
            layoutPGroup.getChildAt(position).setEnabled(true);
            previousPoint=position;
        }
    }
    private class MyPagerAdapter extends PagerAdapter{
        @Override
        public int getCount(){
            return Integer.MAX_VALUE;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewList.get(position%imageViewList.size()));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViewList.get(position%imageViewList.size()));
            return imageViewList.get(position%imageViewList.size());
        }
    }
    @Override
    protected void onDestroy(){
        isStop=true;
        super.onDestroy();
    }
}
