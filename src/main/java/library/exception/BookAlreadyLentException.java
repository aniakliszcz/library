package library.exception;

public class BookAlreadyLentException extends Exception{

    public BookAlreadyLentException(String message){
        super(message);
    }
}
