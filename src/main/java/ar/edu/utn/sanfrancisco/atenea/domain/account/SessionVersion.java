package ar.edu.utn.sanfrancisco.atenea.domain.account;

public record SessionVersion(
    long value
) {

    public SessionVersion next() {
        return new SessionVersion(this.value() + 1);
    }

}
