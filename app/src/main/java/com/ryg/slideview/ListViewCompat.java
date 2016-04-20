package com.ryg.slideview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

import com.ryg.slideview.MainActivity.MessageItem;

public class ListViewCompat extends ListView {

    private static final String TAG = "ListViewCompat";

    private SlideView currentOpenItemView;
    private boolean hasCacheOpenView = false;
    private int currentOpenPosition = -1;

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
        int position = pointToPosition(x,y);
        if(currentOpenItemView != null && currentOpenItemView.isOpen){
            setLongClickable(false);
        }else if(currentOpenItemView == null){
            setLongClickable(true);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(position <= getHeaderViewsCount() - 1){
                    break;
                }

                if (currentOpenPosition == position) {
                    if (currentOpenItemView != null && currentOpenItemView.isOpen) {
                        Log.d(TAG, "click delete down");
                        break;
                    }
                }

                if( currentOpenItemView != null ){
                    Log.d(TAG,"down down down");
                    currentOpenItemView.shrink();
                    currentOpenItemView = null;
                    currentOpenPosition = -1;
                    hasCacheOpenView = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(position > getHeaderViewsCount()-1 && currentOpenItemView != null && hasCacheOpenView){
                    //hasCacheOpenView = true,证明滑块已经打开了
                    currentOpenItemView.onRequireTouchEvent(event);
                    //屏蔽ListView的onTouchEvent()
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG,"up");
                Log.d(TAG,"up"+(currentOpenItemView !=null));
                if(currentOpenItemView!=null) Log.d(TAG,"up "+"isopen "+(currentOpenItemView.isOpen));
                if(currentOpenItemView != null && !currentOpenItemView.isOpen){
                    hasCacheOpenView = false;
                    currentOpenPosition = -1;
                    currentOpenItemView = null;
                }
                if(currentOpenItemView != null && currentOpenItemView.isOpen){
                    Log.d(TAG,"up up");
                    currentOpenItemView.onRequireTouchEvent(event);
                    //记录手指离开后滑块最后的状态
                    if(!currentOpenItemView.isOpen){
                        Log.d(TAG, "up up up up up2");
                        currentOpenItemView = null;
                        currentOpenPosition = -1;
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
                Log.e(TAG,"position "+position);
                if(position > getHeaderViewsCount()-1) {
                        MessageItem data = (MessageItem) getItemAtPosition(position);
                        if (data != null) {
                            //获取当前点击Item的SlideView
                            currentOpenItemView = data.slideView;
                        }
                }
            }
            break;
        }

        if (currentOpenItemView != null) {
            //使SlideView能够左滑
            currentOpenItemView.onRequireTouchEvent(event);
        }
        if(currentOpenItemView!= null && currentOpenItemView.isOpen){
            //SlideView被打开了，缓存下来,用于后面MOVE时屏蔽ListView
            hasCacheOpenView = true;
            currentOpenPosition = position;
            return true;
        }
        return super.onTouchEvent(event);
    }

}


