package ar.edu.utn.sanfrancisco.atenea.application.account.command.dto;

public record CreateAccountCommand(
        String username,
        char[] password
) {
}
