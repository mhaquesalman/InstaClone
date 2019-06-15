package model;

public class Story {
    private String imageurl;
    private long timeStart;
    private long timeend;
    private String storyid;
    private String userid;

    public Story() {
    }

    public Story(String imageurl, long timeStart, long timeend, String storyid, String userid) {
        this.imageurl = imageurl;
        this.timeStart = timeStart;
        this.timeend = timeend;
        this.storyid = storyid;
        this.userid = userid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
