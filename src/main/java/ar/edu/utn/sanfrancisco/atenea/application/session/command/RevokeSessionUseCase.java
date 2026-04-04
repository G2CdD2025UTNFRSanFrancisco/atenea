package ar.edu.utn.sanfrancisco.atenea.application.session.command;

import ar.edu.utn.sanfrancisco.atenea.application.session.command.dto.RevokeSessionCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import ar.edu.utn.sanfrancisco.atenea.domain.session.Session;
import ar.edu.utn.sanfrancisco.atenea.domain.session.SessionRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.InvalidSessionException;
import ar.edu.utn.sanfrancisco.atenea.domain.session.exceptions.SessionNotFoundException;

import java.time.Clock;

public class RevokeSessionUseCase {

    private final AccountRepository accountRepository;
    private final SessionRepository sessionRepository;
    private final Clock clock;

    public RevokeSessionUseCase(
            final AccountRepository accountRepository,
            final SessionRepository sessionRepository,
            final Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.clock = clock;
    }

    public void execute(final RevokeSessionCommand command) {
        final Session session = this.sessionRepository.findById(command.sessionId())
                .orElseThrow(() -> new SessionNotFoundException(command.sessionId()));
        if (session.getAccountId().equals(command.operatorId())) {
            session.revoke(this.clock);
            this.sessionRepository.update(session);
            return;
        }

        final Account operator = this.accountRepository.findAccountById(command.operatorId())
                .orElseThrow(InvalidSessionException::new);
        final Account target = this.accountRepository.findAccountById(session.getAccountId())
                        .orElseThrow(() -> new AccountNotFoundException(session.getAccountId()));

        target.allows(operator, Scope.MANAGE_SESSIONS);
        session.revoke(this.clock);
        this.sessionRepository.update(session);
    }

}
