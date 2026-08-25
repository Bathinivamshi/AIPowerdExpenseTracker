package com.example.expensetracker.controller;
import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.dto.ExpenseStatisticsResponse;
import com.example.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    @Operation(
            summary = "Get all expenses",
            description = "Returns all expenses"
    )
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @RequestParam(required = false)  String category,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(expenseService.getExpenses(category, min, max,sort));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long id) {

        ExpenseResponse expense =
                expenseService.getExpenseById(id);

        return ResponseEntity.ok(expense);
    }
    @GetMapping("/sort")
    public ResponseEntity<List<ExpenseResponse>> sortExpenses(
            @RequestParam String field,
            @RequestParam String direction) {

        return ResponseEntity.ok(
                expenseService.sortExpenses(field, direction)
        );
    }
    @GetMapping("/page")
    public ResponseEntity<Page<ExpenseResponse>> getExpensesPage(Pageable pageable) {
        return ResponseEntity.ok(expenseService.getExpensesWithPagination(pageable));
    }
    @Operation(
            summary = "Search expenses",
            description = "Search  all expenses"
    )
    @GetMapping("/search")
    public ResponseEntity<List<ExpenseResponse>> searchExpenses(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                expenseService.searchExpenses(keyword)
        );
    }
    @Operation(
            summary = "Get total expenses",
            description = "Returns the total amount of all expenses"
    )
    @GetMapping("/total")
    public ResponseEntity<Double> getTotalAmount() {
        return ResponseEntity.ok(
                expenseService.getTotalAmount()
        );
    }
    @GetMapping("/total/month")
    public ResponseEntity<Double> getTotalByMonth(
            @RequestParam int year,
            @RequestParam int month) {

        return ResponseEntity.ok(
                expenseService.getTotalByMonth(year, month)
        );
    }
    @GetMapping("/total/year")
    public ResponseEntity<Double> getTotalByYear(
            @RequestParam int year) {

        return ResponseEntity.ok(
                expenseService.getTotalByYear(year)
        );
    }
    @GetMapping("/average")
    public ResponseEntity<Double> getAverageAmount() {
        return ResponseEntity.ok(
                expenseService.getAverageAmount()
        );
    }
    @GetMapping("/highest")
    public ResponseEntity<ExpenseResponse> getHighestExpense() {
        return ResponseEntity.ok(
                expenseService.getHighestExpense()
        );
    }
    @GetMapping("/lowest")
    public ResponseEntity<ExpenseResponse> getLowestExpense() {
        return ResponseEntity.ok(
                expenseService.getLowestExpense()
        );
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getExpenseCount() {
        return ResponseEntity.ok(
                expenseService.getExpenseCount()
        );
    }
    @GetMapping("/count/category")
    public ResponseEntity<Long> getExpenseCountByCategory(
            @RequestParam String category) {

        return ResponseEntity.ok(
                expenseService.getExpenseCountByCategory(category)
        );
    }
    @GetMapping("/count/category/all")
    public ResponseEntity<Map<String, Long>> getExpenseCountByCategory() {

        return ResponseEntity.ok(
                expenseService.getExpenseCountByCategory()
        );
    }
    @GetMapping("/summary/monthly")
    public ResponseEntity<Map<String, Double>> getMonthlySummary(
            @RequestParam int year) {

        return ResponseEntity.ok(
                expenseService.getMonthlySummary(year)
        );
    }
    @GetMapping("/percentage/category")
    public ResponseEntity<Map<String, Double>> getCategoryPercentages() {

        return ResponseEntity.ok(
                expenseService.getCategoryPercentages()
        );
    }
    @GetMapping("/statistics")
    public ResponseEntity<ExpenseStatisticsResponse> getStatistics() {

        return ResponseEntity.ok(
                expenseService.getStatistics()
        );
    }
    @Operation(
            summary = "Create a new expense",
            description = "Creates and saves a new expense"
    )
    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@Valid @RequestBody ExpenseRequest request)
    {
        ExpenseResponse savedExpense=expenseService.addExpense(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedExpense);
    }
    @GetMapping("/total/category")
    public ResponseEntity<Map<String, Double>> getTotalAmountByCategory() {

        return ResponseEntity.ok(
                expenseService.getTotalAmountByCategory()
        );
    }
    @GetMapping("/total/date-range")
    public ResponseEntity<Double> getTotalByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {

        return ResponseEntity.ok(
                expenseService.getTotalByDateRange(start, end)
        );
    }
    @Operation(
            summary = "Update an expense",
            description = "Updates an existing expense using its ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(  @PathVariable Long id,@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse updatedExpense=expenseService.updateExpense(id, request);
      return ResponseEntity.ok(updatedExpense);
    }
    @Operation(
            summary = "Delete an expense",
            description = "Delete an existing expense using its ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

}
