package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.rest;

import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.ActivateTotpUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.DisableMfaUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.RegenerateRecoveryCodesUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.StartTotpEnrollmentUseCase;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.dto.StartTotpEnrollmentResult;
import ar.edu.utn.sanfrancisco.atenea.application.mfa.command.VerifyTotpUseCase;
import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.factor.totp.TotpCode;
import ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery.PlainRecoveryCollection;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.rest.dto.RecoveryCodesResponse;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.rest.dto.StartTotpEnrollmentResponse;
import ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.rest.dto.TotpCodeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/mfa")
public class MfaController {

    private final StartTotpEnrollmentUseCase startTotpEnrollmentUseCase;
    private final ActivateTotpUseCase activateTotpUseCase;
    private final VerifyTotpUseCase verifyTotpUseCase;
    private final RegenerateRecoveryCodesUseCase regenerateRecoveryCodesUseCase;
    private final DisableMfaUseCase disableMfaUseCase;

    public MfaController(
            final StartTotpEnrollmentUseCase startTotpEnrollmentUseCase,
            final ActivateTotpUseCase activateTotpUseCase,
            final VerifyTotpUseCase verifyTotpUseCase,
            final RegenerateRecoveryCodesUseCase regenerateRecoveryCodesUseCase,
            final DisableMfaUseCase disableMfaUseCase
    ) {
        this.startTotpEnrollmentUseCase = startTotpEnrollmentUseCase;
        this.activateTotpUseCase = activateTotpUseCase;
        this.verifyTotpUseCase = verifyTotpUseCase;
        this.regenerateRecoveryCodesUseCase = regenerateRecoveryCodesUseCase;
        this.disableMfaUseCase = disableMfaUseCase;
    }

    @PostMapping("/totp/enrollment")
    public StartTotpEnrollmentResponse startTotpEnrollment(final JwtAuthenticationToken principal) {
        StartTotpEnrollmentResult enrollment = startTotpEnrollmentUseCase.execute(accountIdFrom(principal));
        return new StartTotpEnrollmentResponse(
                enrollment.otpauthUri(),
                enrollment.manualEntryKey(),
                enrollment.issuer(),
                enrollment.accountLabel()
        );
    }

    @PostMapping("/totp/activation")
    public RecoveryCodesResponse activateTotp(
            @Valid @RequestBody final TotpCodeRequest request,
            final JwtAuthenticationToken principal
    ) {
        final PlainRecoveryCollection recoveryCodes = activateTotpUseCase.execute(
                accountIdFrom(principal),
                request.totpCode()
        );
        return toRecoveryCodesResponseAndClose(recoveryCodes);
    }

    @PostMapping("/totp/verification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyTotp(
            @Valid @RequestBody final TotpCodeRequest request,
            final JwtAuthenticationToken principal
    ) {
        verifyTotpUseCase.execute(accountIdFrom(principal), new TotpCode(request.totpCode()));
    }

    @PostMapping("/recovery-codes/regeneration")
    public RecoveryCodesResponse regenerateRecoveryCodes(final JwtAuthenticationToken principal) {
        final PlainRecoveryCollection recoveryCodes = regenerateRecoveryCodesUseCase.execute(accountIdFrom(principal));
        return toRecoveryCodesResponseAndClose(recoveryCodes);
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableMfa(final JwtAuthenticationToken principal) {
        disableMfaUseCase.execute(accountIdFrom(principal));
    }

    private AccountId accountIdFrom(final JwtAuthenticationToken principal) {
        return new AccountId(Long.parseLong(principal.getToken().getSubject()));
    }

    private RecoveryCodesResponse toRecoveryCodesResponseAndClose(final PlainRecoveryCollection recoveryCodes) {
        try {
            final Set<String> codes = recoveryCodes.codes()
                    .stream()
                    .map(code -> String.valueOf(code.value()))
                    .collect(Collectors.toSet());
            return new RecoveryCodesResponse(codes);
        } finally {
            closeQuietly(recoveryCodes);
        }
    }

    private void closeQuietly(final PlainRecoveryCollection recoveryCodes) {
        try {
            recoveryCodes.close();
        } catch (Exception ignored) {
            // Best effort wipe of in-memory recovery codes.
        }
    }
}

