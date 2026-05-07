//Nighat

package model;

public class Book extends AbstractListing {

    // PRIVATE fields — ENCAPSULATION
    private String title;
    private int    grade;        
    private String publisher;
    private String condition;   
    private int    sellerId;
    private int    categoryId;
    private String categoryName; 
    private String sellerName;  

    public Book(int id, String title, int grade, String publisher,
                double price, String condition, String status,
                int sellerId, int categoryId) {
        super(id, price, status); // AbstractListing constructor
        this.title      = title;
        this.grade      = grade;
        this.publisher  = publisher;
        this.condition  = condition;
        this.sellerId   = sellerId;
        this.categoryId = categoryId;
    }

    public Book() { super(); }

    public String getTitle()        { return title; }
    public int    getGrade()        { return grade; }
    public String getPublisher()    { return publisher; }
    public String getCondition()    { return condition; }
    public int    getSellerId()     { return sellerId; }
    public int    getCategoryId()   { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getSellerName()   { return sellerName; }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Book title cannot be empty.");
        this.title = title;
    }
    public void setGrade(int grade)            { this.grade = grade; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setSellerId(int id)            { this.sellerId = id; }
    public void setCategoryId(int id)          { this.categoryId = id; }
    public void setCategoryName(String name)   { this.categoryName = name; }
    public void setSellerName(String name)     { this.sellerName = name; }

    public boolean isAvailable() { return isActive(); }


    @Override
    public String getSummary() {
        return "Book: " + title + " | Grade: " + (grade == 0 ? "General" : grade)
             + " | Price: Rs." + getPrice() + " | Condition: " + condition
             + " | Status: " + getStatus();
    }

    @Override
    public String getListingType() {
        return "Book";
    }

    @Override
    public String toString() {
        return "Book[" + title + ", Grade=" + grade +
               ", Price=Rs." + getPrice() + ", Status=" + getStatus() + "]";
    }
}
