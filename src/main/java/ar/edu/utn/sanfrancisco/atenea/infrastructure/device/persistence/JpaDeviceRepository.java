package ar.edu.utn.sanfrancisco.atenea.infrastructure.device.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.device.Device;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceId;
import ar.edu.utn.sanfrancisco.atenea.domain.device.DeviceRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot.DeviceDetailsSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;
import ar.edu.utn.sanfrancisco.atenea.domain.spot.ParkingSpotId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaDeviceRepository implements DeviceRepository {

    private final SpringDataDeviceRepository repository;
    private final EntityManager em;

    @Override
    public Optional<Device> findDeviceById(DeviceId id) {
        return repository
                .findByIdAndDeletedAtIsNull(id.value())
                .map(DeviceMapper::toDomain);
    }

    @Override
    public Optional<DeviceDetailsSnapshot> findDeviceDetailsById(DeviceId id) {
        return em.createQuery("""
                select new ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot.DeviceDetailsSnapshot(
                    s.id,
                    s.spotId,
                    s.createdAt,
                    s.updatedAt,
                    s.deletedAt
                )
                from DeviceEntity s
                where s.id = :id
                  and s.deletedAt is null
                """, DeviceDetailsSnapshot.class)
                .setParameter("id", id.value())
                .getResultStream()
                .findFirst();
    }

    @Override
    public PagedResult<DeviceDetailsSnapshot> findAllDeviceDetailsSnapshot(PaginationQuery query) {
        List<DeviceDetailsSnapshot> results = em.createQuery("""
                select new ar.edu.utn.sanfrancisco.atenea.domain.device.snapshot.DeviceDetailsSnapshot(
                    s.id,
                    s.spotId,
                    s.createdAt,
                    s.updatedAt,
                    s.deletedAt
                )
                from DeviceEntity s
                order by s.id
                """, DeviceDetailsSnapshot.class)
                .setFirstResult(query.offset())
                .setMaxResults(query.limit())
                .getResultList();

        Long total = em.createQuery("""
                select count(s)
                from DeviceEntity s
                """, Long.class)
                .getSingleResult();

        return PagedResult.of(
                results,
                total,
                query
        );
    }

    @Override
    public boolean existsByParkingSpotId(ParkingSpotId spotId) {
        return repository.existsBySpotIdAndDeletedAtIsNull(spotId.value());
    }

    @Override
    @Transactional
    public void create(Device device) {
        DeviceEntity entity = DeviceMapper.toEntity(device);
        em.persist(entity);
    }

    @Override
    public void update(Device device) {
        DeviceEntity entity = DeviceMapper.toEntity(device);
        em.merge(entity);
    }
}
