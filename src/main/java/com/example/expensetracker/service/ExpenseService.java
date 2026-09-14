package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.dto.ExpenseStatisticsResponse;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository) {

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET CURRENT LOGGED-IN USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // =========================================================
    // ADD EXPENSE
    // =========================================================

    public ExpenseResponse addExpense(ExpenseRequest request) {

        User user = getCurrentUser();

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());

        // Connect expense to logged-in user
        expense.setUser(user);

        Expense savedExpense =
                expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

    // =========================================================
    // MAP ENTITY TO DTO
    // =========================================================

    private ExpenseResponse mapToResponse(Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getCreatedDate(),
                expense.getUpdatedAt()
        );
    }

    // =========================================================
    // GET EXPENSES
    // =========================================================

    public List<ExpenseResponse> getExpenses(
            String category,
            Double min,
            Double max,
            String sort) {
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException(
                    "Minimum amount cannot be greater than maximum amount"
            );
        }

        User user = getCurrentUser();

        List<Expense> expenses =
                new ArrayList<>(
                        expenseRepository.findByUser(user)
                );

        if (category != null) {

            expenses.removeIf(expense ->
                    !expense.getCategory()
                            .equalsIgnoreCase(category)
            );
        }

        if (min != null) {

            expenses.removeIf(expense ->
                    expense.getAmount() < min
            );
        }

        if (max != null) {

            expenses.removeIf(expense ->
                    expense.getAmount() > max
            );
        }

        if ("DESC".equalsIgnoreCase(sort)) {

            expenses.sort(
                    Comparator.comparing(
                            Expense::getAmount
                    ).reversed()
            );

        } else {

            expenses.sort(
                    Comparator.comparing(
                            Expense::getAmount
                    )
            );
        }

        return expenses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET EXPENSE BY ID
    // =========================================================

    public ExpenseResponse getExpenseById(Long id) {

        User user = getCurrentUser();

        Expense expense =
                expenseRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ExpenseNotFoundException(
                                        "Expense not found"
                                ));

        return mapToResponse(expense);
    }

    // =========================================================
    // UPDATE EXPENSE
    // =========================================================

    public ExpenseResponse updateExpense(
            Long id,
            ExpenseRequest request) {

        User user = getCurrentUser();

        Expense existingExpense =
                expenseRepository
                        .findByIdAndUser(id, user)
                        .orElseThrow(() ->
                                new ExpenseNotFoundException(
                                        "Expense not found"
                                ));

        existingExpense.setAmount(request.getAmount());
        existingExpense.setCategory(request.getCategory());
        existingExpense.setTitle(request.getTitle());

        existingExpense.setUpdatedAt(
                LocalDateTime.now()
        );

        Expense updatedExpense =
                expenseRepository.save(existingExpense);

        return mapToResponse(updatedExpense);
    }

    // =========================================================
    // DELETE EXPENSE
    // =========================================================

    public void deleteExpense(Long id) {

        User user = getCurrentUser();

        if (!expenseRepository
                .existsByIdAndUser(id, user)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Expense not found"
            );
        }

        expenseRepository.deleteByIdAndUser(id, user);
    }

    // =========================================================
    // SORT EXPENSES
    // =========================================================

    public List<ExpenseResponse> sortExpenses(
            String field,
            String direction) {

        User user = getCurrentUser();

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(field).ascending()
                : Sort.by(field).descending();

        expenses.sort(
                (e1, e2) -> {

                    if (field.equals("amount")) {

                        int result =
                                e1.getAmount()
                                        .compareTo(e2.getAmount());

                        return direction.equalsIgnoreCase("asc")
                                ? result
                                : -result;
                    }

                    return 0;
                }
        );

        return expenses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // PAGINATION
    // =========================================================

    public Page<ExpenseResponse> getExpensesWithPagination(Pageable pageable) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Expense> expenses =
                expenseRepository.findByUserId(user.getId(), pageable);

        return expenses.map(this::mapToResponse);
    }

    // =========================================================
    // SEARCH
    // =========================================================

    public List<ExpenseResponse> searchExpenses(
            String keyword) {

        User user = getCurrentUser();

        List<Expense> expenses =
                expenseRepository
                        .findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                                keyword,
                                keyword
                        );

        // Keep only current user's expenses
        expenses.removeIf(
                expense -> !expense.getUser()
                        .getId()
                        .equals(user.getId())
        );

        return expenses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // TOTAL AMOUNT
    // =========================================================

    public Double getTotalAmount() {

        User user = getCurrentUser();

        return expenseRepository
                .findByUser(user)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // =========================================================
    // TOTAL BY CATEGORY
    // =========================================================

    public Map<String, Double> getTotalAmountByCategory() {

        User user = getCurrentUser();

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Map<String, Double> totals =
                new HashMap<>();

        for (Expense expense : expenses) {

            totals.merge(
                    expense.getCategory(),
                    expense.getAmount(),
                    Double::sum
            );
        }

        return totals;
    }

    // =========================================================
    // TOTAL BY DATE RANGE
    // =========================================================

    public Double getTotalByDateRange(
            LocalDateTime start,
            LocalDateTime end) {

        if (start.isAfter(end)) {

            throw new IllegalArgumentException(
                    "Start date must be before end date"
            );
        }

        User user = getCurrentUser();

        return expenseRepository
                .findByUser(user)
                .stream()
                .filter(e ->
                        !e.getCreatedDate().isBefore(start)
                                && !e.getCreatedDate().isAfter(end)
                )
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // =========================================================
    // TOTAL BY MONTH
    // =========================================================

    public Double getTotalByMonth(
            int year,
            int month) {

        LocalDateTime start =
                LocalDateTime.of(
                        year,
                        month,
                        1,
                        0,
                        0
                );

        LocalDateTime end =
                start.plusMonths(1);

        User user = getCurrentUser();

        return expenseRepository
                .findByUser(user)
                .stream()
                .filter(e ->
                        !e.getCreatedDate().isBefore(start)
                                && e.getCreatedDate().isBefore(end)
                )
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // =========================================================
    // TOTAL BY YEAR
    // =========================================================

    public Double getTotalByYear(int year) {

        LocalDateTime start =
                LocalDateTime.of(
                        year,
                        1,
                        1,
                        0,
                        0
                );

        LocalDateTime end =
                start.plusYears(1);

        User user = getCurrentUser();

        return expenseRepository
                .findByUser(user)
                .stream()
                .filter(e ->
                        !e.getCreatedDate().isBefore(start)
                                && e.getCreatedDate().isBefore(end)
                )
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // =========================================================
    // AVERAGE
    // =========================================================

    public Double getAverageAmount() {

        User user = getCurrentUser();

        return expenseRepository
                .findByUser(user)
                .stream()
                .mapToDouble(Expense::getAmount)
                .average()
                .orElse(0.0);
    }

    // =========================================================
    // HIGHEST EXPENSE
    // =========================================================

    public ExpenseResponse getHighestExpense() {

        User user = getCurrentUser();

        Expense expense =
                expenseRepository
                        .findByUser(user)
                        .stream()
                        .max(Comparator.comparing(
                                Expense::getAmount
                        ))
                        .orElseThrow(() ->
                                new ExpenseNotFoundException(
                                        "No expenses found"
                                ));

        return mapToResponse(expense);
    }

    // =========================================================
    // LOWEST EXPENSE
    // =========================================================

    public ExpenseResponse getLowestExpense() {

        User user = getCurrentUser();

        Expense expense =
                expenseRepository
                        .findByUser(user)
                        .stream()
                        .min(Comparator.comparing(
                                Expense::getAmount
                        ))
                        .orElseThrow(() ->
                                new ExpenseNotFoundException(
                                        "No expenses found"
                                ));

        return mapToResponse(expense);
    }

    // =========================================================
    // EXPENSE COUNT
    // =========================================================

    public long getExpenseCount() {

        User user = getCurrentUser();

        return expenseRepository
                .findByUser(user)
                .size();
    }

    // =========================================================
    // COUNT BY CATEGORY
    // =========================================================

    public long getExpenseCountByCategory(
            String category) {

        User user = getCurrentUser();

        return expenseRepository
                .findByUser(user)
                .stream()
                .filter(e ->
                        e.getCategory()
                                .equalsIgnoreCase(category)
                )
                .count();
    }

    // =========================================================
    // COUNT BY ALL CATEGORIES
    // =========================================================

    public Map<String, Long> getExpenseCountByCategory() {

        User user = getCurrentUser();

        Map<String, Long> counts =
                new HashMap<>();

        for (Expense expense :
                expenseRepository.findByUser(user)) {

            counts.merge(
                    expense.getCategory(),
                    1L,
                    Long::sum
            );
        }

        return counts;
    }

    // =========================================================
    // MONTHLY SUMMARY
    // =========================================================

    public Map<String, Double> getMonthlySummary(
            int year) {

        User user = getCurrentUser();

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Map<String, Double> summary =
                new LinkedHashMap<>();

        String[] months = {
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        };

        for (Expense expense : expenses) {

            if (expense.getCreatedDate()
                    .getYear() == year) {

                int month =
                        expense.getCreatedDate()
                                .getMonthValue();

                summary.merge(
                        months[month - 1],
                        expense.getAmount(),
                        Double::sum
                );
            }
        }

        return summary;
    }

    // =========================================================
    // CATEGORY PERCENTAGES
    // =========================================================

    public Map<String, Double> getCategoryPercentages() {

        Map<String, Double> categoryTotals =
                getTotalAmountByCategory();

        Double total =
                getTotalAmount();

        Map<String, Double> percentages =
                new LinkedHashMap<>();

        if (total == 0) {
            return percentages;
        }

        for (Map.Entry<String, Double> entry :
                categoryTotals.entrySet()) {

            double percentage =
                    (entry.getValue() / total) * 100;

            percentages.put(
                    entry.getKey(),
                    percentage
            );
        }

        return percentages;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    public ExpenseStatisticsResponse getStatistics() {

        Double totalAmount =
                getTotalAmount();

        Double averageAmount =
                getAverageAmount();

        Long expenseCount =
                getExpenseCount();

        ExpenseResponse highestExpense =
                getHighestExpense();

        ExpenseResponse lowestExpense =
                getLowestExpense();

        return new ExpenseStatisticsResponse(
                totalAmount,
                averageAmount,
                expenseCount,
                highestExpense,
                lowestExpense
        );
    }
}