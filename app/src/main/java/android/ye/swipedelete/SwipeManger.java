package android.ye.swipedelete;

/**
 * Created by ye on 2016/11/18.
 */
public class SwipeManger {

    //建立单例模式
    private SwipeManger(){};
    private static SwipeManger mInstance = new SwipeManger();

    public static SwipeManger getSwpieMnager(){
        return mInstance;
    }


    private SwipeDeleteLayout currentLayout;//用来记录当前打开的SwipeLayout
    public void SetSwipeDeleteLayout(SwipeDeleteLayout swipeDeleteLayout){
        this.currentLayout = swipeDeleteLayout;
    }

    /**
     * 清空当前所记录的已经打开的layout
     */
    public void clearSwipeDeleteLayout(){
        currentLayout = null;
    }

    /**
     * 关闭已经打开的layout
     */
    public void closeSwipeDeleteLayout(){
        if (currentLayout!=null){
            currentLayout.close();
        }
    }

    /**
     * 判断当前是否应该能够滑动，如果没有打开的，则可以滑动。
     * 如果有打开的，则判断打开的layout和当前按下的layout是否是同一个
     * @param swipeDeleteLayout
     * @return
     */
    public boolean isShouldSwipe(SwipeDeleteLayout swipeDeleteLayout){
        if (currentLayout == null){
            //说明没有打开
            return true;
        }else {
            //说明有打开的
            return currentLayout == swipeDeleteLayout;
        }
    }
}
