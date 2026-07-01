package com.raterelay.application;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByApiKey(String apiKey);

    // NEW — developer's own applications list
    List<Application> findByDeveloperId(Long developerId);

    // NEW — used when verifying an application belongs to the calling developer
    Optional<Application> findByIdAndDeveloperId(Long id, Long developerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Application a WHERE a.id = :id")
    Optional<Application> findByIdWithLock(@Param("id") Long id);
}