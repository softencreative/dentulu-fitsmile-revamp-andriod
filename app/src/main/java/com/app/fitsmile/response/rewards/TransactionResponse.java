package com.app.fitsmile.response.rewards;

import java.util.ArrayList;

public class TransactionResponse
{
    private ArrayList<TransactionResult> transactions;

    private String status;

    private String limit_reach;

    public ArrayList<TransactionResult> getTransactions ()
    {
        return transactions;
    }

    public void setTransactions ( ArrayList<TransactionResult> transactions)
    {
        this.transactions = transactions;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getLimit_reach ()
    {
        return limit_reach;
    }

    public void setLimit_reach (String limit_reach)
    {
        this.limit_reach = limit_reach;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [transactions = "+transactions+", status = "+status+", limit_reach = "+limit_reach+"]";
    }
}

