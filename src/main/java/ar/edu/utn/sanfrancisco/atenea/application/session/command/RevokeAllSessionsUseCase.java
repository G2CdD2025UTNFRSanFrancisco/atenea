package ar.edu.utn.sanfrancisco.atenea.application.session.command;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RevokeAllSessionsCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidSessionException;

import java.time.Clock;

public class RevokeAllSessionsUseCase {

    private final AccountRepository accountRepository;
    private final SessionRepository sessionRepository;
    private final Clock clock;

    public RevokeAllSessionsUseCase(
            final AccountRepository accountRepository,
            final SessionRepository sessionRepository,
            final Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.clock = clock;
    }

    public void execute(final RevokeAllSessionsCommand command) {
        final Account operator = accountRepository.findAccountById(command.operatorId())
                .orElseThrow(InvalidSessionException::new);
        final Account target = command.isSelfOperation() ? operator :
                this.accountRepository.findAccountById(command.targetId())
                        .orElseThrow(() -> new AccountNotFoundException(command.targetId()));
        target.allows(operator, Scope.MANAGE_SESSIONS);
        target.invalidateSessions(clock);
        this.accountRepository.update(target);
        this.sessionRepository.revokeAllByAccountId(command.targetId());
    }

}
