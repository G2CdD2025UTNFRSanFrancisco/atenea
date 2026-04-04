package ar.edu.utn.sanfrancisco.atenea.infrastructure.mfa.rest.dto;

import java.util.Set;

public record RecoveryCodesResponse(
        Set<String> codes
) {
}

