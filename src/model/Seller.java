//Nighat
package model;

public class Seller extends User {

    private int    totalBooksSold;
    private double totalEarnings;

    public Seller(int id, String name, String phone, String password) {
        super(id, name, phone, password, "seller");
        this.totalBooksSold = 0;
        this.totalEarnings  = 0.0;
    }

    public int    getTotalBooksSold() { return totalBooksSold; }
    public double getTotalEarnings()  { return totalEarnings; }

    public void setTotalBooksSold(int total)    { this.totalBooksSold = total; }
    public void setTotalEarnings(double amount) { this.totalEarnings = amount; }

    @Override
    public String getInfo() {
        return super.getInfo() + " | Books Sold: " + totalBooksSold +
               " | Earnings: Rs." + totalEarnings;
    }

}