package player.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.leilu.playerframe.R;


public class CommonLoadingView extends AppCompatImageView {

    private Animation mAnimation;

    public CommonLoadingView(Context context) {
        this(context, null);
    }

    public CommonLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonLoadingView);
        int bgResId = ta.getResourceId(R.styleable.CommonLoadingView_BackGroundResId, R.drawable.progressloading);
        int animationResId = ta.getResourceId(R.styleable.CommonLoadingView_AnimationResId, R.anim.progressbar);
        ta.recycle();
        setImageResource(bgResId);
        mAnimation = AnimationUtils.loadAnimation(context, animationResId);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mAnimation != null) {
            if (getVisibility() == View.VISIBLE) {
                startAnimation(mAnimation);
            } else {
                clearAnimation();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }
}
