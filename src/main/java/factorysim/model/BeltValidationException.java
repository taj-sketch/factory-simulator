package factorysim.model;

/**
 * An exception thrown when a belt configuration is invalid (e.g. when setting up a Factory Network)
 * This can occur when a output only reserved belt name (i.e. sink) is connected to an input port
 * or when a belt is connected to multiple output ports that produce different item types.
 */
public class BeltValidationException extends Exception {

    public BeltValidationException(String message) {
        super(message);
    }
}
