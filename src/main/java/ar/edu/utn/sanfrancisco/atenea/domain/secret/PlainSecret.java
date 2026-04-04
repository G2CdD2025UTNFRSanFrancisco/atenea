package ar.edu.utn.sanfrancisco.atenea.domain.secret;

import org.apache.commons.codec.binary.Base32;

import java.util.Arrays;

public record PlainSecret(char[] value) implements CharSequence {

    private static final Base32 BASE32 = new Base32();

    public static PlainSecret fromBytes(byte[] bytes) {
        try {
            String encoded = BASE32.encodeToString(bytes);
            return new PlainSecret(encoded.toCharArray());
        } finally {
            Arrays.fill(bytes, (byte) 0); // limpieza
        }
    }

    public byte[] toBytes() {
        return BASE32.decode(new String(value));
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public char charAt(int index) {
        return value[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new String(value, start, end - start);
    }

    @Override
    public String toString() {
        return new String(value);
    }
}