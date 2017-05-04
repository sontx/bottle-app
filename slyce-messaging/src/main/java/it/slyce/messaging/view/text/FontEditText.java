package it.slyce.messaging.view.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.vanniktech.emoji.EmojiEditText;

import it.slyce.messaging.R;

/**
 * Created by John C. Hunchar on 2/2/16.
 */
public class FontEditText extends EmojiEditText {
    private static final String VALUE_FONT_DIR_PREFIX = "fonts/";

    private OnBackPressedListener onBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.onBackPressedListener = listener;
    }

    public FontEditText(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);

    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        init(context, attrs, defStyleAttr, 0);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onBackPressedListener != null && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onBackPressedListener.onBackPressed(this))
                return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * Always call this like a constructor
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        String font = null;

        // Load in font attribute
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.FontEditText,
                    defStyleAttr,
                    defStyleRes);

            font = a.getString(R.styleable.FontEditText_editText_font);
            a.recycle();
        }

        // Set font if available
        if (!TextUtils.isEmpty(font)) {
            StringBuilder fontDirBuilder = new StringBuilder();
            fontDirBuilder.append(VALUE_FONT_DIR_PREFIX).append(font);
            try {
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), fontDirBuilder.toString());
                setTypeface(typeface);
            } catch (Exception ignore) {

            }
        }
    }

    public interface OnBackPressedListener {
        boolean onBackPressed(View view);
    }
}
