package com.android.sichard.lockscreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sichard.lockscreen.constants.Const;
import com.android.sichard.lockscreen.framework.SingleInstanceBase;
import com.android.sichard.lockscreen.handle.ExBatteryStateHandler;
import com.android.sichard.lockscreen.utils.BatteryInfo;
import com.android.sichard.lockscreen.view.BubbleAnimationView;
import com.android.sichard.lockscreen.view.ChargeProgressClipImageView;
import com.android.sichard.lockscreen.view.ChargeProgressIndicateView;
import com.android.sichard.lockscreen.view.ChargeWaveView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 *<br>类描述：锁屏界面
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2017/3/6</b>
 */
public class LockScreenActivity extends Activity implements GestureDetector.OnGestureListener{
    /** 充电完成标志位 */
    private static final String BATTERY_COMPLETE = "battery_complete";
    /** 剩余涓流充电时间	 */
    private static final String BATTERY_TRICKLE_TIME = "batter_trickle_time";
    /** 充电开始时的电量 */
    public static final String START_LEVEL = "start_level";
    private static final int LIGHT_COLOR = Color.parseColor("#FFFFFF");
    private static final int DARK_COLOR = Color.parseColor("#43FFFFFF");
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    /** 涓流充电时间10*60s	 */
    private static final int TRICKLE_TIME = 10 * 60;

    private ExBatteryStateHandler mBatteryStateHandler;

    /** 电池电量 */
    private int mBatteryLevel = 0;

    private TextView mTextBatteryIndicator;
    private TextView mTipText;
    private ChargeWaveView mChargeWaveView = null;
    private BubbleAnimationView mBubbleAnimationView;
    private BatteryStateChangedReceiver mBatteryStateChangedReceiver;
    /**
     * 充电指示TextView
     */
    private TextView mChargeSpeed, mChargeContinuous, mChargeTrickle;
    /** 显示时间的view */
    private TextView mTextTimeHour, mTextTimeMinute;

    private ChargeProgressIndicateView mChargeIndicateView;
    private ChargeProgressClipImageView mChargeClipView;
    /**
     * 涓流充电标志位
     */
    private boolean mIsTrickleCharging = false;
    /**
     * 充电完成标志位
     */
    private boolean mIsChargeComplete = false;

    /**	涓流充电时间 */
    private int mTrickleTime = TRICKLE_TIME;
    /**	涓流充电时间更新的线程 */
    private TrickleRunnable mTrickleRunnable = null;
    private TimeReceiver mTimeReceiver;
    private TextView mTimeNow;
    private GestureDetector mGestureDetector;
    /** 开始充电时的起始电量 */
    private int mStartLevel;
    private LinearLayout mChargeIndicate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        if(metrics.heightPixels <= 854) {
            setContentView(R.layout.activity_lock_screen_low_dpi);
        } else {
            setContentView(R.layout.activity_lock_screen);
        }
        initDate();
        initViews();
        initReceiver();
    }

    private void initDate() {
        mPreferences = getSharedPreferences("lock_screen", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        SingleInstanceBase.registerContext(getApplicationContext());
        mBatteryStateHandler = ExBatteryStateHandler.getInstance();
        mBatteryStateHandler.onStartCommand();

        mGestureDetector = new GestureDetector(this, this);

        mStartLevel = getIntent().getIntExtra(START_LEVEL, 0);
    }

    private void initViews() {

        mTimeNow = (TextView) findViewById(R.id.text_time_now);
        mTimeNow.setText(getTimeNow());

        mTextBatteryIndicator = (TextView) findViewById(R.id.text_battery_indicator);
        mTipText = (TextView) findViewById(R.id.text_charge_time);

        mChargeIndicate = (LinearLayout) findViewById(R.id.charge_indicate);
        mChargeIndicateView = (ChargeProgressIndicateView) findViewById(R.id.charging_progress_indicate);
        mChargeIndicateView.setProgress(BatteryInfo.getLevelPercent(this));
        mChargeClipView = (ChargeProgressClipImageView) findViewById(R.id.charging_green);
        mChargeClipView.setProgress(BatteryInfo.getLevelPercent(this));

        mChargeWaveView = (ChargeWaveView) findViewById(R.id.charge_wave_view);
        mChargeWaveView.setProgress(98);

        mBubbleAnimationView = (BubbleAnimationView) findViewById(R.id.bubble_view);
        mBubbleAnimationView.startAnimation();

        mChargeSpeed = (TextView) findViewById(R.id.charge_speed);
        mChargeContinuous = (TextView) findViewById(R.id.charge_continuous);
        mChargeTrickle = (TextView) findViewById(R.id.charge_trickle);

        mTextTimeHour = (TextView) findViewById(R.id.text_time_hour);
        mTextTimeMinute = (TextView) findViewById(R.id.text_time_minute);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_BATTERY_CHANGED);
        filter.addAction(Const.ACTION_ANSWER_AVAILABLE_TIME);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mBatteryStateChangedReceiver = new BatteryStateChangedReceiver();
        registerReceiver(mBatteryStateChangedReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        mTimeReceiver = new TimeReceiver();
        registerReceiver(mTimeReceiver,filter);
    }

    private void updateBatteryPower(int level) {
        if (level == Const.ONE_HUNDRED) {
            mChargeWaveView.setProgress(level);
            if (isCharging()) {
                if (mIsChargeComplete) {
                    showChargeComplete();
                } else {
                    if (!mIsTrickleCharging) {
                        mIsTrickleCharging = true;
                        mTipText.setText(R.string.trickle_charging_time);
                        mTrickleTime = mPreferences.getInt(BATTERY_TRICKLE_TIME, TRICKLE_TIME);
                        if (mTrickleTime > TRICKLE_TIME) {
                            mTrickleTime = TRICKLE_TIME;
                        }
                        if (mTrickleRunnable == null) {
                            mTrickleRunnable = new TrickleRunnable();
                        }
                        mChargeWaveView.postDelayed(mTrickleRunnable, 60 * 1000);
                    }
                }
            }
        } else {
            mChargeWaveView.setProgress(97);
            mIsTrickleCharging = false;
            mIsChargeComplete = false;
            mEditor.putBoolean(BATTERY_COMPLETE, false);
            mTrickleTime = TRICKLE_TIME;
            mEditor.putInt(BATTERY_TRICKLE_TIME, mTrickleTime);
        }
        if (mTextBatteryIndicator != null) {
            mTextBatteryIndicator.setText(level + "");
        }

        if (mChargeClipView == null || mChargeWaveView == null) {
            return;
        }
        if (isCharging()) {
            if (level <= 80) {
                mChargeClipView.clipBound(1);
                changeChargeStatus(true, false, false);
            } else if (level > 80 && level < 100) {
                mChargeClipView.clipBound(2);
                changeChargeStatus(true, true, false);
            } else {
                mChargeClipView.clipBound(3);
                if (!mIsChargeComplete) {
                    changeChargeStatus(true, true, true);
                } else {
                    mChargeSpeed.setTextColor(LIGHT_COLOR);
                    mChargeContinuous.setTextColor(LIGHT_COLOR);
                    mChargeTrickle.setTextColor(LIGHT_COLOR);
                }
            }
        }
        mChargeIndicateView.setProgress(level);
    }

    /**
     * <br>功能简述:设置充电状态是否高亮
     *
     * @param isSpeed
     * @param isContinuous
     * @param isTrickle
     */
    private void changeChargeStatus(boolean isSpeed, boolean isContinuous, boolean isTrickle) {
        if (isSpeed) {
            mChargeSpeed.setTextColor(LIGHT_COLOR);
        } else {
            mChargeSpeed.setTextColor(DARK_COLOR);
        }
        if (isContinuous) {
            mChargeContinuous.setTextColor(LIGHT_COLOR);
        } else {
            mChargeContinuous.setTextColor(DARK_COLOR);
        }
        if (isTrickle) {
            mChargeTrickle.setTextColor(LIGHT_COLOR);
        } else {
            mChargeTrickle.setTextColor(DARK_COLOR);
        }
    }

    /**
     * 获取旋转动画(各个动画不共用同一个旋转动画是因为会导致旋转动画异常)
     *
     * @return 返回旋转动画
     */
    private RotateAnimation getRotateAnim() {
        RotateAnimation rotateAnimation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        return rotateAnimation;
    }

    private void showChargeComplete() {
        mIsTrickleCharging = false;
        mIsChargeComplete = true;
        mEditor.putBoolean(BATTERY_COMPLETE, mIsChargeComplete).commit();
        mEditor.putInt(BATTERY_TRICKLE_TIME, mTrickleTime).commit();
        // 设置提示文本及时间
        mTipText.setText(R.string.charging_complete);
        mTextTimeHour.setVisibility(GONE);
        mTextTimeMinute.setVisibility(GONE);
        // 隐藏旋转的
//        mRotateTrickleView.setVisibility(INVISIBLE);
//        mRotateTrickleView.clearAnimation();
    }


    /**
     * <br>功能简述:是否正在充电
     *
     * @return
     */
    private boolean isCharging() {
        return BatteryInfo.isElectricity(this);
    }

    /**
     * <br>功能简述:改变是否充电的状态
     *
     * @param isCharging
     */
    private void switchIsCharging(boolean isCharging) {
        if (mChargeClipView == null || mChargeIndicateView == null || mTipText == null) {
            return;
        }

        if (isCharging) {
            mTipText.setVisibility(VISIBLE);
            if (mIsTrickleCharging && !mIsChargeComplete) {
                mTipText.setText(R.string.trickle_charging_time);
                mTextTimeHour.setVisibility(GONE);
                mTextTimeMinute.setVisibility(GONE);
            } else {
                mTipText.setText(R.string.charging_estimate_time);
            }
            mChargeIndicate.setVisibility(VISIBLE);
            mChargeClipView.setProgress(mBatteryLevel);
            mChargeIndicateView.setProgress(mBatteryLevel);
        } else {
            mChargeIndicate.setVisibility(View.INVISIBLE);
            mTextTimeHour.setVisibility(GONE);
            mTextTimeMinute.setVisibility(GONE);
            changeChargeStatus(false, false, false);
            int max = 15;
            int min = 5;
            final Random random = new Random();
            int s = random.nextInt(max) % (max - min + 1) + min;
            if (mStartLevel == 100) {
                mTipText.setVisibility(GONE);
            } else {
                mTipText.setVisibility(VISIBLE);
                mTipText.setText(getResources().getString(R.string.charging_speed, s, "%"));
            }
            if (mIsTrickleCharging && mTrickleRunnable != null) {
                mChargeWaveView.removeCallbacks(mTrickleRunnable);
            }
            mIsTrickleCharging = false;
        }
    }

    /**
     * 更新充电预估时间
     */
    private void updateTime(int totalSeconds) {
        if (mTextTimeHour == null || mTextTimeMinute == null) {
            return;
        }
        if (mTipText != null) {
            mTipText.setText(R.string.charging_estimate_time);
        }
        if (totalSeconds <= 0) {
            mTextTimeHour.setVisibility(View.GONE);
            mTextTimeMinute.setVisibility(View.GONE);
            return;
        }
        int totalMinute = totalSeconds / Const.SECOND_PER_MINITE;
        if (totalMinute == 0) {
            // 如果totalMinute等于0，则人为的让其等于1，防止显示时的错误
            totalMinute = 1;
        }
        int hour = totalMinute / Const.MINITE_PER_HOUR;
        int minute = totalMinute % Const.MINITE_PER_HOUR;

        if (hour == 0) {
            mTextTimeHour.setVisibility(View.GONE);
        } else {
            mTextTimeHour.setVisibility(View.VISIBLE);
            mTextTimeHour.setText(getResources().getString(R.string.time_hour, hour));
        }

        if (minute == 0) {
            mTextTimeMinute.setVisibility(View.GONE);
        } else {
            mTextTimeMinute.setVisibility(View.VISIBLE);
            mTextTimeMinute.setText(getResources().getString(R.string.time_minute, minute));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1 == null || e2 == null) {
            return false;
        }
        final float distanceX = e2.getX() - e1.getX();
        if (distanceX > 100 && Math.abs(velocityX) > Math.abs(velocityY)) {
            finish();
        }
        return false;
    }

    /**
     *<br>类描述：监听电池状态变化广播的接收器
     *<br>详细描述：
     *<br><b>Author sichard</b>
     *<br><b>Date 2017/2/22</b>
     */
    private class BatteryStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Const.ACTION_BATTERY_CHANGED)) {

                mBatteryLevel = intent.getIntExtra(Const.EXTRA_LEVEL, 1);
                if (isCharging()) {
                    switchIsCharging(true);
                    int totalSeconds = intent.getIntExtra(Const.EXTRA_TOTAL_TIME, 0);
                    if (mBatteryLevel != 100) {
                        updateTime(totalSeconds);
                    }
                }
                updateBatteryPower(mBatteryLevel);
            } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                switchIsCharging(false);
            }
        }
    }

    private class TimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mTimeNow.setText(getTimeNow());
            }
        }
    }

    private String getTimeNow() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String time = simpleDateFormat.format(new Date());
        return time;
    }

    /**
     *<br>类描述：刷新涓流充电时间的线程
     *<br>详细描述：
     *<br><b>Author sichard</b>
     *<br><b>Date 2017/2/21</b>
     */
    private class TrickleRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (LockScreenActivity.this) {
                if (mTrickleTime > TRICKLE_TIME) {
                    mTrickleTime = TRICKLE_TIME;
                }
                mTrickleTime -= 60;
                mEditor.putInt(BATTERY_TRICKLE_TIME, mTrickleTime).commit();
                if (mTrickleTime <= 0) {
                    showChargeComplete();
                    return;
                }
                mChargeWaveView.postDelayed(mTrickleRunnable, 60 * 1000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBatteryStateHandler != null) {
            mBatteryStateHandler.release();
        }

        if (mBatteryStateChangedReceiver != null) {
            unregisterReceiver(mBatteryStateChangedReceiver);
        }

        if (mTimeReceiver != null) {
            unregisterReceiver(mTimeReceiver);
        }

        // 内存泄漏问题处理：退出时需要回收
        if (mBubbleAnimationView != null) {
            mBubbleAnimationView.cleanAnimation();
        }

        // 内存泄漏问题处理：退出时需要回收
        if (mChargeWaveView != null && mTrickleRunnable != null) {
            mChargeWaveView.removeCallbacks(mTrickleRunnable);
        }
    }
}
