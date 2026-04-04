package ar.edu.utn.sanfrancisco.atenea.application.spot.query;

import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpot;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.exception.ParkingSpotNotFoundException;

public class GetParkingSpotUseCase {

    private final ParkingSpotRepository parkingSpotRepository;

    public GetParkingSpotUseCase(final ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public ParkingSpot execute(final ParkingSpotId id) {
        return this.parkingSpotRepository.findParkingSpotById(id)
                .orElseThrow(() -> new ParkingSpotNotFoundException(id));
    }

}
