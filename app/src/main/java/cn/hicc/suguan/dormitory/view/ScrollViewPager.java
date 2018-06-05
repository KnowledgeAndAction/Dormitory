package cn.hicc.suguan.dormitory.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/9/25/025.
 */

public class ScrollViewPager extends ViewPager {
    private boolean noScroll = false;

    public ScrollViewPager(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    public ScrollViewPager(Context context) {
        super(context);
    }

    /**
     * 是否禁止viewpager禁止滑动
     * @param noScroll true禁止，false不禁止
     */
    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // 返回false，不消费滑动事件，进而禁止滑动，
        // 返回true，可以滑动
        if (noScroll){
            return false;
        } else{
            return super.onTouchEvent(arg0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}
