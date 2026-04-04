package ar.edu.utn.sanfrancisco.atenea.application.account.command;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.SetScopesCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scopes;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class SetScopesUseCase {

    private final AccountRepository repository;
    private final Clock clock;

    public SetScopesUseCase(
            final AccountRepository repository,
            final Clock clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public void execute(SetScopesCommand command) {
        final Account actor = repository.findAccountById(command.actorId())
                .orElseThrow(() -> new AccountNotFoundException(command.actorId()));

        final Account target = repository.findAccountById(command.targetId())
                .orElseThrow(() -> new AccountNotFoundException(command.targetId()));

        final Scopes newScopes = Scopes.of(command.scopes().toArray(new Scope[0]));

        target.setScopes(newScopes, actor, clock);

        repository.update(target);
    }

}
