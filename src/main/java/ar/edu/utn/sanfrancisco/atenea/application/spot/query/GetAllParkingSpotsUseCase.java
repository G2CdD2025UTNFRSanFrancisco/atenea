package ar.edu.utn.sanfrancisco.atenea.application.spot.query;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpot;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotRepository;

import java.util.Set;

public class GetAllParkingSpotsUseCase {

    private final ParkingSpotRepository parkingSpotRepository;

    public GetAllParkingSpotsUseCase(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public Set<ParkingSpot> execute() {
        return this.parkingSpotRepository.findAllParkingSpots();
    }
}
