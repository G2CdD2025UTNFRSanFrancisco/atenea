package ar.edu.utn.sanfrancisco.atenea.application.account.query;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountDetailsSnapshot;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PagedResult;
import ar.edu.utn.sanfrancisco.atenea.domain.shared.PaginationQuery;

public class GetAllAccountDetailsUseCase {

    private final AccountRepository accountRepository;

    public GetAllAccountDetailsUseCase(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public PagedResult<AccountDetailsSnapshot> execute(final PaginationQuery query) {
        return accountRepository.findAllAccountDetailsSnapshot(query);
    }

}
