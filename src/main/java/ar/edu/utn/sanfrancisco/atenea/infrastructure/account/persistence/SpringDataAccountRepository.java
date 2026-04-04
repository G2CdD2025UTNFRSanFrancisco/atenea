package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByUsernameAndDeletedAtIsNull(String username);
    Optional<AccountEntity> findByIdAndDeletedAtIsNull(Long id);
    boolean existsByUsernameAndDeletedAtIsNull(String username);
}
