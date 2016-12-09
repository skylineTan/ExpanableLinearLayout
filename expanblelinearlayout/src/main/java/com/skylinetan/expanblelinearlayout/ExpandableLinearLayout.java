package com.skylinetan.expanblelinearlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by skylineTan on 16/12/9.
 */
public class ExpandableLinearLayout extends LinearLayout implements View.OnClickListener{

    private static final int DEFAULT_ANIMATION_DURATION = 300;
    private int mAnimationDuration;

    private ViewGroup mNormalViewGroup;
    private ViewGroup mExpandCollapseViewGroup;
    private ViewGroup mExpandCollapseView;

    //默认为收缩状态
    private boolean isCollapsed = true;
    private boolean isFinallyCollapsed = true;
    private boolean mAnimating;
    private int mCollapsedHeight;
    //扩展的最大高度
    private int mMaxHeight;
    private int mMaxAllHeight;
    private boolean isFirst = true;

    private OnExpandCollapseViewClickListener mListener;

    public ExpandableLinearLayout(Context context) {
        this(context, null);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onFinishInflate() {
        findViews();
    }

    private void init(AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLinearLayout);
        mAnimationDuration = ta.getInt(R.styleable.ExpandableLinearLayout_animationDuration, DEFAULT_ANIMATION_DURATION);
        ta.recycle();
    }

    protected void findViews(){
        mNormalViewGroup = (ViewGroup) getChildAt(0);
        mExpandCollapseViewGroup = (ViewGroup) getChildAt(1);
        mExpandCollapseView = (ViewGroup) getChildAt(2);
        mExpandCollapseView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        isCollapsed = !isCollapsed;
        mAnimating = true;
        mListener.onClick(view, isCollapsed);

        Animation animation;
        if(isCollapsed){
            animation = new ExpandCollapseAnimation(this, getHeight(), mCollapsedHeight);
        }else {
            animation = new ExpandCollapseAnimation(this, getHeight(), mMaxAllHeight);
        }

        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                mAnimating = false;
                if(isCollapsed){
                    isFinallyCollapsed = true;
                }else {
                    isFinallyCollapsed = false;
                }
                requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        clearAnimation();
        startAnimation(animation);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mAnimating;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //默认设置为可见
        mExpandCollapseViewGroup.setVisibility(VISIBLE);
        //测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        //如果是第一次，获取应该扩展的最大高度
        if(isFirst) {
            mMaxHeight = mNormalViewGroup.getMeasuredHeight() + mExpandCollapseViewGroup.getMeasuredHeight();
            mMaxAllHeight = mMaxHeight + mExpandCollapseView.getMeasuredHeight();
        }

        if(isCollapsed && isFirst){
            mExpandCollapseViewGroup.setVisibility(GONE);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(widthSize, getMeasuredHeight());
            isFirst = false;
        }

        if(isFinallyCollapsed && !mAnimating){
            mExpandCollapseViewGroup.setVisibility(GONE);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(widthSize, getMeasuredHeight());
        }

        if(!isFinallyCollapsed && !mAnimating){
            mExpandCollapseViewGroup.setVisibility(VISIBLE);
            setMeasuredDimension(widthSize, mMaxAllHeight);
        }

        if(isCollapsed){
            mCollapsedHeight = getMeasuredHeight();
        }
    }

    //设置
    class ExpandCollapseAnimation extends Animation{

        private final View mView;
        private final int mStartHeight;
        private final int mEndHeight;

        public ExpandCollapseAnimation(View view, int startHeight, int endHeight){
            mView = view;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setDuration(mAnimationDuration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final int newHeight = (int)((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
            mView.getLayoutParams().height = newHeight;
            mView.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }


    public void setOnECViewListener(OnExpandCollapseViewClickListener listener){
        this.mListener = listener;
    }

    public interface OnExpandCollapseViewClickListener{
        void onClick(View view, boolean isCollapsed);
    }
}
