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
    @ColumnInfo(name = "grow")              private Double  grow;
    @ColumnInfo(name = "decay")             private Double  decay;
    @ColumnInfo(name = "clockify")          private String  clockify;

    public Adventure( String title, String tags, ArrayList<String> details, Double priority, String last, Double grow, Double decay, String clockify) {
        this.title = title;
        this.tags = tags;
        this.details = details;
        this.priority = priority;
        this.last = last;
        this.grow = grow;
        this.decay = decay;
        this.clockify = clockify;
    }


    @Ignore
    public Adventure( Adventure adventure) {
        this.id = adventure.getId();
        this.title = adventure.getTitle();
        this.tags = adventure.getTags();
        this.details = adventure.getDetails();
        this.priority = adventure.getPriority();
        this.last = adventure.getLast();
        this.grow = adventure .getGrow();
        this.decay = adventure.getDecay();
        this.clockify = adventure.getClockify();
    }

    @Ignore
    public Adventure( Bundle bundle) {
        this.id = bundle.getInt("id");
        this.title = bundle.getString("title");
        this.tags = bundle.getString("tags");
        this.details = bundle.getStringArrayList("details");
        this.priority = bundle.getDouble("priority");
        this.last = bundle.getString("last");
        this.grow = bundle.getDouble("grow");
        this.decay = bundle.getDouble("decay");
        this.clockify = bundle.getString("clockify");
    }

    @Ignore
    public Adventure( Data data) {
        this.id = data.getInt("id", -1);
        this.title = data.getString("title");
        this.tags = data.getString("tags");
        this.details = new Gson().fromJson( data.getString("details"), new TypeToken<ArrayList<String>>() {}.getType());
        this.priority = data.getDouble("priority", Double.NaN);
        this.last = data.getString("last");
        this.grow = data.getDouble("grow", Double.NaN);
        this.decay = data.getDouble("decay", Double.NaN);
        this.clockify = data.getString("clockify");
    }

    @Ignore
    public static Adventure newAdventure( String title) {
        return new Adventure(
                title,
                "",
                new ArrayList<>(),
                0.0,
                UtilKt.getRidOfMillis(Instant.now().toString()),
                0.0,
                0.0,
                null
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

    public Double getGrow() {
        return grow;
    }
    public void setGrow(Double grow) {
        this.grow = grow;
    }

    public Double getDecay() {
        return decay;
    }
    public void setDecay(Double decay) {
        this.decay = decay;
    }

    public String getClockify() {
        return clockify;
    }
    public void setClockify(String clockify) {
        this.clockify = clockify;
    }




    @Ignore
    public Data toData() {
        Data.Builder builder = new Data.Builder();
        builder.putInt("id", this.id);
        builder.putString("title", this.title);
        builder.putString("tags", this.tags);
        builder.putString("details", new Gson().toJson(this.details));
        builder.putDouble("priority", this.priority);
        builder.putString("last", this.last);
        builder.putDouble("grow", this.grow);
        builder.putDouble("decay", this.decay);
        builder.putString("clockify", this.clockify);
        return builder.build();
    }


    @Ignore
    public SpannableStringBuilder titleToSpannableStringBuilder(int minFontSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append( this.title);

        int priority = min(100, this.priority.intValue());
        int maxFontSize = priority / 2;


        for (int i = 0; i < this.title.length(); i++) {
            int fontSize = maxFontSize - 3*i;
            spannableStringBuilder.setSpan( new AbsoluteSizeSpan( max( fontSize , minFontSize), true), i, i + 1, 0);
        }

        return spannableStringBuilder;
    }

    @Ignore
    public SpannableStringBuilder titleToSpannableStringBuilder() {
        return titleToSpannableStringBuilder( MIN_FONT_SIZE);
    }

    @Ignore
    public static final int MIN_FONT_SIZE = 3;
}
