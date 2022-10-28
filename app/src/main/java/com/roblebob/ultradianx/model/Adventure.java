package com.roblebob.ultradianx.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "Adventure" /*, indices = @Index(value = {"description"}, unique = true)*/)
public class Adventure {

    @PrimaryKey(autoGenerate = false )       private int     id;
    @ColumnInfo(name = "title")             private String  title;
    @ColumnInfo(name = "tags"       )       private String  tags;
    @ColumnInfo(name = "descriptions")      private String  descriptions;
    @ColumnInfo(name = "links")             private String  links;



    public Adventure(int id, String title, String tags, String descriptions, String links ) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.descriptions = descriptions;
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

    public String getDescriptions() {
        return descriptions;
    }
    public void setDescriptions(String description) {
        this.descriptions = description;
    }

    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLinks() {
        return links;
    }
    public void setLinks(String links) {
        this.links = links;
    }
}
