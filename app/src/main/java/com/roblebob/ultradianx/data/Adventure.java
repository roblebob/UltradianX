package com.roblebob.ultradianx.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "Adventure", indices = @Index(value = {"description"}, unique = true))
public class Adventure {

    @PrimaryKey(autoGenerate = true)        private int     id;
    @ColumnInfo(name = "description")       private String  description;
    @ColumnInfo(name = "category")          private String  category;
    @ColumnInfo(name = "immediate")         private boolean immediate;

    public Adventure(String description, String category, boolean immediate) {
        this.description = description;
        this.category = category;
        this.immediate = immediate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }
}
