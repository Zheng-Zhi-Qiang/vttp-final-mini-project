package backend.backend.exceptions;

public class BadRequestError extends Exception{
    public BadRequestError(){
        super();
    }

    public BadRequestError(String msg){
        super(msg);
    }
}
