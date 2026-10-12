// DO NOT MODIFY THIS FILE
// YOU DO NOT NEED TO USE OR UNDERSTAND THIS CLASS TO COMPLETE THE ASSIGNMENT.
package factorysim.config;

/**
 * Exception thrown when parsing a configuration file fails.
 * Includes the line number where the error occurred in the message.
 */
public class ConfigParseException extends Exception {

    /**
     * Creates a new ConfigParseException with the given message and line number.
     * @param message the error message
     * @param lineNumber the line number where the error occurred
     */
    public ConfigParseException(String message, int lineNumber) {
        super(formatMessage(message, lineNumber));
    }

    /**
     * Creates a new ConfigParseException with the given message and line number.
     * @param message the error message
     * @param lineNumber  the line number where the error occurred
     * @param cause the underlying cause of the error
     */
    public ConfigParseException(
        String message,
        int lineNumber,
        Throwable cause
    ) {
        super(formatMessage(message, lineNumber), cause);
    }

    // private method to format the message to include the line number.
    private static String formatMessage(String message, int lineNumber) {
        if (lineNumber > 0) {
            return "Line " + lineNumber + ": " + message;
        }
        return message;
    }
}
