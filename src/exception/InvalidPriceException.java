//Rubab
package exception;

public class InvalidPriceException extends RuntimeException {

    private double attemptedPrice;

    public InvalidPriceException(String message) {
        super(message);
    }

    public InvalidPriceException(double price) {
        super("Invalid price: Rs." + price + ". Price must be greater than zero.");
        this.attemptedPrice = price;
    }

    public double getAttemptedPrice() {
        return attemptedPrice;
    }
}
