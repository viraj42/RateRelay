package com.raterelay.client;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    //To find user using API Key
    Optional<Client> findByApiKey(String apiKey);

    //To check whether user exist with this email
    boolean existsByEmail(String email);

    // Locks the row at DB level during rate limit check — prevents
    // concurrent requests from racing on the same token bucket

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Client c WHERE c.id = :id")
    Optional<Client> findByIdWithLock(@Param("id") Long id);
}
