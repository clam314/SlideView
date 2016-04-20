package com.ryg.slideview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.ryg.slideview.MainActivity.MessageItem;

public class ListViewCompat extends ListView {

    private static final String TAG = "ListViewCompat";

    private SlideView CurrentOpenItemView;
    private boolean hasCacheOpenView = false;

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
                int position = pointToPosition(x, y);
                MessageItem d = (MessageItem)getItemAtPosition(position);
                if(d!=null && d.slideView.isOpen){
                    break;
                }
                boolean hasOpen = false;
                for (int i = 0; i < getCount(); i++) {
                    MessageItem data = (MessageItem) getItemAtPosition(i);
                    SlideView slideView = null;
                    if (data != null) {
                        slideView = data.slideView;
                    }
                    if (slideView != null && slideView.isOpen) {
                        slideView.shrink();
                        hasOpen = true;
                    }
                }
                if (hasOpen) {
                    CurrentOpenItemView = null;
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(CurrentOpenItemView != null && hasCacheOpenView){
                    CurrentOpenItemView.onRequireTouchEvent(event);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(CurrentOpenItemView != null && CurrentOpenItemView.isOpen){
                    CurrentOpenItemView.onRequireTouchEvent(event);
                    return false;
                }
                if(CurrentOpenItemView !=null) {
                    CurrentOpenItemView.onRequireTouchEvent(event);
                    CurrentOpenItemView = null;
                    hasCacheOpenView = false;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = pointToPosition(x, y);
                MessageItem data = (MessageItem) getItemAtPosition(position);
                if(data != null){
                    CurrentOpenItemView = data.slideView;
                }
            }
            default:
                break;
        }

        if (CurrentOpenItemView != null) {
            CurrentOpenItemView.onRequireTouchEvent(event);
        }
        if(CurrentOpenItemView != null && CurrentOpenItemView.isOpen){
            hasCacheOpenView = true;
            return true;
        }
        return super.onTouchEvent(event);
    }

}


