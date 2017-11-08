package com.gu.userinfo.observablescroll.cloudmusic_smartrefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gu.userinfo.observablescroll.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/11/9
 */
public class RefreshLayoutActivity extends AppCompatActivity {
    List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refreshlayout_main);
        data = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            data.add(String.valueOf(i));
        }
        MyAdapter adapter = new MyAdapter();
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        //        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        //        refreshLayout.setEnableOverScrollDrag(true);
        //        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
        //            @Override
        //            public void onRefresh(RefreshLayout refreshlayout) {
        //                refreshlayout.finishRefresh(2000);
        //            }
        //        });
        //        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
        //            @Override
        //            public void onLoadmore(RefreshLayout refreshlayout) {
        //                refreshlayout.finishLoadmore(2000);
        //            }
        //        });
    }

    class MyAdapter extends RecyclerView.Adapter {
        LayoutInflater inflater;

        public MyAdapter() {
            super();
            inflater = LayoutInflater.from(RefreshLayoutActivity.this);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(inflater.inflate(R.layout.rv_holder_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }
    }
}
