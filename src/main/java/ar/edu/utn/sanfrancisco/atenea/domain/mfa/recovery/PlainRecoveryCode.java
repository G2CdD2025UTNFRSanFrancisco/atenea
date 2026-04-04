package ar.edu.utn.sanfrancisco.atenea.domain.mfa.recovery;

import java.util.Arrays;

public final class PlainRecoveryCode implements AutoCloseable, CharSequence {

    private char[] value;

    public PlainRecoveryCode(char[] value) {
        this.value = value;
    }

    public char[] value() {
        return value;
    }

    @Override
    public void close() {
        Arrays.fill(value, '\0');
        value = null;
    }

    @Override
    public int length() {
        ensureNotDestroyed();
        return this.value.length;
    }

    @Override
    public char charAt(int index) {
        ensureNotDestroyed();
        return value[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        ensureNotDestroyed();
        return new String(value, start, end - start);
    }

    private void ensureNotDestroyed() {
        if (value == null) {
            throw new IllegalStateException("Password already destroyed");
        }
    }
}
