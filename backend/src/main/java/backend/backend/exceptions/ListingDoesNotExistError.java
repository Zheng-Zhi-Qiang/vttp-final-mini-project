package backend.backend.exceptions;

public class ListingDoesNotExistError extends Exception {
    public ListingDoesNotExistError(){
        super();
    }

    public ListingDoesNotExistError(String msg){
        super(msg);
    }
}
