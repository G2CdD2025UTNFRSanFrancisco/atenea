package ar.edu.utn.sanfrancisco.atenea.infrastructure.account;

import ar.edu.utn.sanfrancisco.atenea.application.account.command.ChangePasswordUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.CreateAccountUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.DeleteAccountUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.command.SetRoleUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.query.GetAccountDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.account.query.GetAllAccountDetailsUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.account.credential.PasswordHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.identity.IdentityGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AccountConfiguration {

    @Bean
    public GetAllAccountDetailsUseCase provideGetAllAccountDetails(
            final AccountRepository accountRepository
    ) {
        return new GetAllAccountDetailsUseCase(accountRepository);
    }

    @Bean
    public GetAccountDetailsUseCase provideGetAccountDetails(
            final AccountRepository accountRepository
    ) {
        return new GetAccountDetailsUseCase(accountRepository);
    }

    @Bean
    public CreateAccountUseCase provideCreateAccountUseCase(
            final AccountRepository accountRepository,
            final PasswordHashService passwordHashService,
            final IdentityGenerator identityGenerator,
            final Clock clock
    ) {
        return new CreateAccountUseCase(accountRepository, passwordHashService, identityGenerator, clock);
    }

    @Bean
    public DeleteAccountUseCase provideDeleteAccountUseCase(
            final AccountRepository accountRepository,
            final Clock clock
    ) {
        return new DeleteAccountUseCase(accountRepository, clock);
    }

    @Bean
    public SetRoleUseCase provideSetRoleUseCase(
            final AccountRepository accountRepository,
            final Clock clock
    ) {
        return new SetRoleUseCase(accountRepository, clock);
    }

    @Bean
    public ChangePasswordUseCase provideChangePasswordUseCase(
            final AccountRepository accountRepository,
            final PasswordHashService passwordHashService,
            final Clock clock
    ) {
        return new ChangePasswordUseCase(accountRepository, passwordHashService, clock);
    }
}
