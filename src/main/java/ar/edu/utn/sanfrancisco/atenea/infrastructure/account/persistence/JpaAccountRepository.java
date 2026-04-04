package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.persistence;

import ar.edu.utn.sanfrancisco.atenea.domain.account.*;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountDetailsSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountSessionSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccountRepository repository;
    private final EntityManager em;

    @Override
    public Optional<Account> findAccountByUsername(final Username username) {
        return repository.findByUsernameAndDeletedAtIsNull(username.value())
                .map(AccountMapper::toDomain);
    }

    @Override
    public Optional<Account> findAccountById(final AccountId id) {
        return repository.findByIdAndDeletedAtIsNull(id.value())
                .map(AccountMapper::toDomain);
    }

    @Override
    public Optional<AccountSessionSnapshot> findAccountSessionSnapshotByIdAndVersion(
            final AccountId id,
            final SessionVersion version
    ) {
        return em.createQuery("""
                select new ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountSessionSnapshot(
                    a.id,
                    a.version,
                    a.scopes,
                    a.mfaRequired
                )
                from AccountEntity a
                where a.id = :id
                  and a.version = :version
                  and a.deletedAt is null
                """, AccountSessionSnapshot.class)
                .setParameter("id", id.value())
                .setParameter("version", version.value())
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<AccountDetailsSnapshot> findAccountDetailsSnapshotById(AccountId id) {
        return em.createQuery("""
                select new ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountDetailsSnapshot(
                    a.id,
                    a.username,
                    a.scopes,
                    a.mfaRequired,
                    a.createdAt,
                    a.updatedAt,
                    a.deletedAt
                )
                from AccountEntity a
                where a.id = :id
                """, AccountDetailsSnapshot.class)
                .setParameter("id", id.value())
                .getResultStream()
                .findFirst();
    }

    @Override
    public PagedResult<AccountDetailsSnapshot> findAllAccountDetailsSnapshot(PaginationQuery query) {
        List<AccountDetailsSnapshot> results = em.createQuery("""
                select new ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountDetailsSnapshot(
                    a.id,
                    a.username,
                    a.scopes,
                    a.mfaRequired,
                    a.createdAt,
                    a.updatedAt,
                    a.deletedAt
                )
                from AccountEntity a
                order by a.id
                """, AccountDetailsSnapshot.class)
                .setFirstResult(query.offset())
                .setMaxResults(query.limit())
                .getResultList();

        Long total = em.createQuery("""
                select count(a)
                from AccountEntity a
                """, Long.class)
                .getSingleResult();

        return PagedResult.of(
                results,
                total,
                query
        );
    }
    @Override
    public boolean existsByUsername(Username username) {
        return repository.existsByUsernameAndDeletedAtIsNull(username.value());
    }

    @Override
    @Transactional
    public void create(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        em.persist(entity);
    }

    @Override
    @Transactional
    public void update(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        em.merge(entity);
    }

}
