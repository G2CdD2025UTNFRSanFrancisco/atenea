package ar.edu.utn.sanfrancisco.atenea.domain.spot.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

import java.util.Map;

public class ParkingSpotAlreadyAssignedException extends BusinessException {

    private final ParkingSpotId spotId;

    public ParkingSpotAlreadyAssignedException(final ParkingSpotId spotId) {
        super("This parking spot is already assigned.", "parking_spot.already_assigned", 409);
        this.spotId = spotId;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("spot_id", spotId);
    }
}
