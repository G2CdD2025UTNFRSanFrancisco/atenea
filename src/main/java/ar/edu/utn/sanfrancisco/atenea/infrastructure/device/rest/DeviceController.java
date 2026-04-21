package ar.edu.utn.sanfrancisco.atenea.infrastructure.device.rest;

import ar.edu.utn.sanfrancisco.atenea.application.device.command.CreateDeviceUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.device.command.DeleteDeviceUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.device.command.dto.CreateDeviceCommand;
import ar.edu.utn.sanfrancisco.atenea.application.device.command.dto.CreateDeviceResult;
import ar.edu.utn.sanfrancisco.atenea.application.device.command.dto.DeleteDeviceCommand;
import ar.edu.utn.sanfrancisco.atenea.application.device.query.GetAllDeviceDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.device.query.GetDeviceDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.device.rest.dto.CreateDeviceRequest;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.device.rest.dto.CreateDeviceResponse;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.device.rest.dto.DeleteDeviceRequest;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.device.rest.dto.DeviceDetailsResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final GetAllDeviceDetailsUseCase getAllDeviceDetailsUseCase;
    private final GetDeviceDetailsUseCase getDeviceDetailsUseCase;
    private final CreateDeviceUseCase createDeviceUseCase;
    private final DeleteDeviceUseCase deleteDeviceUseCase;

    public DeviceController(
            final GetAllDeviceDetailsUseCase getAllDeviceDetailsUseCase,
            final GetDeviceDetailsUseCase getDeviceDetailsUseCase,
            final CreateDeviceUseCase createDeviceUseCase,
            final DeleteDeviceUseCase deleteDeviceUseCase
    ) {
        this.getAllDeviceDetailsUseCase = getAllDeviceDetailsUseCase;
        this.getDeviceDetailsUseCase = getDeviceDetailsUseCase;
        this.createDeviceUseCase = createDeviceUseCase;
        this.deleteDeviceUseCase = deleteDeviceUseCase;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_READ_SENSORS') or hasAuthority('SCOPE_ADMIN')")
    public DeviceDetailsResponse getDevice(@PathVariable String id) {
        return DeviceDetailsResponse.fromSnapshot(this.getDeviceDetailsUseCase.execute(new DeviceId(id)));
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('SCOPE_READ_SENSORS') or hasAuthority('SCOPE_ADMIN')")
    public PagedResult<DeviceDetailsResponse> getAllDevices(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var query = PaginationQuery.of(page, size);
        final var result = this.getAllDeviceDetailsUseCase.execute(query);
        return PagedResult.of(
                result.items().stream()
                        .map(DeviceDetailsResponse::fromSnapshot)
                        .collect(Collectors.toList()),
                result.totalItems(),
                query
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_MANAGE_SENSORS') or hasAuthority('SCOPE_ADMIN')")
    public CreateDeviceResponse createDevice(@RequestBody final CreateDeviceRequest request) {
        return CreateDeviceResponse.fromResult(this.createDeviceUseCase.execute(new CreateDeviceCommand(
                new ParkingSpotId(request.spot())
        )));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_MANAGE_SENSORS') or hasAuthority('SCOPE_ADMIN')")
    public void deleteDevice(@RequestBody final DeleteDeviceRequest request) {
        this.deleteDeviceUseCase.execute(new DeleteDeviceCommand(
                new DeviceId(request.id())
        ));
    }

}
