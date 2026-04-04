package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMfaEnrollmentRepository extends JpaRepository<MfaEnrollmentEntity, Long> {
}

