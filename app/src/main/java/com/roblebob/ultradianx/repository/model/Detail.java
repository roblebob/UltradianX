package com.roblebob.ultradianx.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "Detail", indices = @Index(value = {"adventure", "subject", "content"}, unique = true))
public class Detail {

    @PrimaryKey(autoGenerate = true )       private int     id;
    @ColumnInfo(name = "adventure")         private String  adventure;   // parent adventure, which the detail is a part of
    @ColumnInfo(name = "subject")           private String  subject;
    @ColumnInfo(name = "content")           private String  content;

    public Detail( String adventure, String subject, String content) {
        this.adventure = adventure;
        this.subject = subject;
        this.content = content;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAdventure() {
        return adventure;
    }
    public void setAdventure(String adventure) {
        this.adventure = adventure;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
