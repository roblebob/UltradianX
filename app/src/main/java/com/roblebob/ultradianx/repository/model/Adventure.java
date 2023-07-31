package com.roblebob.ultradianx.repository.model;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.work.Data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.roblebob.ultradianx.util.UtilKt;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;


@Entity(tableName = "Adventure" , indices = @Index(value = {"title"}, unique = true))
public class Adventure {

    @PrimaryKey(autoGenerate = true )       private int     id;
    @ColumnInfo(name = "active")           private Boolean  active;
    @ColumnInfo(name = "title")             private String  title;
    @ColumnInfo(name = "tags"       )       private String  tags;
    @ColumnInfo(name = "details")           private ArrayList<String> details;
    @ColumnInfo(name = "priority")          private Double priority;
    @ColumnInfo(name = "lasttime")          private String lasttime;
    @ColumnInfo(name = "lasttimePassive")   private String lasttimePassive;
    @ColumnInfo(name = "clockify")          private String clockify;
    @ColumnInfo(name = "targetDuration")    private Integer targetDuration; // in seconds



    public Adventure(Boolean active, String title, String tags, ArrayList<String> details, String clockify, Double priority, String lasttime, String lasttimePassive, Integer targetDuration) {
        this.active = active;
        this.title = title;
        this.tags = tags;
        this.details = details;
        this.clockify = clockify;
        this.priority = priority;
        this.lasttime = lasttime;
        this.lasttimePassive = lasttimePassive;
        this.targetDuration = targetDuration;
    }


    @Ignore
    public Adventure( Adventure adventure) {
        this.id = adventure.getId();
        this.active = adventure.isActive();
        this.title = adventure.getTitle();
        this.tags = adventure.getTags();
        this.details = adventure.getDetails();
        this.clockify = adventure.getClockify();
        this.priority = adventure.getPriority();
        this.lasttime = adventure.getLasttime();
        this.lasttimePassive = adventure.getLasttimePassive();
        this.targetDuration = adventure.getTargetDuration();
    }



    @Ignore
    public Adventure( Data data) {
        this.id = data.getInt("id", -1);
        this.active = data.getBoolean("active", false);
        this.title = data.getString("title");
        this.tags = data.getString("tags");
        this.details = new Gson().fromJson( data.getString("details"), new TypeToken<ArrayList<String>>() {}.getType());
        this.clockify = data.getString("clockify");
        this.priority = data.getDouble("priority", Double.NaN);
        this.lasttime = data.getString("lasttime");
        this.lasttimePassive = data.getString("lasttimePassive");
        this.targetDuration = data.getInt("targetDuration", -1);
    }

    @Ignore
    public static Adventure newAdventure( String title) {
        return new Adventure(
                false,
                title,
                "",
                new ArrayList<>(),
                null,
                0.0,
                UtilKt.getRidOfMillis(Instant.now().toString()),
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

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
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
        return max(min(priority, 100.0), 0.0);
    }
    public void setPriority(Double priority) {
        this.priority = max(min(priority, 100.0), 0.0);
    }


    public String getLasttime() {
        return this.lasttime;
    }
    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
        if ( !this.active ) {
            this.lasttimePassive = lasttime;
        }
    }


    public String getLasttimePassive() {
        return lasttimePassive;
    }
    public void setLasttimePassive(String lasttimePassive) {
        this.lasttimePassive = lasttimePassive;
    }


    public String getClockify() {
        return clockify;
    }
    public void setClockify(String clockify) {
        this.clockify = clockify;
    }


    public Integer getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(Integer targetDuration) {
        this.targetDuration = targetDuration;
    }


    @Ignore
    public Data toData() {
        Data.Builder builder = new Data.Builder();
        builder.putInt("id", this.id);
        builder.putBoolean("active", this.active);
        builder.putString("title", this.title);
        builder.putString("tags", this.tags);
        builder.putString("details", new Gson().toJson(this.details));
        builder.putString("clockify", this.clockify);
        builder.putDouble("priority", this.priority);
        builder.putString("lasttime", this.lasttime);
        builder.putString("lasttimePassive", this.lasttimePassive);
        builder.putInt("targetDuration", this.targetDuration);
        return builder.build();
    }

    @Ignore
    public double getGrow() {
        return 100. / (24.0 * 60.0 * 60.0);  // 1 day
    }

    @Ignore
    public double getDecay() {
        return 100.0 / this.targetDuration;
    }

    @Override
    public String toString() {
        return "Adventure{" +
                "id=" + id +
                ", active=" + active +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", details=" + details +
                ", priority=" + priority +
                ", lasttime='" + lasttime + '\'' +
                ", lasttimePassive='" + lasttimePassive + '\'' +
                ", clockify='" + clockify + '\'' +
                ", targetDuration=" + targetDuration +
                '}';
    }

    @Ignore
    public void refresh() {

        Instant now = Instant.parse(UtilKt.getRidOfMillis(Instant.now().toString()));
        Instant last = Instant.parse(this.lasttime);

        Duration duration = Duration.between( last , now);

        setPriority( active ?
                priority - (duration.getSeconds() * getDecay()) :
                priority + (duration.getSeconds() * getGrow())  );

        setLasttime( now.toString());
    }



    @Ignore
    public void revert() {
        if (isActive()) {
            refresh();

            setActive(false);

            Duration duration = Duration.between(
                    Instant.parse( lasttime),
                    Instant.parse( lasttimePassive)
            );

            setPriority( priority + (duration.getSeconds() * getDecay())  +  // reverse (subtract) the active part ...
                         priority + (duration.getSeconds() * getGrow())  // ... add the passive part, instead
            );

            setLasttime(this.lasttimePassive);
        } else {
            Log.e(this.getClass().getSimpleName(), "revert() called on inactive adventure");
        }
    }


    @Ignore
    public void update( Data data) {
        if ( data.getInt("id", -1) == this.id ) {
            if ( data.hasKeyWithValueOfType("active", Boolean.class)) {
                this.active = data.getBoolean("active", false);
            }
            if ( data.hasKeyWithValueOfType("title", String.class)) {
                this.title = data.getString("title");
            }
            if ( data.hasKeyWithValueOfType("tags", String.class)) {
                this.tags = data.getString("tags");
            }
            if ( data.hasKeyWithValueOfType("details", String.class)) {
                this.details = new Gson().fromJson( data.getString("details"), new TypeToken<ArrayList<String>>() {}.getType());
            }
            if ( data.hasKeyWithValueOfType("clockify", String.class)) {
                this.clockify = data.getString("clockify");
            }
            if ( data.hasKeyWithValueOfType("priority", Double.class)) {
                this.priority = data.getDouble("priority", Double.NaN);
            }
            if ( data.hasKeyWithValueOfType("lasttime", String.class)) {
                this.lasttime = data.getString("lasttime");
            }
            if ( data.hasKeyWithValueOfType("lasttimePassive", String.class)) {
                this.lasttimePassive = data.getString("lasttimePassive");
            }
            if ( data.hasKeyWithValueOfType("targetDuration", Integer.class)) {
                this.targetDuration = data.getInt("targetDuration", -1);
            }
            refresh();
        }
    }
}
