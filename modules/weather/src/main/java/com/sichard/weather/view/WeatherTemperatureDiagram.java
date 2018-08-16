package com.sichard.weather.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.sichard.weather.R;
import com.sichard.weather.WeatherUtils;
import com.sichard.weather.weatherData.ForecastHourlyEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <br>类描述:温度曲线图
 * <br>详细描述:作图时，以view的左上角为(0, 0)开始计算，总宽度为dimen值weather_hour_item_width的12倍，
 * <br>总高度为130dp，其中0-40dp是显示温度预留的空间，40-100之间为曲线作图空间，100-130dp给渐变曲线预留的空间。
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/18</b>
 */

public class WeatherTemperatureDiagram extends View {
    private int mWidth;
    private int mHeight;
    private float mDensity;
    /** 对应最低温度y的坐标dp值 */
    private final int LOWEST_TEMPERATURE_SCALE = 100;
    /** 对应最高温度y的坐标dp值 */
    private final int HIGHEST_TEMPERATURE_SCALE = 40;
    /** 曲度 */
    private float mIntensity = 0.2f;
    /** 温度曲线 */
    private Path mPath;
    /** 渐变色的封闭路径 */
    private Path mGradientPath = new Path();

    private Bitmap mNowBitmap;
    private Rect mBitmapRect;

    private int mLineColor = Color.parseColor("#32D1FF");
    private int mPathColor = Color.parseColor("#3C32D1FF");
    private Paint mPathPaint;
    private Paint mLinePaint;
    private Paint mTextPaint;
    private ArrayList<CPoint> mPointList = new ArrayList<>();
    private float mSizeOfBitmap;

    public WeatherTemperatureDiagram(Context context) {
        super(context);
    }

    public WeatherTemperatureDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDensity = getResources().getDisplayMetrics().density;
        mSizeOfBitmap = 23 * mDensity;

        mNowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_now_cloudy);
        mBitmapRect = new Rect();

        mPathPaint = new Paint();
        mPathPaint.setColor(mLineColor);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.FILL);
        mPathPaint.setStrokeWidth(mDensity * 2);

        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mDensity * 2);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor("#89000000"));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(mDensity * 2);
        mTextPaint.setTextSize(mDensity * 14);
    }

    public WeatherTemperatureDiagram(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPath != null) {
            canvas.drawPath(mPath, mLinePaint);
            mGradientPath.reset();
            mGradientPath.addPath(mPath);
            mGradientPath.lineTo(mWidth, mHeight);
            mGradientPath.lineTo(0, mHeight);
            mGradientPath.close();
            LinearGradient linearGradient = new LinearGradient(0, 30 * mDensity, 0, mHeight, mPathColor, Color.WHITE, Shader.TileMode.CLAMP);
            mPathPaint.setShader(linearGradient);
            canvas.drawPath(mGradientPath, mPathPaint);
        }

        for (int i = 0; i < mPointList.size(); i++) {
            final CPoint cPoint = mPointList.get(i);
            if (i == 1) {
                mBitmapRect.left = (int) (cPoint.x - mSizeOfBitmap);
                mBitmapRect.top = (int) (cPoint.y - mSizeOfBitmap);
                mBitmapRect.right = (int) (cPoint.x + mSizeOfBitmap);
                mBitmapRect.bottom = (int) (cPoint.y + mSizeOfBitmap);
                canvas.drawBitmap(mNowBitmap, null, mBitmapRect, null);
            }
            if (i != 0 && i != mPointList.size() - 1) {
                canvas.drawText(cPoint.temperature + "°", cPoint.x - 8 * mDensity, cPoint.y - mSizeOfBitmap, mTextPaint);
            }
        }
    }

    /**
     * 设置当前时间的温度信息
     * @param min 最低温低
     * @param max 最高温度
     * @param temperature 当前温度
     */
    public void addNowData(int min, int max, int temperature) {
        mPointList.clear();
        final float scale = (LOWEST_TEMPERATURE_SCALE - HIGHEST_TEMPERATURE_SCALE) / (float) (max - min);
        final int weatherItemWidth = getResources().getDimensionPixelSize(R.dimen.weather_hour_item_width);

        //添加温度线头部假数据
        int fakeTemperature = new Random().nextBoolean() ? temperature + 1 : temperature -1;
        fakeTemperature = Math.min(max, fakeTemperature);
        fakeTemperature = Math.max(min, fakeTemperature);
        int y = (int) (LOWEST_TEMPERATURE_SCALE - (temperature - min) * scale);
        CPoint pointFake = new CPoint(0, y * mDensity, fakeTemperature);
        mPointList.add(pointFake);

        y = (int) (LOWEST_TEMPERATURE_SCALE - (temperature - min) * scale);
        CPoint point = new CPoint(weatherItemWidth / 2, y * mDensity, temperature);
        mPointList.add(point);
    }

    /**
     * 设置未来二十四小时的温度信息
     * @param min 最低文帝
     * @param max 最高温度
     * @param currentTemp 当前温度
     * @param entityList 小时温度列表
     */
    public void setData(int min, int max, int currentTemp, List<ForecastHourlyEntity> entityList) {
        mPointList.clear();
        final float scale = (LOWEST_TEMPERATURE_SCALE - HIGHEST_TEMPERATURE_SCALE) / (float)(max - min);
        final int weatherItemWidth = getResources().getDimensionPixelSize(R.dimen.weather_hour_item_width);
        //实际就是WeatherTemperatureDiagram的mWidth,由于最开始setData时，Layout还未完成所以mWidht=0，故此处需要手动计算宽度
        final int weatherDiagramWidth = weatherItemWidth * 25;

        //添加温度线头部假数据
        int fakeTemperature = new Random().nextBoolean() ? currentTemp + 1 : currentTemp -1;
        fakeTemperature = Math.min(max, fakeTemperature);
        fakeTemperature = Math.max(min, fakeTemperature);
        int y = (int) (LOWEST_TEMPERATURE_SCALE - (currentTemp - min) * scale);
        CPoint pointFakeHead = new CPoint(0, y * mDensity, fakeTemperature);
        mPointList.add(pointFakeHead);

        //添加当前时间数据
        y = (int) (LOWEST_TEMPERATURE_SCALE - (currentTemp - min) * scale);
        CPoint pointCurrent = new CPoint(weatherItemWidth / 2, y * mDensity, currentTemp);
        mPointList.add(pointCurrent);

        //添加未来24小时数据
        int temperature = 0;
        for (int i = 0; i < entityList.size(); i++) {
            final ForecastHourlyEntity entity = entityList.get(i);
            temperature = WeatherUtils.getCurrentCTemp(entity.Temperature.Value, entity.Temperature.UnitType);
            y = (int) (LOWEST_TEMPERATURE_SCALE - (temperature - min) * scale);
            CPoint point = new CPoint((i + 1) * weatherItemWidth + weatherItemWidth / 2, y * mDensity, temperature);
            mPointList.add(point);
        }

        //添加温度线尾部假数据
        fakeTemperature = new Random().nextBoolean() ? temperature + 1 : temperature -1;
        fakeTemperature = Math.min(max, fakeTemperature);
        fakeTemperature = Math.max(min, fakeTemperature);
        y = (int) (LOWEST_TEMPERATURE_SCALE - (temperature - min) * scale);
        CPoint pointFakeEnd = new CPoint(weatherDiagramWidth, y * mDensity, fakeTemperature);
        mPointList.add(pointFakeEnd);

        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        drawTemperaturePath(mPath, mPointList);

        invalidate();
    }

    /**
     * 画曲线
     *  @param path
     * @param points
     */
    private void drawTemperaturePath(Path path, List<CPoint> points) {
        if (points.size() > 1) {
            for (int j = 0; j < points.size(); j++) {

                CPoint point = points.get(j);

                if (j == 0) {
                    CPoint next = points.get(j + 1);
                    point.dx = ((next.x - point.x) * mIntensity);
                    point.dy = ((next.y - point.y) * mIntensity);
                } else if (j == points.size() - 1) {
                    CPoint prev = points.get(j - 1);
                    point.dx = ((point.x - prev.x) * mIntensity);
                    point.dy = ((point.y - prev.y) * mIntensity);
                } else {
                    CPoint next = points.get(j + 1);
                    CPoint prev = points.get(j - 1);
                    point.dx = ((next.x - prev.x) * mIntensity);
                    point.dy = ((next.y - prev.y) * mIntensity);
                }

                // create the cubic-spline path
                if (j == 0) {
                    path.moveTo(point.x, point.y);
                } else {
                    CPoint prev = points.get(j - 1);
                    path.cubicTo(prev.x + prev.dx, (prev.y + prev.dy),
                            point.x - point.dx, (point.y - point.dy),
                            point.x, point.y);
                }
            }
        }
    }

    /**
     * 根据天气值(weatherIcon)来设置曲线图的起点图片和曲线颜色
     * @param weatherIcon 天气id
     */
    public void setWeatherIconNumber(int weatherIcon) {
        switch (weatherIcon) {
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
                mNowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_now_night);
                mLinePaint.setColor(Color.parseColor("#AB7FE6"));
                mPathColor = Color.parseColor("#3CA268F0");
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 30:
                mNowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_now_sunny);
                mLinePaint.setColor(Color.parseColor("#FF9147"));
                mPathColor = Color.parseColor("#3CFF9147");
                break;
            case 22:
            case 23:
            case 24:
            case 31:
            case 43:
            case 44:
                mNowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_now_snow);
                mLinePaint.setColor(Color.parseColor("#46C8FE"));
                mPathColor = Color.parseColor("#3C46D5FE");
                break;
            case 6:
            case 7:
            case 8:
            case 32:
            case 11:
                mNowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_now_cloudy);
                mLinePaint.setColor(Color.parseColor("#32D1FF"));
                mPathColor = Color.parseColor("#3C32D1FF");
                break;
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 25:
            case 26:
            case 29:
            case 39:
            case 40:
            case 41:
                mNowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_now_rain);
                mLinePaint.setColor(Color.parseColor("#01CFE2"));
                mPathColor = Color.parseColor("#3C00E9FF");
                break;
            case 15:
            case 42:
                mNowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_now_thunderstorm);
                mLinePaint.setColor(Color.parseColor("#8A82B1"));
                mPathColor = Color.parseColor("#3C6450C3");
                break;
        }
    }

    private class CPoint {

        public float x = 0f;
        public float y = 0f;

        /**
         * x-axis distance
         */
        public float dx = 0f;

        /**
         * y-axis distance
         */
        public float dy = 0f;

        /** 点对应的温度 */
        public int temperature;

        public CPoint(float x, float y, int temperature) {
            this.x = x;
            this.y = y;
            this.temperature = temperature;
        }
    }
}
