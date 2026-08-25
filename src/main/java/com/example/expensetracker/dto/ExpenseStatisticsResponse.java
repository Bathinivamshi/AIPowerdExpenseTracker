package com.example.expensetracker.dto;

public class ExpenseStatisticsResponse {

    private Double totalAmount;
    private Double averageAmount;
    private Long expenseCount;
    private ExpenseResponse highestExpense;
    private ExpenseResponse lowestExpense;

    public ExpenseStatisticsResponse() {
    }

    public ExpenseStatisticsResponse(
            Double totalAmount,
            Double averageAmount,
            Long expenseCount,
            ExpenseResponse highestExpense,
            ExpenseResponse lowestExpense) {

        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.expenseCount = expenseCount;
        this.highestExpense = highestExpense;
        this.lowestExpense = lowestExpense;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Double getAverageAmount() {
        return averageAmount;
    }

    public Long getExpenseCount() {
        return expenseCount;
    }

    public ExpenseResponse getHighestExpense() {
        return highestExpense;
    }

    public ExpenseResponse getLowestExpense() {
        return lowestExpense;
    }
}