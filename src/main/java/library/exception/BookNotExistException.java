package library.exception;

public class BookNotExistException extends Exception {

    public BookNotExistException(String message){
        super(message);
    }
}
