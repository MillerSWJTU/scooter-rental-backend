package scooterrent.repository;

import scooterrent.entity.Scooter;
import scooterrent.enums.ScooterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {
    List<Scooter> findByStatus(ScooterStatus status);
    List<Scooter> findByStatusAndBatteryLevelGreaterThan(ScooterStatus status, Integer batteryLevel);
    List<Scooter> findByLocation(String location);
} 