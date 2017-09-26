/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gu.userinfo.observablescroll;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class SimpleHeaderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_HEADER_DIVIDE = 2;
    private static final int HEADER_COUNT = 2;

    private LayoutInflater mInflater;
    private ArrayList<String> mItems;
    private View mHeaderView;

    public SimpleHeaderRecyclerAdapter(Context context, ArrayList<String> items, View headerView) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mHeaderView = headerView;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null) {
            return mItems.size();
        } else {
            return mItems.size() + HEADER_COUNT;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : (position == 1 ? VIEW_TYPE_HEADER_DIVIDE : VIEW_TYPE_ITEM);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderView);
        } else if (viewType == VIEW_TYPE_HEADER_DIVIDE) {
            return new HeaderDivideHolder(mInflater.inflate(R.layout.recyclerview_divide, parent, false));
        } else {
            return new ItemViewHolder(mInflater.inflate(R.layout.text_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position > 1) {
            ((ItemViewHolder) viewHolder).textView.setText(mItems.get(position - HEADER_COUNT));
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ItemViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textview_rv);
            if (textView == null) {
                Log.e("TAG", "ItemViewHolder: textview == null");
            }
            itemView.setOnClickListener(v -> Toast.makeText(v.getContext(), "on Click Item!", Toast.LENGTH_SHORT).show());
        }
    }

    static class HeaderDivideHolder extends RecyclerView.ViewHolder {

        public HeaderDivideHolder(View view) {
            super(view);
        }
    }

}
