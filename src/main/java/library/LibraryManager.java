package library;

import library.dao.BookDetailsDAO;
import library.dao.BookDAO;
import library.exception.BookAlreadyLentException;
import library.exception.BookNotExistException;
import library.model.Book;
import library.model.BookDetails;
import library.search.SearchCriteria;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

public class LibraryManager implements Library {

    private final static Logger logger = Logger.getLogger(LibraryManager.class.getName());

    private List<Book> books = new ArrayList<>();
    private BookDetailsDAO bookDetailsDAO = new BookDetailsDAO();
    private BookDAO bookDAO = new BookDAO();

    @Override
    public String addNewBook(String title, String author, Integer year) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setAuthor(author);
        criteria.setTitle(title);
        criteria.setYear(year);

        String bookDetailsId = bookDetailsDAO.getBookDetailsIdByParams(criteria);
        if(isNull(bookDetailsId)) {
            BookDetails bookDetails = new BookDetails(title, author, year);
          bookDetailsId = bookDetailsDAO.addBookDetail(bookDetails);
        }
      return addBook(bookDetailsId);
    }


    /**
     * Method that creates an instance of Book,
     * which contains all information about each bookDetails in library.
     * This instance is added to all bookDetails distrobutors list
     * @param bookDetailsId
     * @return id
     */
    String addBook(String bookDetailsId) {
        Book book = new Book(bookDetailsId);
        String id = bookDAO.addBookDistributor(book);
        return id;
    }

    @Override
    public BookDetails getBookById(String id) throws BookNotExistException {

        if (!checkIfBookExist(id)) {
            throw new BookNotExistException("This book doesn't exist in library");
        }
        String bookDetailId = bookDAO.getBookDistributor(id).getBookDetailsId();
        return bookDetailsDAO.getBookDetailsById(bookDetailId);
    }

    /**
     * @return List of all books
     */
    @Override
    public List<BookDetails> getAllBookDetails() {
        return bookDetailsDAO.getAllBookDetails();
    }

    /**
     * Method that display all information about all books in library
     * Information: BookDetails, available amount, lent amount
     */
    @Override
    public void displayAllBooksInformation() {
        Map<String, Set<Book>> result = bookDAO.getAllBookDistributors().stream().collect(Collectors.groupingBy(Book::getBookDetailsId, toSet()));
        result.forEach((k, v) -> {
            System.out.println("BookDetails " + bookDetailsDAO.getBookDetailsById(k).getTitle() + " "
                    + bookDetailsDAO.getBookDetailsById(k).getAuthor() + " "
                    + bookDetailsDAO.getBookDetailsById(k).getYear());
            System.out.println("Available amount in library " + v.stream().filter(bd -> !bd.isLent()).count());
            System.out.println("Lent amount " + v.stream().filter(Book::isLent).count());
        });

         }


    @Override
    public void displayBookInformation(String id) throws BookNotExistException {
        if (!checkIfBookExist(id)) {
            throw new BookNotExistException("This book doesn't exist in library");
        }

        Book bd = bookDAO.getBookDistributor(id);
        System.out.println("BookDetails details ");
        System.out.println(bookDetailsDAO.getBookDetailsById(bd.getBookDetailsId()).toString());
        System.out.println("BookDetails is " + (bd.isLent() ? "lent" : "available"));
        System.out.println("Last person that lent the book " + bd.getLastLenderName());
    }


    /**
     * @param id
     * @return if book exist
     */
    private boolean checkIfBookExist(String id) {
        return bookDAO.isInLibrary(id);
        }

    /**
     * @param id
     * @return if book is lent
     */
    boolean checkIfBookIsLent(String id) {
        return bookDAO.checkIfBookIsLent(id);
    }


    @Override
    public List<BookDetails> getBookListByCriteria(SearchCriteria criteria) {
        Stream<Book> stream = bookDAO.getAllBookDistributors().stream();
        if (!isNull(criteria.getAuthor())) {
            stream = stream.filter(b -> bookDetailsDAO.getBookDetailsById(b.getBookDetailsId()).getAuthor().equals(criteria.getAuthor()));
        }
        if (!isNull(criteria.getTitle())) {
            stream = stream.filter(b -> bookDetailsDAO.getBookDetailsById(b.getBookDetailsId()).getTitle().equals(criteria.getTitle()));
        }
        if (!isNull(criteria.getYear())) {
            stream = stream.filter(b -> bookDetailsDAO.getBookDetailsById(b.getBookDetailsId()).getYear().equals(criteria.getYear()));
        }
        return stream.map(b ->bookDetailsDAO.getBookDetailsById(b.getBookDetailsId())).collect(Collectors.toList());
    }


    @Override
    public synchronized void removeBook(String id) throws BookNotExistException, BookAlreadyLentException {
        if (!checkIfBookExist(id)) {
            throw new BookNotExistException("This book doesn't exist in library");
        }
        if (checkIfBookIsLent(id)) {
            throw new BookAlreadyLentException("This book is currently lent");
        }
        Book bd = bookDAO.getBookDistributor(id);

        bookDAO.removeBook(id);
        updateBookDetails();
    }

    private void updateBookDetails(){
        List<String> ids = bookDAO.getAllBookDistributors().stream().map(b -> b.getBookDetailsId()).collect(Collectors.toList());
        List<String> idsToRemove = bookDetailsDAO.getKeys().stream().filter(k -> !ids.contains(k)).collect(Collectors.toList());
        idsToRemove.stream().forEach(i -> bookDetailsDAO.removeBookDetails(i));
    }

   /* private int getAllBookDistributorsByBookDetail(BookDetails bookDetails){

    }*/


    @Override
    public synchronized BookDetails lendBook(String id, String person) throws BookAlreadyLentException, BookNotExistException {
        if (!checkIfBookExist(id)) {
            throw new BookNotExistException("This book doesn't exist in library");
        }
        if (checkIfBookIsLent(id)) {
            throw new BookAlreadyLentException("This book is currently lent");
        }

        Book bd = bookDAO.getBookDistributor(id);
        bd.setLastLenderName(person);
        bd.setLent(true);

        return bookDetailsDAO.getBookDetailsById(bd.getBookDetailsId());
    }

    @Override
    public synchronized BookDetails lendBook(SearchCriteria criteria, String person) throws BookNotExistException, BookAlreadyLentException {
       String detailsId = bookDetailsDAO.getBookDetailsIdByParams(criteria);
       if(isNull(detailsId)){
           throw new BookNotExistException("This book doesn't exist in library");
       }

       List<Book> listOfBooks = bookDAO.getBookDistibutorsByBookDetail(detailsId);
       Book bd = getFirstAvailableBook(listOfBooks);

        if (isNull(bd)) {
            throw new BookAlreadyLentException("All books are currently lent");
        }

        bd.setLastLenderName(person);
        bd.setLent(true);

        return bookDetailsDAO.getBookDetailsById(bd.getBookDetailsId());
    }

    private Book getFirstAvailableBook(List<Book> list){
        Optional<Book> available = list.stream().filter(b -> !b.isLent()).findFirst();
        if(!isNull(available)){
          return available.get();
        }
        else{
            return null;
        }
    }

}
