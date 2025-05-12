package com.frontend.libertywallet.Entity;

import java.util.Date;

public class GraphItem {
    public String getSum() {
        return sum;
    }

    public Date getDate() {
        return date;
    }

    private String sum;

    public void setSum(String sum) {
        this.sum = sum;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private Date date;
}
