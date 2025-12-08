package com.celonis.challenge.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.EnumType.STRING;


@JsonDeserialize(using = TaskDeserializer.class)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Task {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public String id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public Date creationDate;

    @Column(nullable = false)
    @Enumerated(STRING)
    public Status status;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
        status = Status.CREATED;
        id = null;
    }
}
