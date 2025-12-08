package com.celonis.challenge.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public Date creationDate;

    @Column(nullable = false)
    @Enumerated(STRING)
    public Status status;

    @Column(nullable = false)
    public Integer progressPercentage;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
        status = Status.CREATED;
        progressPercentage = 0;
        id = null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", status=" + status +
                ", progressPercentage=" + progressPercentage +
                '}';
    }
}
