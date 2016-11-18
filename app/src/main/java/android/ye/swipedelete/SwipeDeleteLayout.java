package android.ye.swipedelete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by ye on 2016/11/18.
 */
public class SwipeDeleteLayout extends FrameLayout {


    private View content;// item内容区域的view
    private View delete;// delete区域的view
    private ViewDragHelper viewDragHelper;
    private int contentWidth;// content区域的宽度
    private int deleteWidth;// delete区域的宽度
    private int deleteHeight;// delete区域的高度
    private float downX;
    private float downY;

    public SwipeDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public SwipeDeleteLayout(Context context) {
        super(context);
        init();
    }

    public SwipeDeleteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    enum SwipeState{
        Open,Close
    }

    private SwipeState currentState = SwipeState.Close;

    /**
     * 当SlideMenuLayout的xml布局的结束标签被读取完成会执行该方法，
     * 此时会知道自己有几个子View了 一般用来初始化子View的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content = getChildAt(0);
        delete = getChildAt(1);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentWidth = content.getMeasuredWidth();
        deleteWidth = delete.getMeasuredWidth();
        deleteHeight = delete.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        content.layout(0,0,contentWidth,deleteHeight);
        delete.layout(content.getRight(),0,content.getRight()+deleteWidth,deleteHeight);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        //如果当前有打开的，则需要直接拦截，交给onTouch处理
        if (!SwipeManger.getSwpieMnager().isShouldSwipe(this)){
            //先关闭已经打开的layout
            SwipeManger.getSwpieMnager().closeSwipeDeleteLayout();
            result = true;
        }
        return result;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果当前有打开的，则下面的逻辑不能执行
        if (!SwipeManger.getSwpieMnager().isShouldSwipe(this)){
            requestDisallowInterceptTouchEvent(true);
            return true;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                //获取x和y方向移动的距离
                float moveX = event.getX();
                float moveY = event.getY();
                float deltX = moveX-downX;
                float deltY = moveY-downY;
                if (Math.abs(deltX)>Math.abs(deltY)){
                    //表示移动是偏向于水平方向，那么应该SwipeLayout应该处理，请求listview不要拦截
                    requestDisallowInterceptTouchEvent(true);
                }
                //更新想x,y
                downX = moveX;
                downY = moveY;
                break;

        }
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == content){
                if (left>0) left = 0;
                 if(left<-deleteWidth) left=-deleteWidth;
            }else if (delete == child){
                if (left<(contentWidth-deleteWidth)) left = contentWidth-deleteWidth;
                if (left>contentWidth) left = contentWidth;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == content){
                //手动移动deleteView
                delete.layout(delete.getLeft() + dx, delete.getTop() + dy, delete.getRight() + dx, delete.getBottom() + dy);
            }else if ( delete== changedView){
                //手动移动contentView
                content.layout(content.getLeft()+dx,content.getTop()+dy, content.getRight()+dx,content.getBottom()+dy);
            }

            //判断开关逻辑
            if (content.getLeft() == 0 && currentState != SwipeState.Close){
                //说明应该将state更改为关闭
                currentState = SwipeState.Close;
                //回调接口
                if (listener !=null){
                    listener.onClose();
                }

                //说明当前的Layout已经关闭，需要让Manager清空一下
                SwipeManger.getSwpieMnager().clearSwipeDeleteLayout();
            }else if (content.getLeft()== -deleteWidth && currentState != SwipeState.Open){
                //说明应该将state更改为打开
                currentState = SwipeState.Open;
                //回调接口
                if (listener!=null){
                    listener.onOpen();
                }
                //当前的layout已经打开，需要让Manager记录一下下
                SwipeManger.getSwpieMnager().SetSwipeDeleteLayout(SwipeDeleteLayout.this);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (content.getLeft()<-deleteWidth/2){
                open();
            }else {
                close();
            }
        }

    };

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 打开
     */
    private void open() {
        viewDragHelper.smoothSlideViewTo(content,-deleteWidth,content.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeDeleteLayout.this);
    }

    /**
     *关闭
     */
    public void close() {
        viewDragHelper.smoothSlideViewTo(content,0,content.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeDeleteLayout.this);
    }

    //建立滑动删除的状态监听
    private OnSwipeStateChangeListener listener;
    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener){
        this.listener = listener;
    }

    public interface OnSwipeStateChangeListener{
        public void onOpen();
        public void onClose();

    }
}
