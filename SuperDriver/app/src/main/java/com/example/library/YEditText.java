package com.example.library;

import android.annotation.SuppressLint;
import android.widget.EditText;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * EditText获取焦点时,hint值消失
 *
 * @author admin2016-9-5 下午4:32:36
 */
@SuppressLint("AppCompatCustomView")
public class YEditText extends EditText {

    private String hint;

    public YEditText(Context context) {
        super(context);
        init();
    }

    public YEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 获得hint值
     *
     * @author admin 2016-9-5 下午4:32:19
     */
    private void init() {
        hint = getHint().toString();
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            setHint("");
        } else {
            setHint(hint);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
