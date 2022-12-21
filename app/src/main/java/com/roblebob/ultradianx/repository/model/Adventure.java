package com.roblebob.ultradianx.repository.model;

import android.os.Bundle;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.work.Data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


@Entity(tableName = "Adventure" , indices = @Index(value = {"title"}, unique = true))
public class Adventure {

    @PrimaryKey(autoGenerate = true )       private int     id;
    @ColumnInfo(name = "title")             private String  title;
    @ColumnInfo(name = "priority")          private Double  priority;
    @ColumnInfo(name = "last")              private String  last;
    @ColumnInfo(name = "tags"       )       private String  tags;
    @ColumnInfo(name = "details")           private ArrayList<String> details;


    public Adventure( String title, Double priority, String last, String tags, ArrayList<String> details) {
        this.title = title;
        this.priority = priority;
        this.last = last;
        this.tags = tags;
        this.details = details;
    }

    @Ignore
    public Adventure( Adventure adventure) {
        this.id = adventure.getId();
        this.title = adventure.getTitle();
        this.priority = adventure.getPriority();
        this.last = adventure.getLast();
        this.tags = adventure.getTags();
        this.details = adventure.getDetails();
    }

    @Ignore
    public Adventure( Bundle bundle) {
        this.id = bundle.getInt("id");
        this.title = bundle.getString("title");
        this.priority = bundle.getDouble("priority");
        this.last = bundle.getString("last");
        this.tags = bundle.getString("tags");
        this.details = bundle.getStringArrayList("details");
    }

    @Ignore
    public Adventure( Data data) {
        this.id = data.getInt("id", -1);
        this.title = data.getString("title");
        this.priority = data.getDouble("priority", Double.MIN_VALUE);
        this.last = data.getString("last");
        this.tags = data.getString("tags");
        this.details = new Gson().fromJson(
                data.getString("details"),
                new TypeToken<ArrayList<String>>() {}.getType()
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

    @Ignore
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("id", this.id);
        bundle.putString("title", this.title);
        bundle.putDouble("priority", this.priority);
        bundle.putString("last", this.last);
        bundle.putString("tags", this.tags);
        bundle.putStringArrayList("details", this.details);
        return bundle;
    }

    @Ignore
    public Data toData() {
        Data.Builder builder = new Data.Builder();

        builder.putInt("id", this.id);
        builder.putString("title", this.title);
        builder.putDouble("priority", this.priority);
        builder.putString("last", this.last);
        builder.putString("tags", this.tags);
        builder.putString("details", new Gson().toJson(this.details));

        return builder.build();
    }


}
