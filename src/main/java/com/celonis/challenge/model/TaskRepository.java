package com.celonis.challenge.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    Collection<Task> findByStatus(Status status);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.status = :status AND t.creationDate < :cutoff")
    void deleteByStatusAndOlderThan(@Param("status") Status status, @Param("cutoff") Date cutoff);

}
