package backend.backend.exceptions;

public class DepositNotRequiredError extends Exception {
    public DepositNotRequiredError(){
        super();
    }

    public DepositNotRequiredError(String msg){
        super(msg);
    }
}
