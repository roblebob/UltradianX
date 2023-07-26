package com.roblebob.ultradianx.repository.model;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.work.Data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.roblebob.ultradianx.util.UtilKt;

import java.time.Instant;
import java.util.ArrayList;


@Entity(tableName = "Adventure" , indices = @Index(value = {"title"}, unique = true))
public class Adventure {

    @PrimaryKey(autoGenerate = true )       private int     id;
    @ColumnInfo(name = "title")             private String  title;
    @ColumnInfo(name = "tags"       )       private String  tags;
    @ColumnInfo(name = "details")           private ArrayList<String> details;
    @ColumnInfo(name = "priority")          private Double  priority;
    @ColumnInfo(name = "last")              private String  last;

    @ColumnInfo(name = "clockify")          private String  clockify;

    @ColumnInfo(name = "duration")          private Integer duration; // in seconds

    public Adventure( String title, String tags, ArrayList<String> details, String clockify, Double priority, String last, Integer duration) {
        this.title = title;
        this.tags = tags;
        this.details = details;
        this.clockify = clockify;
        this.priority = priority;
        this.last = last;
        this.duration = duration;
    }


    @Ignore
    public Adventure( Adventure adventure) {
        this.id = adventure.getId();
        this.title = adventure.getTitle();
        this.tags = adventure.getTags();
        this.details = adventure.getDetails();
        this.clockify = adventure.getClockify();
        this.priority = adventure.getPriority();
        this.last = adventure.getLast();
        this.duration = adventure.getDuration();
    }



    @Ignore
    public Adventure( Data data) {
        this.id = data.getInt("id", -1);
        this.title = data.getString("title");
        this.tags = data.getString("tags");
        this.details = new Gson().fromJson( data.getString("details"), new TypeToken<ArrayList<String>>() {}.getType());
        this.clockify = data.getString("clockify");
        this.priority = data.getDouble("priority", Double.NaN);
        this.last = data.getString("last");
        this.duration = data.getInt("duration", -1);
    }

    @Ignore
    public static Adventure newAdventure( String title) {
        return new Adventure(
                title,
                "",
                new ArrayList<>(),
                null,
                0.0,
                UtilKt.getRidOfMillis(Instant.now().toString()),
                0
        );
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }

    public ArrayList<String> getDetails() {
        return details;
    }
    public void setDetails(ArrayList<String> details) {
        this.details = details;
    }

    public Double getPriority() {
        return priority;
    }
    public void setPriority(Double priority) {
        this.priority = priority;
    }

    public String getLast() {
        return last;
    }
    public void setLast(String last) {
        this.last = last;
    }

    public String getClockify() {
        return clockify;
    }
    public void setClockify(String clockify) {
        this.clockify = clockify;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    @Ignore
    public Data toData() {
        Data.Builder builder = new Data.Builder();
        builder.putInt("id", this.id);
        builder.putString("title", this.title);
        builder.putString("tags", this.tags);
        builder.putString("details", new Gson().toJson(this.details));
        builder.putString("clockify", this.clockify);
        builder.putDouble("priority", this.priority);
        builder.putString("last", this.last);
        builder.putInt("duration", this.duration);
        return builder.build();
    }

    @Ignore
    public double getGrow() {
        return 100. / (24.0 * 60.0 * 60.0);  // 1 day
    }

    @Ignore
    public double getDecay() {
        return 100.0 / this.duration ;
    }

}
