package com.example.instachat.RecyclerViewPojo;

public class ItemDesign {
    private String message, timeStamp, uId;

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ItemDesign{" +
                "message='" + message + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}

