package ar.edu.utn.sanfrancisco.atenea.infrastructure.account.rest;

import ar.edu.utn.sanfrancisco.atenea.domain.account.scope.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/scopes")
public class ScopeController {

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_MANAGE_ACCOUNTS') or hasAuthority('SCOPE_ADMIN')")
    public Map<String, Long> getAllScopes() {
        return Arrays.stream(Scope.values())
                .map(s -> Map.entry(s.name(), calculateBitValue(s)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private long calculateBitValue(Scope scope) {
        return 1L << scope.ordinal();
    }
}

