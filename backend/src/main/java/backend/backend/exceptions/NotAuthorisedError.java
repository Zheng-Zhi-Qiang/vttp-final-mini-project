package backend.backend.exceptions;

public class NotAuthorisedError extends Exception {
    public NotAuthorisedError(){
        super();
    }

    public NotAuthorisedError(String msg){
        super(msg);
    }
}
