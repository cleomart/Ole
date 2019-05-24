package com.corey.ole;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public abstract class MessagesActivity extends NavDrawerActivity {

    protected ArrayList<Message> mMessages;
    protected RecyclerView mMessagesRecycler;

    protected void setMessages() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference messRef = db.getReference("messages");

        Query query = messRef.orderByChild("participants");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessages.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ArrayList participants = (ArrayList<String>) child.child("participants").getValue();
                    String convId = child.getKey();
                    if (participants.contains(mUid)) {
                        Iterable<DataSnapshot> allMessages = child.child("messages").getChildren();
                        Date d = new Date();
                        d.setTime(0);
                        Message newMessage = new Message("", d, "", true, convId);
                        for (DataSnapshot mess : allMessages) {
                            if (mess.child("Date").getValue(Date.class) == null) {
                                newMessage = new Message(mess.child("Message").getValue(String.class),
                                        new Date(),
                                        mess.child("Sender").getValue(String.class),
                                        mess.child("Read").getValue(Boolean.class),
                                        convId);
                            } else if (mess.child("Date").getValue(Date.class).getTime() > newMessage.getDate().getTime()) {
                                newMessage = new Message(mess.child("Message").getValue(String.class),
                                        mess.child("Date").getValue(Date.class),
                                        mess.child("Sender").getValue(String.class),
                                        mess.child("Read").getValue(Boolean.class),
                                        convId);
                            }
                        }
                        mMessages.add(newMessage);
                    }
                }

                setAdapterAndUpdateData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    protected void setAdapterAndUpdateData() {
        // Save state
        Parcelable recyclerViewState;
        recyclerViewState = mMessagesRecycler.getLayoutManager().onSaveInstanceState();

        // create a new adapter with the updated mComments array
        // this will "refresh" our recycler view
        Collections.sort(mMessages, new Comparator<Message>() {
            @Override
            public int compare(Message comment, Message t1) {
                return -Long.compare(comment.getDate().getTime(), t1.getDate().getTime());
            }
        });
        MessageAdapter adapter = new MessageAdapter(mMessages, this);
        mMessagesRecycler.setAdapter(adapter);

        // Restore state
        mMessagesRecycler.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    public abstract void getConversation(String convId);
}
