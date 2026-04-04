package ar.edu.utn.sanfrancisco.atenea.application.account.query;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.snapshot.AccountDetailsSnapshot;

public class GetAccountDetailsUseCase {

    private final AccountRepository accountRepository;

    public GetAccountDetailsUseCase(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDetailsSnapshot execute(final AccountId id) {
        return accountRepository.findAccountDetailsSnapshotById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

}
