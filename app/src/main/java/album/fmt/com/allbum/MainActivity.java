package album.fmt.com.allbum;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import album.fmt.com.allbum.eneity.ImageBean;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    RecyclerView mRecycleView;
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<ImageBean> list = new ArrayList<ImageBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecycleView = (RecyclerView) findViewById(R.id.mRecycleView);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        init();
    }
    private void  init(){
        Observable.create(new Observable.OnSubscribe<HashMap<String, List<String>>>() {

            @Override
            public void call(Subscriber<? super HashMap<String, List<String>>> subscriber) {
                subscriber.onNext(getDatas());
            }
        }). subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<HashMap<String,List<String>>, List<ImageBean>>() {
            @Override
            public List<ImageBean> call(HashMap<String, List<String>> stringListHashMap) {
                return subGroupOfImage(stringListHashMap);
            }
        }).subscribe(new Action1<List<ImageBean>>() {
            @Override
            public void call(List<ImageBean> imageBeen) {
                mRecycleView.setAdapter(new HomeAdapter(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> childList = mGruopMap.get(list.get((Integer) v.getTag()).getFileName());
                        Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                        intent.putStringArrayListExtra("data", (ArrayList<String>)childList);
                        startActivity(intent);
                        finish();
                    }
                }));
            }
        });
    }


    private HashMap<String, List<String>> getDatas() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = MainActivity.this.getContentResolver();

        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

        while (mCursor.moveToNext()) {
            //获取图片的路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

            //获取该图片的父路径名
            String parentName = new File(path).getParentFile().getName();
            //根据父路径名将图片放入到mGruopMap中
            if (!mGruopMap.containsKey(parentName)) {
                List<String> chileList = new ArrayList<String>();
                chileList.add(path);
                mGruopMap.put(parentName, chileList);
            } else {
                mGruopMap.get(parentName).add(path);
            }
        }
        mCursor.close();
        return mGruopMap;
    }


    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
        if(mGruopMap.size() == 0){
            return null;
        }
        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            mImageBean.setFileName(key);
            mImageBean.setmCounts(value.size());
            mImageBean.setmPath(value.get(0));//获取该组的第一张图片
            list.add(mImageBean);
        }
        return list;
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private View.OnClickListener mOnClickListener;

        public HomeAdapter(View.OnClickListener mOnClickListener) {
            this.mOnClickListener = mOnClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.album_item, parent, false);
            MyViewHolder holder = new MyViewHolder(inflate);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.iv.setTag(position);
            Picasso.with(MainActivity.this).load(new File(list.get(position).getmPath())).resize(120,240).into(holder.iv);
            holder.mTextViewTitle.setText(list.get(position).getFileName());
            holder.mTextViewCounts.setText(Integer.toString(list.get(position).getmCounts()));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView iv;
            TextView mTextViewTitle;
            TextView mTextViewCounts;
            public MyViewHolder(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.list_item_iv);
                mTextViewTitle= (TextView) view.findViewById(R.id.album_list_title);
                mTextViewCounts= (TextView) view.findViewById(R.id.album_num);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener.onClick(iv);
                    }
                });
            }
        }
    }
}
