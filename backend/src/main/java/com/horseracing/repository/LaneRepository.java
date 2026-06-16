package com.horseracing.repository;

import com.horseracing.entity.Lane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaneRepository extends JpaRepository<Lane, Long> {
    List<Lane> findByRaceId(Long raceId);
    Optional<Lane> findByRaceIdAndLaneNumber(Long raceId, Integer laneNumber);
    Optional<Lane> findByRegistrationId(Long registrationId);
}
