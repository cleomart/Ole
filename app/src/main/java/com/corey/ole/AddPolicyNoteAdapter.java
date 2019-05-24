package com.corey.ole;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class AddPolicyNoteAdapter extends RecyclerView.Adapter<AddPolicyNoteAdapter.AddPolicyNoteView> {

    private ArrayList<String> mData;
    private OnItemClickListener mListener;
    private int currentPosition;
    public AddPolicyNoteAdapter(ArrayList<String> data) {
        mData = data;
    }
    @Override
    public void onBindViewHolder(AddPolicyNoteView holder, int position) {
        currentPosition = position;
        String string = mData.get(position);
        holder.bindView(string);
    }

    @Override
    public AddPolicyNoteView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.add_policy_note_cell, parent, false);
        return new AddPolicyNoteView(v, mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AddPolicyNoteAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    class AddPolicyNoteView extends RecyclerView.ViewHolder {
        EditText mTextView;
        ImageView mDeleteButton;


        public AddPolicyNoteView(View itemView, final AddPolicyNoteAdapter.OnItemClickListener listener) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.add_policy_note_text);
            mDeleteButton = itemView.findViewById(R.id.add_policy_note_delete_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }

                }
            });
        }

        public void bindView(String string) {
            mTextView.setText(string);
        }
    }

}