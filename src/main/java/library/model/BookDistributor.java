package library.model;


public class BookDistributor {

    private String bookDetailsId;
    private boolean isLent;
    private String lastLenderName;

    public BookDistributor(String bookDetailsId) {
        this.bookDetailsId = bookDetailsId;
    }

    public String getBookDetailsId() {
        return bookDetailsId;
    }

    public boolean isLent() {
        return isLent;
    }

    public String getLastLenderName() {
        return lastLenderName;
    }

    public void setLent(boolean lent) {
        isLent = lent;
    }

    public void setLastLenderName(String lastLenderName) {
        this.lastLenderName = lastLenderName;
    }
}
