package com.yuanyang.map.test.model;

import androidx.annotation.NonNull;

public class EventLevelSetting {
    public int id;
    public int level;
    public String title;
    public String description;

    @NonNull
    @Override
    public String toString(){
        return description;
    }
}
