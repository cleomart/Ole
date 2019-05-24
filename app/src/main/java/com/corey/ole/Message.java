package com.corey.ole;

import java.util.Date;

public class Message {

    private String mMessage;
    private Date mDate;
    private String mSenderUID;
    private Boolean mRead;
    private String mConvId;

    Message(String message, Date date, String senderUID, Boolean read, String convId) {
        mMessage = message;
        if (date == null) {
            date = new Date();
        }
        mDate = date;
        mSenderUID = senderUID;
        mRead = read;
        mConvId = convId;
    }

    public Date getDate() {
        return mDate;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getSenderUID() {
        return mSenderUID;
    }

    public Boolean getRead() {
        return mRead;
    }

    public String getConvId() {
        return mConvId;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setSenderUID(String senderUID) {
        mSenderUID = senderUID;
    }

    public void setRead(Boolean read) {
        mRead = read;
    }

    public void setConvId(String convId) {
        mConvId = convId;
    }
}
