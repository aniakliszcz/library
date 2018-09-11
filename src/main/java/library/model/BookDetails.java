package library.model;


import java.util.Objects;

public class BookDetails {

    private final String title;
    private final String author;
    private final Integer year;


    public BookDetails(final String title, final String author, final Integer year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getYear() {
        return year;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDetails bookDetails = (BookDetails) o;
        return Objects.equals(title, bookDetails.title) &&
                Objects.equals(author, bookDetails.author) &&
                Objects.equals(year, bookDetails.year);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, author, year);
    }

    @Override
    public String toString() {
        return "BookDetails{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                '}';
    }
}
