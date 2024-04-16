package backend.backend.exceptions;

public class SQLInsertionError extends Exception {
    public SQLInsertionError(){
        super();
    }

    public SQLInsertionError(String msg){
        super(msg);
    }
}
