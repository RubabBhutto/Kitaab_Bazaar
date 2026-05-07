//Rubab

package model;

public abstract class AbstractListing implements Manageable {

    // Shared private fields (ENCAPSULATION)
    private int    id;
    private double price;
    private String status; 

    public AbstractListing(int id, double price, String status) {
        this.id     = id;
        this.price  = price;
        this.status = status;
    }

    public AbstractListing() {}

    public int    getId()     { return id; }
    public double getPrice()  { return price; }
    public String getStatus() { return status; }

    public void setId(int id)         { this.id = id; }
    public void setStatus(String s)   { this.status = s; }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new exception.InvalidPriceException(price);
        }
        this.price = price;
    }

    public boolean isActive() {
        return "available".equalsIgnoreCase(status);
    }

    public abstract String getSummary();

    public abstract String getListingType();
}
