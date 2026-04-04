package ar.edu.utn.sanfrancisco.atenea.application.session.command.dto;

public record RefreshSessionResponse(
        String accessToken,
        String refreshToken
) {
}
