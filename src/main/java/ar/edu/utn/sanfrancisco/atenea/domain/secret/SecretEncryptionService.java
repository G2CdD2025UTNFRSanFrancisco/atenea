package ar.edu.utn.sanfrancisco.atenea.domain.secret;

public interface SecretEncryptionService {

    PlainSecret decrypt(final EncryptedSecret cipher);
    EncryptedSecret encrypt(final PlainSecret secret);

}
