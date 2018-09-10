package library.dao;

import library.model.BookDetails;
import library.search.SearchCriteria;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;


public class BookDetailsDAO {

    private Map<String, BookDetails> mapOfBookDetails = new HashMap<>();

    public String addBookDetail(BookDetails bookDetails){
        String id = UUID.randomUUID().toString();
        mapOfBookDetails.put(id, bookDetails);
        return id;
    }

    public BookDetails getBookDetailsById(String id){
        return mapOfBookDetails.get(id);
    }

    public List<BookDetails> getAllBookDetails(){
        return mapOfBookDetails.values().stream().collect(Collectors.toList());
    }

    public String getBookDetailsIdByParams(SearchCriteria criteria){
        Optional<Map.Entry<String, BookDetails>> result = getBookDetailsByParams(criteria);
        if(result.isPresent()){
            return result.get().getKey();
        }
        else{
            return null;
        }
    }

    public Optional<Map.Entry<String, BookDetails>> getBookDetailsByParams(SearchCriteria criteria){

        Stream<Map.Entry<String, BookDetails>> resultStream = mapOfBookDetails.entrySet().stream();

         if(!isNull(criteria.getTitle())){
           resultStream =  resultStream.filter(b -> b.getValue().getTitle().equals(criteria.getTitle()));
         }
        if(!isNull(criteria.getAuthor())){
            resultStream =  resultStream.filter(b -> b.getValue().getAuthor().equals(criteria.getAuthor()));
        }
        if(!isNull(criteria.getYear())){
            resultStream =  resultStream.filter(b -> b.getValue().getYear().equals(criteria.getYear()));
        }
        return resultStream.findFirst();
    }

    public Set<String> getKeys(){
        return mapOfBookDetails.keySet();
    }

    public void removeBookDetails(String id){
        mapOfBookDetails.remove(id);
    }
}
