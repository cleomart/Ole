package com.corey.ole;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.AnnouncementView> {

    private ArrayList<Message> mData;

    public ConversationAdapter(ArrayList<Message> data) {
        mData = data;
    }
    @Override
    public void onBindViewHolder(AnnouncementView holder, int position) {
        Message message = mData.get(position);
        holder.bindView(message);
    }

    @Override
    public AnnouncementView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.conversation_cell, parent, false);
        return new AnnouncementView(v);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AnnouncementView extends RecyclerView.ViewHolder {

        private TextView mSenderView1;
        private TextView mDateView1;
        private TextView mSenderView2;
        private TextView mDateView2;
        private TextView mMessageView;

        public AnnouncementView(View itemView) {
            super(itemView);
            mSenderView1 = itemView.findViewById(R.id.sender1);
            mDateView1 = itemView.findViewById(R.id.date1);
            mSenderView2 = itemView.findViewById(R.id.sender2);
            mDateView2 = itemView.findViewById(R.id.date2);
            mMessageView = itemView.findViewById(R.id.message);
        }

        public void bindView(Message message) {
            mMessageView.setText(message.getMessage());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = db.getReference("users");
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String senderUid = message.getSenderUID();
            if (myUid.equals(senderUid)) {
                mSenderView2.setText("Me");
                mDateView2.setText(format.format(message.getDate()));
                mSenderView2.setVisibility(View.VISIBLE);
                mDateView2.setVisibility(View.VISIBLE);
                mSenderView1.setVisibility(View.GONE);
                mDateView1.setVisibility(View.GONE);
            } else {
                DatabaseReference user = usersRef.child(senderUid);
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot data) {
                        mSenderView1.setText(data.child("firstName").getValue(String.class) + " " +
                                data.child("lastName").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("message", String.valueOf(databaseError));
                    }
                });
                mDateView1.setText(format.format(message.getDate()));
                mSenderView1.setVisibility(View.VISIBLE);
                mDateView1.setVisibility(View.VISIBLE);
                mSenderView2.setVisibility(View.GONE);
                mDateView2.setVisibility(View.GONE);
            }
        }
    }

}