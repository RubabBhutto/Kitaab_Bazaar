//Rubab

package exception;

public class InvalidUserInputException extends Exception {

    private String fieldName; 

    public InvalidUserInputException(String fieldName, String message) {
        super("Validation failed for [" + fieldName + "]: " + message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
