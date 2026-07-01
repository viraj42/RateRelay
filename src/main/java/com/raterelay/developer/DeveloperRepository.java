package com.raterelay.developer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    Optional<Developer> findByEmail(String email);

    Optional<Developer> findByVerificationToken(String token);

    boolean existsByEmail(String email);
}