package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CounterTask extends Task {

    @Column(nullable = false)
    @JsonProperty("x")  // was requested to be named x
    public Integer start;

    @Column(nullable = false)
    @JsonProperty("y") // was requested to be named y
    public Integer end;

    @Column(nullable = false)
    public Integer pointer;

    protected void onCreate() {
        super.onCreate();
        pointer = start;
    }

    public float getProgress() {
        if (status != Status.IN_PROGRESS) {
            return 0;
        }
        return (float) (pointer - start) / (end - start);
    }
}
