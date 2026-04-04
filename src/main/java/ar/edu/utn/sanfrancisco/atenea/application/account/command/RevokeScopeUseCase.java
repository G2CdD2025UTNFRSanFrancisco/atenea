package ar.edu.utn.sanfrancisco.atenea.application.account.command;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.RevokeScopeCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class RevokeScopeUseCase {

    private final AccountRepository repository;
    private final Clock clock;

    public RevokeScopeUseCase(AccountRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public void execute(final RevokeScopeCommand command) {
        Account actor = repository.findAccountById(command.actorId())
                .orElseThrow(() -> new AccountNotFoundException(command.actorId()));

        Account target = repository.findAccountById(command.targetId())
                .orElseThrow(() -> new AccountNotFoundException(command.targetId()));

        target.revokeScope(command.scope(), actor, clock);

        repository.update(target);
    }
}
