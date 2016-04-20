package com.ryg.slideview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

import com.ryg.slideview.MainActivity.MessageItem;

public class ListViewCompat extends ListView {

    private static final String TAG = "ListViewCompat";

    private SlideView CurrentOpenItemView;
    private boolean hasCacheOpenView = false;
    //UP之后滑块的开关状态，true 为开
    private boolean lastClose = false;
    public ListViewCompat(Context context) {
        super(context);
    }

    public ListViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        if(CurrentOpenItemView != null && CurrentOpenItemView.isOpen){
            setLongClickable(false);
        }else if(CurrentOpenItemView == null){
            setLongClickable(true);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int position = pointToPosition(x,y);
                MessageItem item = (MessageItem)getItemAtPosition(position);
                if(item != null && item.slideView != null){
                    if(item.slideView.equals(CurrentOpenItemView) && CurrentOpenItemView.isOpen){
                        Log.d(TAG, "click delete down");
                        break;
                    }
                }
                if(CurrentOpenItemView != null && CurrentOpenItemView.isOpen){
                    Log.d(TAG,"down down down");
                   CurrentOpenItemView.shrink();
                    CurrentOpenItemView = null;
                    hasCacheOpenView = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(CurrentOpenItemView != null && hasCacheOpenView){
                    //hasCacheOpenView = true,证明滑块已经打开了
                    CurrentOpenItemView.onRequireTouchEvent(event);
                    //屏蔽ListView的onTouchEvent()
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(CurrentOpenItemView != null && CurrentOpenItemView.isOpen){
                    Log.d(TAG,"up up");
                    CurrentOpenItemView.onRequireTouchEvent(event);
                    //记录手指离开后滑块最后的状态
                    if(!CurrentOpenItemView.isOpen){
                        Log.d(TAG, "up up up up up2");
                        CurrentOpenItemView = null;
                        hasCacheOpenView = false;
                        return false;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int position = pointToPosition(x, y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                MessageItem data = (MessageItem) getItemAtPosition(position);
                if(data != null){
                    //获取当前点击Item的SlideView
                    CurrentOpenItemView = data.slideView;
                }
            }
            break;
        }

        if (CurrentOpenItemView != null) {
            //使SlideView能够左滑
            CurrentOpenItemView.onRequireTouchEvent(event);
        }
        if(CurrentOpenItemView != null && CurrentOpenItemView.isOpen){
            //SlideView被打开了，缓存下来,用于后面MOVE时屏蔽ListView
            hasCacheOpenView = true;
            return true;
        }
        return super.onTouchEvent(event);
    }

}


