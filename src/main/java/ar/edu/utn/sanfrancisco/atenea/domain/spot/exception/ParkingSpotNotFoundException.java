package ar.edu.utn.sanfrancisco.atenea.domain.spot.exception;

import ar.edu.utn.sanfrancisco.atenea.domain.shared.BusinessException;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;

import java.util.Map;

public class ParkingSpotNotFoundException extends BusinessException {
    private final ParkingSpotId id;

    public ParkingSpotNotFoundException(final ParkingSpotId id) {
        super("This parking spot does not exist.", "spot.not_found", 404);
        this.id = id;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of("spot_id", id);
    }
}
