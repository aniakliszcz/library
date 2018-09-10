package library;

import library.dao.BookDetailsDAO;
import library.dao.BookDistributorDAO;
import library.exception.BookAlreadyLentException;
import library.exception.BookNotExistException;
import library.model.BookDetails;
import library.model.BookDistributor;
import library.search.SearchCriteria;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

public class LibraryManager implements Library {

    private final static Logger logger = Logger.getLogger(LibraryManager.class.getName());

    private List<BookDistributor> bookDistributors = new ArrayList<>();
    private BookDetailsDAO bookDetailsDAO = new BookDetailsDAO();
    private BookDistributorDAO bookDistributorDAO = new BookDistributorDAO();

    @Override
    public String addNewBook(String title, String author, Integer year) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setAuthor(author);
        criteria.setTitle(title);
        criteria.setYear(year);

        String bookDetailsId = bookDetailsDAO.getBookDetailsIdByParams(criteria);
        if(isNull(bookDetailsId)) {
            BookDetails bookDetails = new BookDetails.BookDetailsBuilder()
                    .setAuthor(author)
                    .setTitle(title)
                    .setYear(year)
                    .build();
          bookDetailsId = bookDetailsDAO.addBookDetail(bookDetails);
        }
      return addBook(bookDetailsId);
    }


    /**
     * Method that creates an instance of BookDistributor,
     * which contains all information about each bookDetails in library.
     * This instance is added to all bookDetails distrobutors list
     * @param bookDetailsId
     * @return id
     */
    String addBook(String bookDetailsId) {
        BookDistributor bookDistributor = new BookDistributor(bookDetailsId);
        String id = bookDistributorDAO.addBookDistributor(bookDistributor);
        return id;
    }

    @Override
    public BookDetails getBookById(String id) throws BookNotExistException {

        if (!checkIfBookExist(id)) {
            throw new BookNotExistException("This book doesn't exist in library");
        }
        String bookDetailId = bookDistributorDAO.getBookDistributor(id).getBookDetailsId();
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
        Map<String, Set<BookDistributor>> result = bookDistributorDAO.getAllBookDistributors().stream().collect(Collectors.groupingBy(BookDistributor::getBookDetailsId, toSet()));
        result.forEach((k, v) -> {
            System.out.println("BookDetails " + bookDetailsDAO.getBookDetailsById(k).getTitle() + " "
                    + bookDetailsDAO.getBookDetailsById(k).getAuthor() + " "
                    + bookDetailsDAO.getBookDetailsById(k).getYear());
            System.out.println("Available amount in library " + v.stream().filter(bd -> !bd.isLent()).count());
            System.out.println("Lent amount " + v.stream().filter(BookDistributor::isLent).count());
        });

         }


    @Override
    public void displayBookInformation(String id) throws BookNotExistException {
        if (!checkIfBookExist(id)) {
            throw new BookNotExistException("This book doesn't exist in library");
        }

        BookDistributor bd = bookDistributorDAO.getBookDistributor(id);
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
        return bookDistributorDAO.isInLibrary(id);
        }

    /**
     * @param id
     * @return if book is lent
     */
    boolean checkIfBookIsLent(String id) {
        return bookDistributorDAO.checkIfBookIsLent(id);
    }


    @Override
    public List<BookDetails> getBookListByCriteria(SearchCriteria criteria) {
        Stream<BookDistributor> stream = bookDistributorDAO.getAllBookDistributors().stream();
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
        BookDistributor bd = bookDistributorDAO.getBookDistributor(id);

        bookDistributorDAO.removeBook(id);
        updateBookDetails();
    }

    private void updateBookDetails(){
        List<String> ids = bookDistributorDAO.getAllBookDistributors().stream().map(b -> b.getBookDetailsId()).collect(Collectors.toList());
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

        BookDistributor bd = bookDistributorDAO.getBookDistributor(id);
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

       List<BookDistributor> listOfBooks = bookDistributorDAO.getBookDistibutorsByBookDetail(detailsId);
       BookDistributor bd = getFirstAvailableBook(listOfBooks);

        if (isNull(bd)) {
            throw new BookAlreadyLentException("All books are currently lent");
        }

        bd.setLastLenderName(person);
        bd.setLent(true);

        return bookDetailsDAO.getBookDetailsById(bd.getBookDetailsId());
    }

    private BookDistributor getFirstAvailableBook(List<BookDistributor> list){
        Optional<BookDistributor> available = list.stream().filter(b -> !b.isLent()).findFirst();
        if(!isNull(available)){
          return available.get();
        }
        else{
            return null;
        }
    }

}
