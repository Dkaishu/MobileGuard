package com.fang.mobileguard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义TextView，使其总是Foucused。
 * Created by Administrator on 2016/7/29.
 */
public class FocusTextView extends TextView {
    public FocusTextView(Context context) {
        super(context);
    }

    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
