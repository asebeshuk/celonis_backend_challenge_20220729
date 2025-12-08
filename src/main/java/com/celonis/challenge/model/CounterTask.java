package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;

@Entity
public class CounterTask extends Task {

    @Min(0)
    @Column(nullable = false)
    @JsonProperty("x")  // was requested to be named x
    public int start;

    @Column(nullable = false)
    @JsonProperty("y") // was requested to be named y
    public int end;

    @Column(nullable = false)
    public int pointer;

    protected void onCreate() {
        super.onCreate();
        pointer = start;
    }

    @Override
    public String toString() {
        return "CounterTask{" +
                "start=" + start +
                ", end=" + end +
                ", pointer=" + pointer +
                "} " + super.toString();
    }
}
