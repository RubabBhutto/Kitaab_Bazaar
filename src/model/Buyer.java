//Nighat

package model;

public class Buyer extends User {

    private int totalOrdersPlaced;

    public Buyer(int id, String name, String phone, String password) {
        super(id, name, phone, password, "buyer");
        this.totalOrdersPlaced = 0;
    }

    public int getTotalOrdersPlaced()           { return totalOrdersPlaced; }
    public void setTotalOrdersPlaced(int total) { this.totalOrdersPlaced = total; }

    @Override
    public String getInfo() {
        return super.getInfo() + " | Orders Placed: " + totalOrdersPlaced;
    }

   
}