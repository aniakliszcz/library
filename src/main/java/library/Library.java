package library;

import library.exception.BookAlreadyLentException;
import library.exception.BookNotExistException;
import library.model.BookDetails;
import library.search.SearchCriteria;

import java.util.List;

public interface Library {

    /**
     * Method that creates an instance of BookDetails and calls the method
     * @addBookToLibrary
     * @param title
     * @param author
     * @param year
     */
    String addNewBook(String title, String author, Integer year);


    /**
     * Method checks if book with given id exist in library.
     * If no, it returns an exception
     * If yes, it returns book
     * @param id
     * @return BookDetails
     * @throws BookNotExistException
     */
    BookDetails getBookById(String id) throws BookNotExistException;

    /**
     * @return List of all books
     */
    List<BookDetails> getAllBookDetails();

    /**
     * Method that display all information about all books in library
     * Information: BookDetails, available amount, lent amount
     */
    void displayAllBooksInformation();

    /**
     * Method display all book details like author, title, year.
     * Display also information is book is available or lent and last person that lent the book
     * @param id
     * @throws BookNotExistException
     */
    void displayBookInformation(String id) throws BookNotExistException;

    /**
     * Method that returns list of books by criteria like title, author and year
     * @param criteria
     * @return list of book
     */
    List<BookDetails> getBookListByCriteria(SearchCriteria criteria);

    /**
     * Method that removes book from library
     * Before removing checks if book exist and is not currently lent
     * @param id
     * @throws BookNotExistException
     * @throws BookAlreadyLentException
     */
    void removeBook(String id) throws BookNotExistException, BookAlreadyLentException;


    /**
     * Method that allows to lent a book
     * Before checks if book exist and is not currently lent
     * Method assigned to Book the person who lent the book
     * @param id
     * @param person
     * @return BookDetails
     * @throws BookAlreadyLentException
     * @throws BookNotExistException
     */
    BookDetails lendBook(String id, String person) throws BookAlreadyLentException, BookNotExistException;

    /**
     * Method that allows to lent a book
     * Before checks if there are any available books in library
     * Method assigned to Book the person who lent the book
     * @param criteria
     * @param person
     * @return BookDetails
     * @throws BookAlreadyLentException
     * @throws BookNotExistException
     */
    BookDetails lendBook(SearchCriteria criteria, String person) throws BookAlreadyLentException, BookNotExistException;

}
