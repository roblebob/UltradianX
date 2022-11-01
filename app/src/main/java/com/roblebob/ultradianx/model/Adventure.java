package com.roblebob.ultradianx.model;

import android.os.Bundle;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "Adventure" , indices = @Index(value = {"title"}, unique = true))
public class Adventure {

    @PrimaryKey(autoGenerate = true )       private int     id;
    @ColumnInfo(name = "title")             private String  title;
    @ColumnInfo(name = "tags"       )       private String  tags;
    @ColumnInfo(name = "details")           private String  details;
    @ColumnInfo(name = "links")             private String  links;



    public Adventure( String title, String tags, String details, String links ) {
        this.title = title;
        this.tags = tags;
        this.details = details;
        this.links = links;
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

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public String getLinks() {
        return links;
    }
    public void setLinks(String links) {
        this.links = links;
    }


    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("id", this.id);
        bundle.putString("title", this.title);
        bundle.putString("tags", this.tags);
        bundle.putString("details", this.details);
        bundle.putString("links", this.links);
        return bundle;
    }
}
