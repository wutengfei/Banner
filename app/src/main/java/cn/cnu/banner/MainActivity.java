package cn.cnu.banner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewpager;
    private Context context = MainActivity.this;
    private MyAdapter myAdapter;
    private RunnableTask runnableTask = new RunnableTask();
    // 准备要显示的图片资源
    private int[] imageIdArray = {R.drawable.a, R.drawable.b, R.drawable.c};
    // 准备title
    private String[] titleArray = {"巩俐不低俗，我就不能低俗",
            "扑树又回来啦！再唱经典老歌引万人大合唱",
            "揭秘北京电影如何升级"};
    // 轮播图显示的当前页
    private int currentPosition = Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % imageIdArray.length);
    int middle = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % imageIdArray.length);
    /**
     * 放置点的集合
     */
    private List<View> viewList = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 找到ViewPager控件
        viewpager = (ViewPager) findViewById(R.id.vp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ★图片滚动的入口,为什么写在onResume方法里呢？
        // ★因为当view失去焦点时，停止滚动，在重新获取焦点时要继续滚动起来
        // ★如果写在onCreate方法里，只是activity第一次创建时才会滚动，一但停止滚动，再次获取焦点也不会滚动了
        startRoll();
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                //★★★★★ 当轮播图显示当前页时，一定要把currentPosition设为arg0，
                //★★★★★ 否则，当你用手指滑动轮播图时，放手后，轮播图显示的下一张页面是并不是当前显示页面的下一页
                // ★★★★★而是，他自己根据3秒钟计算得到的下一页
                currentPosition = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 让轮播图viewpager滚动起来
     */
    public void startRoll() {
        // 滚动viewpager
        if (myAdapter == null) {
            // 1.第一次初始化适配器
            myAdapter = new MyAdapter();
            viewpager.setAdapter(myAdapter);
            viewpager.setCurrentItem(currentPosition);
        } else {// 8.第二次，只需要通知适配器数据发生了变化，要刷新Ui
            myAdapter.notifyDataSetChanged();
        }
        // 2.发送一个延时的消息，3秒后执行runnableTask类里run方法里的操作
        // ★（为什么执行的是runnableTask，而不是handleMessage呢？这里涉及到handler消息机制源码解析）
        handler.postDelayed(runnableTask, 3000);
    }

    class RunnableTask implements Runnable {
        @Override
        public void run() {
            // 3.变化轮播图当前要显示的页面位置，递增1，为了不使这个数字递增超过轮播图 图片的个数，取余数
            currentPosition = currentPosition + 1;
            // 4.发送消息给主线程的handler
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        // 5.接收并处理run方法发来的消息
        public void handleMessage(android.os.Message msg) {
            // 6.viewpager设置新的当前页
            viewpager.setCurrentItem(currentPosition);
            // 7.继续执行startRoll方法，成为一个循环
            startRoll();
        }
    };

    /**
     * ★当手指按住轮播图不动时，轮播图停止滚动；当点击轮播图时，跳转到相关界面
     */
    public void onTouchViewPager(View view, final int position) {
        // 给图片注册触摸事件监听器
        view.setOnTouchListener(new OnTouchListener() {

            private long downTime;
            private int downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下去时，记录按下的坐标和时间，用于判断是否是点击事件
                        handler.removeCallbacksAndMessages(null);// 手指按下时，取消所有事件，即轮播图不在滚动了
                        downX = (int) event.getX();
                        downTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:// 抬起手指时，判断落下抬起的时间差和坐标，符合以下条件为点击
                        // Toast.makeText(context, "手指抬起了", 0).show();
                        if (System.currentTimeMillis() - downTime < 500
                                && Math.abs(downX - event.getX()) < 30) {// ★考虑到手按下和抬起时的坐标不可能完全重合，这里给出30的坐标偏差

                         //每次启动程序时position都是1073741823,所以如下操作可以使其跳转到对应的界面
                            int number = position - 1073741822;
                            System.out.println("点击的是" + number);
                            if (number % titleArray.length == 1) {
                                startActivity(new Intent(MainActivity.this, Activity1.class));
                            } else if (number % titleArray.length == 2) {
                                startActivity(new Intent(MainActivity.this, Activity2.class));
                            }else if (number % titleArray.length == 0) {
                                startActivity(new Intent(MainActivity.this, Activity3.class));
                            }

                        }
                        startRoll();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // ★写这个的目的为了让用户在手指滑动完图片后，能够让轮播图继续自动滚动
                        startRoll();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 移除所有的任务，即：view失去焦点时，停止轮播图的滚动
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 处理小点点，使得小点点随着轮播图的位置而改变颜色
     */
    private void initDot(LinearLayout dots_ll, int position) {
        // 必须每次进来清除线性布局里的所有小点点，不然，每次切换回页面，都运行initDot方法，会一直累加小点点，每次增加8个点
        dots_ll.removeAllViews();
        viewList.clear();
        position = position % imageIdArray.length;
        // 遍历轮播图片的集合，每遍历一个，new一个view，给这个view设置背景图片，
        // 给包含小点点的父亲现形布局设置参数，设置间距，线性布局添加这些点，viewList也添加小点点
        for (int i = 0; i < imageIdArray.length; i++) {
            View view = new View(context);
            if (i == position) {
                view.setBackgroundResource(R.drawable.dot_focused);
            } else {
                view.setBackgroundResource(R.drawable.dot_normal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    10,//点点的宽度
                    10);
            view.setLayoutParams(layoutParams);
            layoutParams.setMargins(5, 0, 5, 0);
            dots_ll.addView(view);
            viewList.add(view);
        }
    }

    /**
     * 适配器，要重写下面四个方法
     */
    class MyAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(context, R.layout.item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageResource(imageIdArray[position % titleArray.length]);
            TextView title = (TextView) view.findViewById(R.id.top_news_title);
            title.setText(titleArray[position % titleArray.length]);
            LinearLayout dots_ll = (LinearLayout) view.findViewById(R.id.dots_ll);
            // 处理小点点的操作
            initDot(dots_ll, position);
            // onTouchViewPager方法一定要写在instantiateItem内部，表示触摸的是当前位置的页面
            onTouchViewPager(view, position);
            // ★★★这句话很重要！！！别忘了写！！！
            ((ViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // ★★★这句话很重要！！！别忘了写！！！
            ((ViewPager) container).removeView((View) object);
        }
    }
}