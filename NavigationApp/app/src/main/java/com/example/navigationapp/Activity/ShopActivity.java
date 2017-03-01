package com.example.navigationapp.Activity;

import java.util.ArrayList;
import java.util.List;


import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.navigationapp.R;

public class ShopActivity extends AppCompatActivity {
    private ViewPager mViewPager;

    // 图片都存放在这里
    private List<ImageView> imageViewlist;

    private ImageView b_che;
    private ImageView b_shang;
    private ImageView b_wo;


    private ImageView iv;
    private TextView imgDes;

    // 线程开关，当activity销毁后，线程也应该停止运行
    private boolean isStop = false;
    private int previousPoint = 0;

    // 存放小点的布局文件
    private LinearLayout layoutPGroup;

    private String[] imageDescription = { "2017新年快乐，鸡年大吉", "奔驰全系祝您新春快乐",
            "长安汽车新春特价猛烈来袭", "新一代梅赛德斯奔驰GLE，尽情驰骋", "宝马X5与您恭贺新春" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shop_activity);


        b_che=(ImageView)findViewById(R.id.a_car);
        b_che.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShopActivity.this,FirstPage.class);
                startActivity(intent);
                finish();
            }
        });
        b_shang=(ImageView)findViewById(R.id.a_shop);
        b_shang.setImageResource(R.mipmap.shang_two);
        b_wo=(ImageView)findViewById(R.id.a_mymessage);
        b_wo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShopActivity.this,MymessageActivity.class);
                startActivity(intent);
                finish();
            }
        });




        ImageView taobao = (ImageView)findViewById(R.id.tu1);		//获得图片控件
        taobao.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){							//淘宝图标响应
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.taobao.com/"));
                startActivity(intent);
            }
        });

        // 初始化
        init();

        //设置图片自动滚动
        new Thread(new Runnable() {

            @Override
            public void run() {
                //如果activity未销毁则一直执行
                while (!isStop) {
                    //先休息4秒钟
                    SystemClock.sleep(4000);

                    //以下代码发送到主线程中执行
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager
                                    .getCurrentItem() + 1);
                        }
                    });
                }
            }
        }).start();
    }
    private void init() {
        mViewPager = (ViewPager) this.findViewById(R.id.aviewpager);
        layoutPGroup = (LinearLayout) this.findViewById(R.id.ashow_pointer);
        imgDes = (TextView) this.findViewById(R.id.image_description);
        imageViewlist = new ArrayList<ImageView>();

        // 先拿到图片id
        int[] ivIDs = new int[] { R.mipmap.aaa, R.mipmap.bbb, R.mipmap.ccc,
                R.mipmap.ddd, R.mipmap.eee };

        // 遍历图片id
        for (int id : ivIDs) {
            // 构造新的图片对象，并根据id给图片设置背景
            iv = new ImageView(this);
            iv.setBackgroundResource(id);
            // 所有的图片存放在imageViewlist中
            imageViewlist.add(iv);

            // 构造小点
            View v = new View(this);
            // 设置小点的宽和高
            LayoutParams params = new LayoutParams(8, 8);
            // 设置小点的左边距
            params.leftMargin = 12;
            v.setLayoutParams(params);
            // 设置小点是否可用，默认都不可用，当不可用时，小点是透明的，否则是白色的
            v.setEnabled(false);
            // 设置小点的背景，这个背景是使用xml文件画的一个小圆点
            v.setBackgroundResource(R.drawable.pointer_selector);
            // 把小点添加到它的布局文件中
            layoutPGroup.addView(v);
        }
        // 计算应用打开时显示的第一项 Integer.MAX_VALUE /2 - 3=0
        int index = Integer.MAX_VALUE / 2 - 3;
        // 给mViewPager设置数据
        mViewPager.setAdapter(new MyPagerAdapter());
        // 给mViewPager设置页面滑动事件
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        // 设置应用打开时显示的第一项，index的值为0
        // 使用这种方式得到的0，和直接写0有什么区别呢？
        // 直接写0，应用打开后不能直接向右滑动，因为viewpager中存image位置不能为负值，只能先向左滑动
        // 这种方式得到的0，可以实现应用一打开，就可以向右滑动
        mViewPager.setCurrentItem(index);
    }

    private class MyOnPageChangeListener implements OnPageChangeListener {

        // 开始
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        // 正在进行时
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        // 结束
        @Override
        public void onPageSelected(int position) {
            // 当页面滑动结束时，先对页面位置取模
            position = position % imageViewlist.size();
            // 设置textview的文本内容
            imgDes.setText(imageDescription[position]);
            // 将上一个点的可用性设置为false
            layoutPGroup.getChildAt(previousPoint).setEnabled(false);
            // 把当前点的可用性设置为true
            layoutPGroup.getChildAt(position).setEnabled(true);
            // 把当前位置值赋值给previousPoint
            previousPoint = position;
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        /**
         * 返回图片总数，Integer.MAX_VALUE的值为 2147483647这个数有21亿，也就是说我们的viewpager
         * 理论上在每次使用应用的时候可以滑动21亿次,当然，实际上是没人要去这么做的，我们这样做是为了实现实现viewpager循环滑动的效果
         * 即当滑动到viewpager的最后一页时，继续滑动就可以回到第一页
         *
         */
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // 当某一页滑出去的时候，将其销毁
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewlist.get(position
                    % imageViewlist.size()));
        }

        // 向容器中添加图片，由于我们要实现循环滑动的效果，所以要对position取模
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container
                    .addView(imageViewlist.get(position % imageViewlist.size()));
            return imageViewlist.get(position % imageViewlist.size());
        }
    }

    // 当activity销毁时，让线程停止
    @Override
    protected void onDestroy() {
        isStop = true;
        super.onDestroy();
    }
}
