package com.app.fitsmile.fitsreminder;

import java.util.Date;

public class SimpleEvent {
    public Date date;
    public String title;
    public int color;
    public Integer progress;
    public String currentAligner;

    public SimpleEvent(Date time, String goal_reached, int color, int i,String aligner) {
        date=time;
        title=goal_reached;
        this.color=color;
        progress=i;
        currentAligner=aligner;
    }
}
