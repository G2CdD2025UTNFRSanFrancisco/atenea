package ar.edu.utn.sanfrancisco.atenea.application.account.command.dto;

import ar.edu.utn.sanfrancisco.atenea.domain.account.AccountId;

public record ChangePasswordCommand(
        AccountId accountId,
        char[] newPassword
) {
}
