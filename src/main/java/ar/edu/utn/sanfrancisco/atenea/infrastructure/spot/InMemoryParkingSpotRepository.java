package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryParkingSpotRepository implements ParkingSpotRepository {

    private final Map<ParkingSpotId, ParkingSpot> spots = new ConcurrentHashMap<>();

    public InMemoryParkingSpotRepository() {
        loadDummySpots();
    }

    private void loadDummySpots() {

        String[] filas = {"A", "B", "C", "D"};
        int[] plazas = {1, 2, 3, 4, 5};

        int counter = 0;

        for (int row = 0; row < filas.length; row++) {
            for (int col = 0; col < plazas.length; col++) {
                final String id = filas[row] + plazas[col];
                final Coordinate coordinate = new Coordinate(col, row);
                final ParkingSpot spot = new ParkingSpot(
                        new ParkingSpotId(id),
                        coordinate,
                        OccupancyStatus.OFFLINE,
                        Instant.now()
                );
                spots.put(spot.getId(), spot);
                counter++;
            }
        }
    }

    @Override
    public Optional<ParkingSpot> findParkingSpotByCoordinate(Coordinate coordinate) {
        return spots.values().stream()
                .filter(s -> s.getCoordinate().equals(coordinate))
                .findFirst();
    }

    @Override
    public Optional<ParkingSpot> findParkingSpotById(ParkingSpotId id) {
        return Optional.ofNullable(spots.get(id));
    }

    @Override
    public Set<ParkingSpot> findAllParkingSpots() {
        return new HashSet<>(spots.values());
    }

    @Override
    public boolean existsById(final ParkingSpotId id) {
        return this.spots.containsKey(id);
    }

    @Override
    public void create(ParkingSpot parkingSpot) {
        spots.put(parkingSpot.getId(), parkingSpot);
    }

    @Override
    public void update(ParkingSpot parkingSpot) {
        if (!spots.containsKey(parkingSpot.getId())) {
            throw new IllegalArgumentException("Parking spot with id " + parkingSpot.getId() + " does not exist.");
        }
        spots.put(parkingSpot.getId(), parkingSpot);
    }
}
