package backend.backend.exceptions;

public class SQLUpdateError extends Exception{
    public SQLUpdateError(){
        super();
    }

    public SQLUpdateError(String msg){
        super(msg);
    }
}
