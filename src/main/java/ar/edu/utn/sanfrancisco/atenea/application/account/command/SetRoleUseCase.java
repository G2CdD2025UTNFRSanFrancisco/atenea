package ar.edu.utn.sanfrancisco.atenea.application.account.command;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.SetRoleCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.OwnerAlreadyExistsException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.role.Role;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class SetRoleUseCase {

    private final AccountRepository repository;
    private final Clock clock;

    public SetRoleUseCase(
            final AccountRepository repository,
            final Clock clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public void execute(final SetRoleCommand command) {
        final Account actor = repository.findAccountById(command.actorId())
                .orElseThrow(() -> new AccountNotFoundException(command.actorId()));

        final Account target = repository.findAccountById(command.targetId())
                .orElseThrow(() -> new AccountNotFoundException(command.targetId()));

        if (command.role() == Role.OWNER && repository.existsActiveOwnerExcept(target.getId())) {
            throw new OwnerAlreadyExistsException();
        }

        target.setRole(command.role(), actor, clock);

        repository.update(target);
    }
}

