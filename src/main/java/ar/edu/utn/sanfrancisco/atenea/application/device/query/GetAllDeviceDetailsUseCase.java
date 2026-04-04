package ar.edu.utn.sanfrancisco.atenea.application.device.query;

import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot.DeviceDetailsSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;

public class GetAllDeviceDetailsUseCase {

    private final DeviceRepository deviceRepository;

    public GetAllDeviceDetailsUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public PagedResult<DeviceDetailsSnapshot> execute(PaginationQuery query) {
        return this.deviceRepository.findAllDeviceDetailsSnapshot(query);
    }

}
