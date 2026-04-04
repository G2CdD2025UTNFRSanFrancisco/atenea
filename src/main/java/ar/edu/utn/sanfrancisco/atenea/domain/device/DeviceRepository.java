package ar.edu.utn.sanfrancisco.atenea.domain.device;

import ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot.DeviceDetailsSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

import java.util.Optional;

public interface DeviceRepository {

    Optional<Device> findDeviceById(final DeviceId id);
    Optional<DeviceDetailsSnapshot> findDeviceDetailsById(final DeviceId id);

    PagedResult<DeviceDetailsSnapshot> findAllDeviceDetailsSnapshot(final PaginationQuery query);

    boolean existsByParkingSpotId(final ParkingSpotId spotId);
    void create(Device device);
    void update(Device device);
}
