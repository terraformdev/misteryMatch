package com.misterymatch.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh@appeots.com on 23/01/2018.
 */

public class TransactionHistoryModel {

    String header;
    List<TransactionHistory> transactionHistory = new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public List<TransactionHistory> getTransactionHistory() {
        return transactionHistory;
    }


    public void setTransactionHistory(List<TransactionHistory> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}
