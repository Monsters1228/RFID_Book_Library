package Book.model;

/**
 * 书本实体
 * @author Monsters
 */
public class Book {
    private String bookId;
    private String bookName;
    private String author;
    private String publisher;

    public String getBookId(){
        return this.bookId;
    }
    public String getBookName() {
        return bookName;
    }
    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
