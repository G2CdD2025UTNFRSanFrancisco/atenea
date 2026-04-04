package ar.edu.utn.sanfrancisco.atenea.application.account.command;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.DeleteAccountCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class DeleteAccountUseCase {

    private final AccountRepository repository;
    private final Clock clock;

    public DeleteAccountUseCase(AccountRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public void execute(final DeleteAccountCommand command) {
        Account actor = repository.findAccountById(command.actorId())
                .orElseThrow(() -> new AccountNotFoundException(command.actorId()));

        Account target = repository.findAccountById(command.targetId())
                .orElseThrow(() -> new AccountNotFoundException(command.actorId()));

        target.delete(actor, clock);
        repository.update(target);
    }
}
