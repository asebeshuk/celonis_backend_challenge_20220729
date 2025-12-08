package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;

@Entity
public class ProjectGenerationTask extends Task {

    @JsonIgnore
    public String storageLocation;
}
