package com.frontend.libertywallet.Entity;

import java.util.Date;
import java.util.UUID;

public class NotificationItem {

    private String id;

    private String name;

    private Date date;

    private String monthSum;

    private String numberOfMonths;

    private String currentNumberOfMonths;

    private String currentSum;

    private String generalSum;

    private boolean completed;

    public NotificationItem(String id, String name, Date date, String monthSum,
                            String numberOfMonths, String currentNumberOfMonths,
                            String currentSum, String generalSum, boolean completed) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.monthSum = monthSum;
        this.numberOfMonths = numberOfMonths;
        this.currentNumberOfMonths = currentNumberOfMonths;
        this.currentSum = currentSum;
        this.generalSum = generalSum;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getMonthSum() {
        return monthSum;
    }

    public String getNumberOfMonths() {
        return numberOfMonths;
    }

    public String getCurrentNumberOfMonths() {
        return currentNumberOfMonths;
    }

    public String getCurrentSum() {
        return currentSum;
    }

    public String getGeneralSum() {
        return generalSum;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMonthSum(String monthSum) {
        this.monthSum = monthSum;
    }

    public void setNumberOfMonths(String numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    public void setCurrentNumberOfMonths(String currentNumberOfMonths) {
        this.currentNumberOfMonths = currentNumberOfMonths;
    }

    public void setCurrentSum(String currentSum) {
        this.currentSum = currentSum;
    }

    public void setGeneralSum(String generalSum) {
        this.generalSum = generalSum;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
