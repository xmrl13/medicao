package exceptions;

public class InvalidSecretPhraseException extends RuntimeException {
    public InvalidSecretPhraseException(String message) {
        super(message);
    }
}
