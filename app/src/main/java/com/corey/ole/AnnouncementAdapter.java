package com.corey.ole;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementView> {

    private ArrayList<String> mData;
    public AnnouncementAdapter(ArrayList<String> data) {
        mData = data;
    }
    @Override
    public void onBindViewHolder(AnnouncementView holder, int position) {
        String string = mData.get(position);
        holder.bindView(string);
    }

    @Override
    public AnnouncementView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.annoucement_cell, parent, false);
        return new AnnouncementView(v);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AnnouncementView extends RecyclerView.ViewHolder {
        TextView mTextView;

        public AnnouncementView(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.announce);
        }

        public void bindView(String string) {
            mTextView.setText(string);
        }
    }

}