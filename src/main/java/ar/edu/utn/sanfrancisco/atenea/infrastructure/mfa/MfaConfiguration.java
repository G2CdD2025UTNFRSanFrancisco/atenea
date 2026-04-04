package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa;

import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.ActivateTotpUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.DisableMfaUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.RegenerateRecoveryCodesUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.StartTotpEnrollmentUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.VerifyTotpUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.MfaEnrollmentRepository;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpService;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.RecoveryCodeGenerator;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.RecoveryCodeHashService;
import ar.edu.utn.sanfrancisco.atenea.domain.secret.SecretEncryptionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class MfaConfiguration {

    @Bean
    public StartTotpEnrollmentUseCase provideStartTotpEnrollmentUseCase(
            final MfaEnrollmentRepository mfaEnrollmentRepository,
            final AccountRepository accountRepository,
            final TotpService totpService,
            final SecretEncryptionService secretEncryptionService,
            final Clock clock
    ) {
        return new StartTotpEnrollmentUseCase(
                mfaEnrollmentRepository,
                accountRepository,
                totpService,
                secretEncryptionService,
                clock
        );
    }

    @Bean
    public ActivateTotpUseCase provideActivateTotpUseCase(
            final MfaEnrollmentRepository mfaEnrollmentRepository,
            final TotpService totpService,
            final RecoveryCodeGenerator recoveryCodeGenerator,
            final RecoveryCodeHashService recoveryCodeHashService,
            final SecretEncryptionService secretEncryptionService,
            final Clock clock
    ) {
        return new ActivateTotpUseCase(
                mfaEnrollmentRepository,
                totpService,
                recoveryCodeGenerator,
                recoveryCodeHashService,
                secretEncryptionService,
                clock
        );
    }

    @Bean
    public VerifyTotpUseCase provideVerifyTotpUseCase(
            final MfaEnrollmentRepository mfaEnrollmentRepository,
            final TotpService totpService,
            final SecretEncryptionService secretEncryptionService,
            final Clock clock
    ) {
        return new VerifyTotpUseCase(
                mfaEnrollmentRepository,
                totpService,
                secretEncryptionService,
                clock
        );
    }

    @Bean
    public RegenerateRecoveryCodesUseCase provideRegenerateRecoveryCodesUseCase(
            final MfaEnrollmentRepository mfaEnrollmentRepository,
            final RecoveryCodeGenerator recoveryCodeGenerator,
            final RecoveryCodeHashService recoveryCodeHashService
    ) {
        return new RegenerateRecoveryCodesUseCase(
                mfaEnrollmentRepository,
                recoveryCodeGenerator,
                recoveryCodeHashService
        );
    }

    @Bean
    public DisableMfaUseCase provideDisableMfaUseCase(
            final MfaEnrollmentRepository mfaEnrollmentRepository
    ) {
        return new DisableMfaUseCase(mfaEnrollmentRepository);
    }
}

