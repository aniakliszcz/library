package demo;

import library.Library;
import library.LibraryManager;
import library.exception.BookAlreadyLentException;
import library.exception.BookNotExistException;
import library.search.SearchCriteria;

public class LibraryDemo {

    public static void main(String[] args){

        //create new library
        Library library = new LibraryManager();

        //add book to library
        String id = library.addNewBook("Zbrodnia i kara", "Fiodor Dostojewski", 2002);
        library.addNewBook("Król Edyp", "Sofokles", 2002);
        library.addNewBook("Hamlet", "William Shakespeare", 2002);
        library.addNewBook("Odyseja", "Homer", 2002);
        library.addNewBook("Odyseja", "Homer", 2001);
        library.addNewBook("Dżuma", "Albert Camus", 2007);
        library.addNewBook("Dżuma", "Albert Camus", 2002);

        //display all books

        library.displayAllBooksInformation();

        //create criteria for searching
        SearchCriteria criteria = new SearchCriteria();
        criteria.setTitle("Odyseja");
        criteria.setAuthor("Homer");

        //lend a book
        try {
            library.lendBook(criteria, "Jan Kowalski");
        } catch (BookAlreadyLentException e) {
            System.out.println("There is no available book in library");
        } catch (BookNotExistException e) {
            System.out.println("There is no such book in library");
        }

        //remove book

        try {
            library.removeBook(id);
        } catch (BookNotExistException e) {
            System.out.println("Book doesn't exist in library");
        } catch (BookAlreadyLentException e) {
            System.out.println("Cannot remove book because is lent");
        }

    }
}
