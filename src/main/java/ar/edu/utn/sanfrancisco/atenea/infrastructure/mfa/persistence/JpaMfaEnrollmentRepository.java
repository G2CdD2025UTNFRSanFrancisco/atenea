package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollment;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMfaEnrollmentRepository implements MfaEnrollmentRepository {

    private final SpringDataMfaEnrollmentRepository repository;
    private final EntityManager em;

    @Override
    public Optional<MfaEnrollment> findByAccountId(final AccountId id) {
        return repository.findById(id.value()).map(MfaEnrollmentMapper::toDomain);
    }

    @Override
    @Transactional
    public void create(final MfaEnrollment mfa) {
        em.persist(MfaEnrollmentMapper.toNewEntity(mfa));
    }

    @Override
    @Transactional
    public void update(final MfaEnrollment mfa) {
        MfaEnrollmentEntity entity = em.find(
                MfaEnrollmentEntity.class,
                mfa.getAccountId().value()
        );

        if (entity == null) {
            throw new IllegalStateException("MFA enrollment not found");
        }

        MfaEnrollmentMapper.updateEntity(entity, mfa);
    }
}

