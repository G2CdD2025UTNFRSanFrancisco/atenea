package ar.edu.utn.sanfrancisco.atenea.application.account.command;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.dto.CreateAccountCommand;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Account;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.Username;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PlainPassword;
import ar.edu.utn.sanfrancisco.atenea.domain.account.exception.AccountAlreadyExistsException;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

public class CreateAccountUseCase {

    private final AccountRepository repository;
    private final PasswordHashService hashService;
    private final IdentityGenerator idGenerator;
    private final Clock clock;

    public CreateAccountUseCase(
            AccountRepository repository,
            PasswordHashService hashService,
            IdentityGenerator idGenerator,
            Clock clock
    ) {
        this.repository = repository;
        this.hashService = hashService;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    @Transactional
    public AccountId execute(final CreateAccountCommand command) {
        Username username = new Username(command.username());
        if (repository.existsByUsername(username)) {
            throw new AccountAlreadyExistsException(username);
        }

        try (PlainPassword password = new PlainPassword(command.password())) {
            Account newAccount = Account.create(
                    username,
                    password,
                    idGenerator,
                    hashService,
                    clock
            );
            repository.create(newAccount);
            return newAccount.getId();
        }
    }
}
