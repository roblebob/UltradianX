package com.roblebob.ultradianx.repository.model;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.util.Log;

import androidx.annotation.NonNull;
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
    @ColumnInfo(name = "tag"       )       private String tag;
    @ColumnInfo(name = "details")           private ArrayList<String> details;
    @ColumnInfo(name = "priority")          private Double priority;
    @ColumnInfo(name = "lastTime")          private String lastTime;
    @ColumnInfo(name = "lastTimePassive")   private String lastTimePassive;
    @ColumnInfo(name = "clockify")          private String clockify;
    @ColumnInfo(name = "target")          private Integer target; // in minutes



    public Adventure(Boolean active, String title, String tag, ArrayList<String> details,
                     String clockify, Double priority, String lastTime, String lastTimePassive,
                     Integer target) {
        this.active = active;
        this.title = title;
        this.tag = tag;
        this.details = details;
        this.clockify = clockify;
        this.priority = priority;
        this.lastTime = lastTime;
        this.lastTimePassive = lastTimePassive;
        this.target = target;
    }


    @Ignore
    public Adventure( Adventure adventure) {
        this.id = adventure.getId();
        this.active = adventure.isActive();
        this.title = adventure.getTitle();
        this.tag = adventure.getTag();
        this.details = adventure.getDetails();
        this.clockify = adventure.getClockify();
        this.priority = adventure.getPriority();
        this.lastTime = adventure.getLastTime();
        this.lastTimePassive = adventure.getLastTimePassive();
        this.target = adventure.getTarget();
    }



    @Ignore
    public Adventure( Data data) {
        this.id = data.getInt("id", -1);
        this.active = data.getBoolean("active", false);
        this.title = data.getString("title");
        this.tag = data.getString("tags");
        this.details = new Gson().fromJson( data.getString("details"), new TypeToken<ArrayList<String>>() {}.getType());
        this.clockify = data.getString("clockify");
        this.priority = data.getDouble("priority", Double.NaN);
        this.lastTime = data.getString("lastTime");
        this.lastTimePassive = data.getString("lastTimePassive");
        this.target = data.getInt("target", -1);
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

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
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


    public String getLastTime() {
        return this.lastTime;
    }
    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
        if ( !this.active ) {
            this.lastTimePassive = lastTime;
        }
    }


    public String getLastTimePassive() {
        return lastTimePassive;
    }
    public void setLastTimePassive(String lastTimePassive) {
        this.lastTimePassive = lastTimePassive;
    }


    public String getClockify() {
        return clockify;
    }
    public void setClockify(String clockify) {
        this.clockify = clockify;
    }


    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }


    @Ignore
    public Data toData() {
        Data.Builder builder = new Data.Builder();
        builder.putInt("id", this.id);
        builder.putBoolean("active", this.active);
        builder.putString("title", this.title);
        builder.putString("tags", this.tag);
        builder.putString("details", new Gson().toJson(this.details));
        builder.putString("clockify", this.clockify);
        builder.putDouble("priority", this.priority);
        builder.putString("lastTime", this.lastTime);
        builder.putString("lastTimePassive", this.lastTimePassive);
        builder.putInt("target", this.target);  // !!!!!!  in MINUTES
        return builder.build();
    }

    @Ignore
    public double getGrow() {
        return 100. / (24.0 * 60.0 * 60.0);  // 1 day
    }

    @Ignore
    public double getDecay() {
        return 100.0 / (this.target * 60.0);  // since we need it in secs
    }

    @NonNull
    @Override
    public String toString() {
        return "Adventure{" +
                "id=" + id +
                ", active=" + active +
                ", title='" + title + '\'' +
                ", tags='" + tag + '\'' +
                ", details=" + details +
                ", priority=" + priority +
                ", lastTime='" + lastTime + '\'' +
                ", lastTimePassive='" + lastTimePassive + '\'' +
                ", clockify='" + clockify + '\'' +
                ", target=" + target +
                '}';
    }

    @Ignore
    public void refresh() {

        Instant now = Instant.parse(UtilKt.getRidOfMillis(Instant.now().toString()));
        Instant last = Instant.parse(this.lastTime);

        Duration duration = Duration.between( last , now);

        setPriority( active ?
                priority - (duration.getSeconds() * getDecay()):
                priority + (duration.getSeconds() * getGrow()));

        setLastTime( now.toString());
    }



    @Ignore
    public void abort() {
        if (isActive()) {
            refresh();

            setActive(false);

            Duration duration = Duration.between(
                    Instant.parse(lastTime),
                    Instant.parse(lastTimePassive)
            );

            setPriority( priority
                    + (duration.getSeconds() * getDecay()) // reverse the active part ...
                    + (duration.getSeconds() * getGrow())  // ... add the passive part, instead
            );

        } else {
            Log.e(this.getClass().getSimpleName(), "abort() called on inactive adventure");
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
                this.tag = data.getString("tags");
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
            if ( data.hasKeyWithValueOfType("lastTime", String.class)) {
                this.lastTime = data.getString("lastTime");
            }
            if ( data.hasKeyWithValueOfType("lastTimePassive", String.class)) {
                this.lastTimePassive = data.getString("lastTimePassive");
            }
            if ( data.hasKeyWithValueOfType("target", Integer.class)) {
                this.target = data.getInt("target", -1);
            }
            refresh();
        }
    }
}
