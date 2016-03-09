package album.fmt.com.allbum;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends Activity {
    RecyclerView mRecycleView;
    List<String> mDatas = new ArrayList<>();
    HomeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        mRecycleView = (RecyclerView) findViewById(R.id.mRecycleView);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 4));

        if (getIntent().getStringArrayListExtra("data") != null) {
            mDatas = getIntent().getStringArrayListExtra("data");
            initAdapter();
        }
    }

    private void initAdapter() {
        if (mAdapter == null) {
            //给item设置点击事件
            mAdapter = new HomeAdapter(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            mRecycleView.setAdapter(mAdapter);
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private View.OnClickListener mOnClickListener;

        public HomeAdapter(View.OnClickListener mOnClickListener) {
            this.mOnClickListener = mOnClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(AlbumActivity.this).inflate(R.layout.album_item, parent, false);
            MyViewHolder holder = new MyViewHolder(inflate);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Picasso.with(AlbumActivity.this).load(new File(mDatas.get(position))).resize(120,240).into(holder.iv);
            holder.mTitle.setVisibility(View.GONE);
            holder.mNum.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView iv;
            TextView mTitle;
            TextView mNum;

            public MyViewHolder(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.list_item_iv);
                mTitle = (TextView) view.findViewById(R.id.album_list_title);
                mNum = (TextView) view.findViewById(R.id.album_num);
            }
        }
    }
}
