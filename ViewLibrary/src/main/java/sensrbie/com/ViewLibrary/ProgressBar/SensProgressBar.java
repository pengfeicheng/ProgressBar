package sensrbie.com.ViewLibrary.ProgressBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengpengfei on 15/11/24.
 * 自定义进度条控件 </br>
 * 如果引用此控件请注明 github 的项目地址
 */
public class SensProgressBar extends LinearLayout implements View.OnTouchListener{

    private int mWidth;
    private int mHeight;
    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 200;

    private int mMaxValue = 5;
    private int mMinValue = 0;
    private int mProgressValue = 1;
    private int mRange;
    private int mRectLength;
    private int mCirclePointRectWidth;   // 每个圆点均分到的正方形的边长
    private int mProgressX = 0;          // X轴上的进度值

    private int mSelectedColor = Color.BLUE;
    private int mNormalColor = Color.WHITE;
    private int mBackgroundColor = Color.GRAY;
    private List<CirclePoint> mCirclePoints = new ArrayList<>();
    private String[] mProgressDescription;          // 刻度描述

    public interface ProgressChangeListener
    {
        void onProgressChange(int new_value);
    }

    private ProgressChangeListener mProgressChangeListener;

    public SensProgressBar(Context context) {
        this(context, null);
    }

    public SensProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(mBackgroundColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getDefaultSize(DEFAULT_WIDTH, widthMeasureSpec);
        mHeight = getDefaultSize(DEFAULT_HEIGHT,heightMeasureSpec);
    }

    /**
     * 设置正常进度条颜色
     * @param color
     */
    public void setNormalCircleColor(int color){
        mNormalColor = color;
    }

    /**
     * 设置选中进度条颜色
     * @param color
     */
    public void setSelectedCircleColor(int color){
        mSelectedColor = color;
    }

    /**
     * 设置进度条最大值
     * @param max_progress 大于 1 的值
     */
    public void setMaxProgress(int max_progress){
        if(max_progress > 1) mMaxValue = max_progress;
    }

    /**
     * 设置进度描述数据
     * @param desc
     */
    public void setProgressDescription(String[] desc){
        mProgressDescription = desc;
    }

    /**
     * 设置进度值监听
     * @param listener
     */
    public void setOnProgressChangeListener(ProgressChangeListener listener){
        mProgressChangeListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mWidth > 0 && mHeight > 0)
        {
            Paint paint = new Paint();
            paint.setColor(mNormalColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(30);

            mRange = mMaxValue - mMinValue;
            mCirclePointRectWidth = (int)Math.floor((double)mWidth / (mRange));
            mRectLength = mCirclePointRectWidth < mHeight ? mCirclePointRectWidth : mHeight;
            double min_radius_2 = (double)mRectLength * 0.15;
            double max_radius_2 = (double)mRectLength * 0.30;
            double difference_d = (max_radius_2 - min_radius_2) / mRange;
            int min_radius = (int)Math.floor(min_radius_2);
            int difference = (int)Math.floor(difference_d);

            int circle_rect_width_2 = mCirclePointRectWidth / 2;
            int circle_center_y = mHeight / 2;
            mCirclePoints.clear();
            mCirclePoints.add(new CirclePoint(circle_rect_width_2,circle_center_y,min_radius));
            for (int i = 1;i < mRange;i++)
            {
                int center_x = mCirclePointRectWidth * i + circle_rect_width_2;
                int circle_radius = min_radius + difference * i;
                mCirclePoints.add(new CirclePoint(center_x,circle_center_y,circle_radius));
            }

            // 进度条背景
            int line_left = mCirclePoints.get(0).center.x;
            int line_top = mCirclePoints.get(0).center.y - 3;
            int line_right = mCirclePoints.get(mRange - 1).center.x;
            int line_bottom = mCirclePoints.get(mRange - 1).center.y + 3;
            canvas.drawRect(line_left, line_top, line_right, line_bottom, paint);

            // 进度条选中部分高亮
            line_right = mProgressX > 0 ? mProgressX : mCirclePoints.get(mProgressValue).center.x;
            int line_max_x = mCirclePoints.get(mRange - 1).center.x;
            line_right = line_right > line_max_x ? line_max_x : line_right;
            paint.setColor(mSelectedColor);
            canvas.drawRect(line_left, line_top, line_right, line_bottom, paint);

            // 渲染进度小圆点
            for(int i = 0;i < mCirclePoints.size();i++)
            {
                CirclePoint point = mCirclePoints.get(i);
                paint.setColor(i <= mProgressValue ? mSelectedColor : mNormalColor);
                canvas.drawCircle(point.center.x, point.center.y, point.radius, paint);
            }

            // 绘制描述文字
            paint.setColor(mNormalColor);
            if(mProgressDescription != null && mProgressDescription.length == mRange)
            {
                for(int i = 0;i < mCirclePoints.size();i++)
                {
                    CirclePoint point = mCirclePoints.get(i);
                    String description = mProgressDescription[i];
                    int width = (int)paint.measureText(description);
                    int start_x = point.center.x - width / 2;
                    int start_y = point.center.y - point.radius - 25;
                    canvas.drawText(description,start_x,start_y,paint);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int touch_x = (int)Math.floor(event.getX());

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                // 有效刻度范围
                CirclePoint first = mCirclePoints.get(0);
                CirclePoint last = mCirclePoints.get(mCirclePoints.size() - 1);
                if(touch_x < first.center.x || touch_x > last.right.x) {
                    return true;
                }
                mProgressX = touch_x;

                // 计算进度值
                int index = touch_x / mCirclePointRectWidth;
                mProgressValue = mCirclePoints.get(index).left.x < mProgressX ? index : index - 1;
                break;

            case MotionEvent.ACTION_UP:

                first = mCirclePoints.get(0);
                last = mCirclePoints.get(mCirclePoints.size() - 1);

                int position_x = touch_x;
                if(position_x < first.right.x) position_x = first.center.x;
                if(position_x > last.right.x) position_x = last.center.x;

                // 归整处理
                index = position_x / mCirclePointRectWidth;
                CirclePoint point = mCirclePoints.get(index);
                if(point.left.x <= touch_x)
                {
                    mProgressX = point.center.x;
                }
                else
                {
                    if(index > 0) {
                        mProgressX = mCirclePoints.get(index - 1).center.x;
                    }
                }

                // 计算进度值
                mProgressValue = point.left.x <= mProgressX ? index : index - 1;

                if(mProgressChangeListener != null) {
                    mProgressChangeListener.onProgressChange(mProgressValue);
                }
                break;
        }

        invalidate();
        return true;
    }

    // 进度条圆点
    class CirclePoint {

        int radius;              // 圆半径
        Point center,left,right; // 中心点、最左边点、最右边点

        CirclePoint(int center_x,int center_y,int radius)
        {
            this.radius = radius;
            this.center = new Point(center_x,center_y);
            this.left = new Point(center_x - radius,center_y);
            this.right = new Point(center_x + radius,center_y);
        }
    }
}
