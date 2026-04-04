package ar.edu.utn.sanfrancisco.atenea.domain.spot;

import java.util.Optional;
import java.util.Set;

public interface ParkingSpotRepository {
    Optional<ParkingSpot> findParkingSpotByCoordinate(final Coordinate coordinate);
    Optional<ParkingSpot> findParkingSpotById(final ParkingSpotId id);

    Set<ParkingSpot> findAllParkingSpots();

    boolean existsById(final ParkingSpotId id);
    void create(final ParkingSpot parkingSpot);
    void update(final ParkingSpot parkingSpot);
}
