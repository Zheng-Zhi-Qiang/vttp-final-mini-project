package backend.backend.repos;

import static backend.backend.repos.Queries.*;

import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import backend.backend.exceptions.SQLInsertionError;
import backend.backend.models.Transaction;

@Repository
public class TransactionRepo {
    
    @Autowired
    private JdbcTemplate template;


    public void insertTransaction(Transaction transaction) throws SQLInsertionError {
        int result = template.update(SQL_INSERT_TRANSACTION, transaction.getListingId(), transaction.getPayee(), transaction.getPayer(), transaction.getAmount());
        if (result < 1) {
            throw new SQLInsertionError("Failed to create transaction");
        }
    }

    public List<Transaction> getTransactionsByUserId(String userId){
        SqlRowSet rs = template.queryForRowSet(SQL_GET_TRANSACTIONS_BY_USER_ID, userId, userId);
        List<Transaction> transactions = new LinkedList<>();
        while (rs.next()){
            transactions.add(Transaction.toTransaction(rs));
        }
        return transactions;
    }
}
