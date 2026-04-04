package ar.edu.utn.sanfrancisco.atenea.application.account.command;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.ChangePasswordCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PlainPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class ChangePasswordUseCase {

    private final AccountRepository accountRepository;
    private final PasswordHashService passwordHashService;
    private final Clock clock;

    public ChangePasswordUseCase(
            final AccountRepository accountRepository,
            final PasswordHashService passwordHashService,
            final Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.passwordHashService = passwordHashService;
        this.clock = clock;
    }

    @Transactional
    public void execute(final ChangePasswordCommand command) {
        final Account account = this.accountRepository.findAccountById(command.accountId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
        account.changePassword(
                new PlainPassword(command.newPassword()),
                this.passwordHashService,
                this.clock
        );

        this.accountRepository.update(account);
    }

}
