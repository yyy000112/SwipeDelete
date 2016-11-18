package android.ye.swipedelete;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.CycleInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.ye.swipedelete.utils.Constant;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> names = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.ll_list);

        for (int i = 0; i<Constant.NAMES.length; i++){
            names.add(Constant.NAMES[i]);
        }
        listView.setAdapter(new MyAdapter());
    }



    class MyAdapter extends BaseAdapter {
         @Override
         public int getCount() {
             return names.size();
         }

         @Override
         public String getItem(int position) {
             return names.get(position);
         }

         @Override
         public long getItemId(int position) {
             return position;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             if (convertView == null) {
                 convertView = View.inflate(getApplicationContext(), R.layout.list_item, null);
             }
             final ViewHolder holder = ViewHolder.getHolder(convertView);
             holder.tvNmae.setText(names.get(position));
             holder.swipeDeleteLayout.setTag(position);
             holder.swipeDeleteLayout.setOnSwipeStateChangeListener(new SwipeDeleteLayout.OnSwipeStateChangeListener() {
                 @Override
                 public void onOpen() {

                 }

                 @Override
                 public void onClose() {
                     com.nineoldandroids.view.ViewPropertyAnimator.animate(holder.ivPic).translationXBy(15)
                             .setInterpolator(new CycleInterpolator(4)).setDuration(500)
                             .start();
                 }
             });
             return convertView;
         }

     }
         static class ViewHolder{
             TextView tvNmae,tvDelete;
             SwipeDeleteLayout swipeDeleteLayout;
             ImageView ivPic;
             public ViewHolder(View convertView){
                 tvNmae = (TextView) convertView.findViewById(R.id.tv_name);
                 tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
                 ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
                 swipeDeleteLayout = (SwipeDeleteLayout) convertView.findViewById(R.id.swipe_layout);
             }
             public static ViewHolder getHolder(View convertView){
                 ViewHolder holder = (ViewHolder) convertView.getTag();
                 if (holder == null){
                     holder = new ViewHolder(convertView);
                     convertView.setTag(holder);
                 }
                 return holder;
             }
         }

}
