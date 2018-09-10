package library.dao;

import library.model.BookDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookDistributorDAO {

    private Map<String, BookDistributor> mapOfBook = new HashMap<>();

    public List<BookDistributor> getAllBookDistributors(){
        return mapOfBook.values().stream().collect(Collectors.toList());
    }

    public String addBookDistributor(BookDistributor bookDistributor){
        String id = UUID.randomUUID().toString();
        mapOfBook.put(id, bookDistributor);
        return id;
    }

    public BookDistributor getBookDistributor(String id){
        return mapOfBook.get(id);
    }

    public boolean isInLibrary(String id){
        return mapOfBook.containsKey(id);
    }

    public boolean checkIfBookIsLent(String id){
        return mapOfBook.get(id).isLent();
    }

    public void removeBook(String id){
        mapOfBook.remove(id);
    }

    public List<BookDistributor> getBookDistibutorsByBookDetail(String bookDetailsId){
       return mapOfBook.entrySet().stream().filter(b -> b.getValue().getBookDetailsId().equals(bookDetailsId))
               .map(k -> k.getValue()).collect(Collectors.toList());

    }

}
