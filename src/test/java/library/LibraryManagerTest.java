package library;

import library.exception.BookAlreadyLentException;
import library.exception.BookNotExistException;
import library.model.BookDetails;
import library.search.SearchCriteria;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class LibraryManagerTest {

    @Test
    public void shouldAddNewBook() throws BookNotExistException {
       LibraryManager library = new LibraryManager();
       library.addNewBook("Title1", "Author1", 2000);
       Assert.assertEquals(1, library.getAllBookDetails().size());
    }

    @Test (expected = BookNotExistException.class)
    public void shouldThrowBookNotExistException() throws BookNotExistException {
        LibraryManager library = new LibraryManager();
        library.getBookById("123456789");
    }

    @Test
    public void shouldRemoveBook() throws BookNotExistException, BookAlreadyLentException {
        LibraryManager library = new LibraryManager();

        String id = library.addNewBook("Title 1", "Author 1", 2000);
        BookDetails bookDetails = new BookDetails.BookDetailsBuilder()
                .setTitle("Title 1")
                .setAuthor("Author 1")
                .setYear(2000)
                .build();
        Assert.assertEquals(bookDetails, library.getBookById(id));

        library.removeBook(id);
        Assert.assertTrue(library.getAllBookDetails().isEmpty());
    }

    @Test
    public void shouldLentBook() throws BookNotExistException, BookAlreadyLentException {
        LibraryManager library = new LibraryManager();

        String id = library.addNewBook("Title 1", "Author 1", 2000);
        BookDetails lentBookDetails = library.lendBook(id, "Jan Kowalski");

        Assert.assertEquals(true, library.checkIfBookIsLent(id));
    }

    @Test(expected = BookAlreadyLentException.class)
    public void shouldNotRemoveBookBecauseIsAlreadyLent() throws BookNotExistException, BookAlreadyLentException {
        LibraryManager library = new LibraryManager();
        BookDetails bookDetails = new BookDetails.BookDetailsBuilder()
                .setTitle("Title 1")
                .setAuthor("Author 1")
                .setYear(2000)
                .build();
        String id = library.addNewBook("Title 1", "Author 1", 2000);
        BookDetails lentBookDetails = library.lendBook(id, "Jan Kowalski");
        library.lendBook(id, "Anna Kowalska");
    }

    @Test
    public void shouldGetListOfAllBooks(){
        LibraryManager library = new LibraryManager();
        library.addNewBook("Title 1", "Author 1", 2000);
        library.addNewBook("Title 2", "Author 2", 2001);
        library.addNewBook("Title 3", "Author 3", 2002);

        Assert.assertEquals(3, library.getAllBookDetails().size());
    }

    @Test
    public void shouldGetBookByCriteria(){

        LibraryManager library = new LibraryManager();
        library.addNewBook("Title 1", "Author 1", 2000);
        library.addNewBook("Title 2", "Author 1", 2001);
        library.addNewBook("Title 3", "Author 3", 2002);


        SearchCriteria criteria = new SearchCriteria();
        criteria.setAuthor("Author 1");

        List<BookDetails> booksOfAuthor1 = library.getBookListByCriteria(criteria);
        Assert.assertEquals(2, booksOfAuthor1.size());

        criteria.setTitle("Title 1");
        List<BookDetails> booksOfAuthor1AndTitle1 = library.getBookListByCriteria(criteria);
        Assert.assertEquals(1, booksOfAuthor1AndTitle1.size());

    }

    @Test
    public void shouldListedCorrectListOfBooksInLibrary() throws BookNotExistException, BookAlreadyLentException {

        LibraryManager library = new LibraryManager();
        String book1Id = library.addNewBook("Title 1", "Author 1", 2000);
        String book2Id = library.addNewBook("Title 2", "Author 1", 2000);
        String book3Id = library.addNewBook("Title 3", "Author 3", 2002);
        String book4Id = library.addNewBook("Title 3", "Author 3", 2002);
        String book5Id = library.addNewBook("Title 4", "Author 3", 2002);

        library.displayAllBooksInformation();

        library.lendBook(book1Id, "Jan Kowalski");
        library.lendBook(book3Id, "Anna Kowalska");
        library.displayAllBooksInformation();

    }

}
