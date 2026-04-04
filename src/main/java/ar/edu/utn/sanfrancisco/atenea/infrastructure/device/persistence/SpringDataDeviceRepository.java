package ar.edu.utn.sanfrancisco.atenea.infrastructure.device.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataDeviceRepository extends JpaRepository<DeviceEntity, String> {

    Optional<DeviceEntity> findByIdAndDeletedAtIsNull(String id);

    boolean existsBySpotIdAndDeletedAtIsNull(String spotId);
}
