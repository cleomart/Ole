package com.corey.ole;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.AnnouncementView> {

    private ArrayList<Message> mData;
    private Context mContext;

    public MessageAdapter(ArrayList<Message> data, Context context) {
        mData = data;
        mContext = context;
    }
    @Override
    public void onBindViewHolder(AnnouncementView holder, int position) {
        Message message = mData.get(position);
        holder.bindView(message);
    }

    @Override
    public AnnouncementView onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.message_cell, parent, false);
        return new AnnouncementView(v, mContext);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AnnouncementView extends RecyclerView.ViewHolder {

        private View mItemView;
        private Context mContext;
        private TextView mSenderView;
        private TextView mDateView;
        private TextView mMessageView;
        private ImageView mUnreadView;

        public AnnouncementView(View itemView, Context context) {
            super(itemView);
            mItemView = itemView;
            mContext = context;
            mSenderView = itemView.findViewById(R.id.sender);
            mDateView = itemView.findViewById(R.id.date);
            mMessageView = itemView.findViewById(R.id.message);
            mUnreadView = itemView.findViewById(R.id.unread);
        }

        public void bindView(final Message message) {
            mMessageView.setText(message.getMessage());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
            mDateView.setText(format.format(message.getDate()));
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = db.getReference("users");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String mUid = auth.getCurrentUser().getUid();
            String uid = message.getSenderUID();
            DatabaseReference user = usersRef.child(uid);
            if (mUid.equals(uid)) {
                mSenderView.setText("Me");
            } else {
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot data) {
                        mSenderView.setText(data.child("firstName").getValue(String.class) + " " +
                                data.child("lastName").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("message", String.valueOf(databaseError));
                    }
                });
            }

            if (message.getRead() || mUid.equals(uid)) {
                mUnreadView.setVisibility(View.GONE);
            } else {
                mUnreadView.setVisibility(View.VISIBLE);
            }

            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MessagesActivity) mContext).getConversation(message.getConvId());
                }
            });
        }
    }

}