package ar.edu.utn.sanfrancisco.atenea.domain.account;

import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountDetailsSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountSessionSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountSummarySnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;

import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findAccountByUsername(final Username username);
    Optional<Account> findAccountById(final AccountId id);

    Optional<AccountSessionSnapshot> findAccountSessionSnapshotByIdAndVersion(final AccountId id, final SessionVersion version);
    Optional<AccountDetailsSnapshot> findAccountDetailsSnapshotById(final AccountId id);

    PagedResult<AccountSummarySnapshot> findAllAccountSummarySnapshot(final PaginationQuery query);

    boolean existsByUsername(final Username username);

    void update(final Account account);
    void create(final Account account);

}
