import java.util.List;

public class Book {
    String title;
    int pages;
    List<String> authors;

    public Book() {}

    public Book(String title, int pages, List<String> authors) {
        this.title = title;
        this.pages = pages;
        this.authors = authors;
    }
}
