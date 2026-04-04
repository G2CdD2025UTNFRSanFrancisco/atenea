package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rest;

import ar.edu.utn.sanfrancisco.atenea.application.spot.query.GetAllParkingSpotsUseCase;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.rest.dto.ParkingSpotResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/spots")
public class ParkingSpotController {

    private final GetAllParkingSpotsUseCase getAllParkingSpotsUseCase;

    public ParkingSpotController(GetAllParkingSpotsUseCase getAllParkingSpotsUseCase) {
        this.getAllParkingSpotsUseCase = getAllParkingSpotsUseCase;
    }

    @GetMapping("")
    public Set<ParkingSpotResponse> getAllParkingSpotController() {
        return this.getAllParkingSpotsUseCase.execute()
                .stream()
                .map(ParkingSpotResponse::fromDomain)
                .collect(Collectors.toSet());
    }
}
