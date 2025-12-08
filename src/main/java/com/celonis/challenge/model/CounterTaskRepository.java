package com.celonis.challenge.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterTaskRepository extends JpaRepository<CounterTask, String> {
    Iterable<CounterTask> findByStatus(Status status);
}

