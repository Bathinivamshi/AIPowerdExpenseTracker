package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.dto.ExpenseStatisticsResponse;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private ExpenseService expenseService;

    private User user;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        expenseService =
                new ExpenseService(
                        expenseRepository,
                        userRepository
                );

        user = new User();
        user.setEmail("test@gmail.com");

        SecurityContextHolder.setContext(securityContext);
    }

    // =========================================================
    // HELPER METHOD
    // =========================================================

    private void mockCurrentUser() {

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));
    }

    private Expense createExpense(
            Long id,
            String title,
            Double amount,
            String category) {

        Expense expense = new Expense();

        expense.setId(id);
        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setUser(user);

        expense.setCreatedDate(
                LocalDateTime.of(2026, 8, 20, 10, 0)
        );

        expense.setUpdatedAt(
                LocalDateTime.of(2026, 8, 20, 10, 0)
        );

        return expense;
    }

    // =========================================================
    // 1. ADD EXPENSE
    // =========================================================

    @Test
    void addExpense_shouldSaveAndReturnExpense() {

        mockCurrentUser();

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("Lunch");
        request.setAmount(250.0);
        request.setCategory("Food");

        Expense savedExpense =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(savedExpense);

        ExpenseResponse result =
                expenseService.addExpense(request);

        assertEquals(1L, result.getId());
        assertEquals("Lunch", result.getTitle());
        assertEquals(250.0, result.getAmount());
        assertEquals("Food", result.getCategory());

        verify(expenseRepository)
                .save(any(Expense.class));
    }

    // =========================================================
    // 2. ADD EXPENSE - VERIFY DATA
    // =========================================================

    @Test
    void addExpense_shouldSendCorrectDataToRepository() {

        mockCurrentUser();

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("Pizza");
        request.setAmount(450.0);
        request.setCategory("Food");

        Expense savedExpense =
                createExpense(
                        1L,
                        "Pizza",
                        450.0,
                        "Food"
                );

        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(savedExpense);

        expenseService.addExpense(request);

        ArgumentCaptor<Expense> captor =
                ArgumentCaptor.forClass(Expense.class);

        verify(expenseRepository)
                .save(captor.capture());

        Expense captured =
                captor.getValue();

        assertEquals("Pizza", captured.getTitle());
        assertEquals(450.0, captured.getAmount());
        assertEquals("Food", captured.getCategory());
        assertEquals(user, captured.getUser());
    }

    // =========================================================
    // 3. GET EXPENSE BY ID
    // =========================================================

    @Test
    void getExpenseById_shouldReturnExpense_whenExists() {

        mockCurrentUser();

        Expense expense =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        when(expenseRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(expense));

        ExpenseResponse result =
                expenseService.getExpenseById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Lunch", result.getTitle());
        assertEquals(250.0, result.getAmount());
        assertEquals("Food", result.getCategory());
    }

    // =========================================================
    // 4. GET EXPENSE BY ID - NOT FOUND
    // =========================================================

    @Test
    void getExpenseById_shouldThrowException_whenNotFound() {

        mockCurrentUser();

        when(expenseRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.getExpenseById(99L)
        );
    }

    // =========================================================
    // 5. UPDATE EXPENSE
    // =========================================================

    @Test
    void updateExpense_shouldUpdateAndReturnExpense() {

        mockCurrentUser();

        Expense existingExpense =
                createExpense(
                        1L,
                        "Lunch",
                        200.0,
                        "Food"
                );

        ExpenseRequest request =
                new ExpenseRequest();

        request.setTitle("Dinner");
        request.setAmount(500.0);
        request.setCategory("Food");

        when(expenseRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(existingExpense));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        ExpenseResponse result =
                expenseService.updateExpense(
                        1L,
                        request
                );

        assertEquals("Dinner", result.getTitle());
        assertEquals(500.0, result.getAmount());
        assertEquals("Food", result.getCategory());

        verify(expenseRepository)
                .save(existingExpense);
    }

    // =========================================================
    // 6. UPDATE - NOT FOUND
    // =========================================================

    @Test
    void updateExpense_shouldThrowException_whenNotFound() {

        mockCurrentUser();

        ExpenseRequest request =
                new ExpenseRequest();

        request.setTitle("Dinner");
        request.setAmount(500.0);
        request.setCategory("Food");

        when(expenseRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.updateExpense(
                        99L,
                        request
                )
        );
    }

    // =========================================================
    // 7. DELETE EXPENSE
    // =========================================================

    @Test
    void deleteExpense_shouldDelete_whenExists() {

        mockCurrentUser();

        when(expenseRepository.existsByIdAndUser(1L, user))
                .thenReturn(true);

        expenseService.deleteExpense(1L);

        verify(expenseRepository)
                .deleteByIdAndUser(1L, user);
    }

    // =========================================================
    // 8. DELETE - NOT FOUND
    // =========================================================

    @Test
    void deleteExpense_shouldThrowException_whenNotFound() {

        mockCurrentUser();

        when(expenseRepository.existsByIdAndUser(99L, user))
                .thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> expenseService.deleteExpense(99L)
        );

        verify(
                expenseRepository,
                never()
        ).deleteByIdAndUser(99L, user);
    }

    // =========================================================
    // 9. GET EXPENSES
    // =========================================================

    @Test
    void getExpenses_shouldReturnUserExpenses() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Bus",
                        50.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        List<ExpenseResponse> result =
                expenseService.getExpenses(
                        null,
                        null,
                        null,
                        null
                );

        assertEquals(2, result.size());

        // Default sorting is ASC by amount
        assertEquals(
                50.0,
                result.get(0).getAmount()
        );

        assertEquals(
                250.0,
                result.get(1).getAmount()
        );
    }

    // =========================================================
    // 10. GET EXPENSES - CATEGORY
    // =========================================================

    @Test
    void getExpenses_shouldFilterByCategory() {

        mockCurrentUser();

        Expense expense =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(expense));

        List<ExpenseResponse> result =
                expenseService.getExpenses(
                        "Food",
                        null,
                        null,
                        null
                );

        assertEquals(1, result.size());
        assertEquals(
                "Food",
                result.get(0).getCategory()
        );
    }

    // =========================================================
    // 11. GET EXPENSES - AMOUNT RANGE
    // =========================================================

    @Test
    void getExpenses_shouldFilterByAmountRange() {

        mockCurrentUser();

        Expense expense =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(expense));

        List<ExpenseResponse> result =
                expenseService.getExpenses(
                        null,
                        100.0,
                        500.0,
                        null
                );

        assertEquals(1, result.size());
        assertEquals(
                250.0,
                result.get(0).getAmount()
        );
    }

    // =========================================================
    // 12. GET EXPENSES - DESCENDING
    // =========================================================

    @Test
    void getExpenses_shouldSortDescending() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Shopping",
                        500.0,
                        "Shopping"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        List<ExpenseResponse> result =
                expenseService.getExpenses(
                        null,
                        null,
                        null,
                        "DESC"
                );

        assertEquals(
                500.0,
                result.get(0).getAmount()
        );

        assertEquals(
                250.0,
                result.get(1).getAmount()
        );
    }

    // =========================================================
    // 13. GET EXPENSES - INVALID RANGE
    // =========================================================

    @Test
    void getExpenses_shouldThrowException_whenMinGreaterThanMax() {

        // Mock the currently logged-in user
        mockCurrentUser();

        // Verify that IllegalArgumentException is thrown
        // when minimum amount is greater than maximum amount
        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.getExpenses(
                        null,      // category
                        1000.0,    // min
                        100.0,     // max
                        null       // sort
                )
        );
    }
    // =========================================================
    // 14. SORT EXPENSES - ASC
    // =========================================================

    @Test
    void sortExpenses_shouldReturnAscendingOrder() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        100.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Shopping",
                        500.0,
                        "Shopping"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        new java.util.ArrayList<>(List.of(
                                expense1,
                                expense2
                        ))
                );

        List<ExpenseResponse> result =
                expenseService.sortExpenses(
                        "amount",
                        "asc"
                );

        assertEquals(
                100.0,
                result.get(0).getAmount()
        );

        assertEquals(
                500.0,
                result.get(1).getAmount()
        );
    }

    // =========================================================
    // 15. SORT EXPENSES - DESC
    // =========================================================

    @Test
    void sortExpenses_shouldReturnDescendingOrder() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        100.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Shopping",
                        500.0,
                        "Shopping"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        new java.util.ArrayList<>(List.of(
                                expense1,
                                expense2
                        ))
                );

        List<ExpenseResponse> result =
                expenseService.sortExpenses(
                        "amount",
                        "desc"
                );

        assertEquals(
                500.0,
                result.get(0).getAmount()
        );

        assertEquals(
                100.0,
                result.get(1).getAmount()
        );
    }

    // =========================================================
    // 16. SEARCH
    // =========================================================

    @Test
    void searchExpenses_shouldReturnMatchingExpenses() {

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        User mockedUser = org.mockito.Mockito.mock(User.class);

        when(mockedUser.getId())
                .thenReturn(1L);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(mockedUser));

        Expense expense =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        expense.setUser(mockedUser);

        when(
                expenseRepository
                        .findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                                "food",
                                "food"
                        )
        ).thenReturn(
                new java.util.ArrayList<>(
                        List.of(expense)
                )
        );

        List<ExpenseResponse> result =
                expenseService.searchExpenses("food");

        assertEquals(1, result.size());

        assertEquals(
                "Lunch",
                result.get(0).getTitle()
        );

        assertEquals(
                250.0,
                result.get(0).getAmount()
        );

        assertEquals(
                "Food",
                result.get(0).getCategory()
        );
    }
    // =========================================================
    // 17. SEARCH - NO RESULTS
    // =========================================================

    @Test
    void searchExpenses_shouldReturnEmptyList_whenNoMatch() {

        mockCurrentUser();

        when(
                expenseRepository
                        .findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                                "xyz",
                                "xyz"
                        )
        ).thenReturn(new java.util.ArrayList<>());

        List<ExpenseResponse> result =
                expenseService.searchExpenses("xyz");

        assertTrue(result.isEmpty());
    }

    // =========================================================
    // 18. PAGINATION
    // =========================================================

    @Test
    void getExpensesWithPagination_shouldReturnPage() {

        mockCurrentUser();

        Expense expense =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Expense> expensePage =
                new PageImpl<>(
                        List.of(expense),
                        pageable,
                        1
                );

        when(
                expenseRepository.findByUserId(
                        user.getId(),
                        pageable
                )
        ).thenReturn(expensePage);

        Page<ExpenseResponse> result =
                expenseService.getExpensesWithPagination(pageable);

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertEquals(
                "Lunch",
                result.getContent()
                        .get(0)
                        .getTitle()
        );
    }

    // =========================================================
    // 19. TOTAL AMOUNT
    // =========================================================

    @Test
    void getTotalAmount_shouldReturnTotal() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Bus",
                        50.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        Double result =
                expenseService.getTotalAmount();

        assertEquals(
                300.0,
                result
        );
    }

    // =========================================================
    // 20. TOTAL BY CATEGORY
    // =========================================================

    @Test
    void getTotalAmountByCategory_shouldReturnTotals() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        500.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Dinner",
                        1000.0,
                        "Food"
                );

        Expense expense3 =
                createExpense(
                        3L,
                        "Bus",
                        200.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2,
                                expense3
                        )
                );

        Map<String, Double> result =
                expenseService.getTotalAmountByCategory();

        assertEquals(
                1500.0,
                result.get("Food")
        );

        assertEquals(
                200.0,
                result.get("Transport")
        );
    }

    // =========================================================
    // 21. TOTAL BY DATE RANGE
    // =========================================================

    @Test
    void getTotalByDateRange_shouldReturnTotal() {

        mockCurrentUser();

        Expense expense =
                createExpense(
                        1L,
                        "Lunch",
                        500.0,
                        "Food"
                );

        expense.setCreatedDate(
                LocalDateTime.of(
                        2026,
                        8,
                        15,
                        10,
                        0
                )
        );

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(expense));

        LocalDateTime start =
                LocalDateTime.of(
                        2026,
                        8,
                        1,
                        0,
                        0
                );

        LocalDateTime end =
                LocalDateTime.of(
                        2026,
                        8,
                        31,
                        23,
                        59
                );

        Double result =
                expenseService.getTotalByDateRange(
                        start,
                        end
                );

        assertEquals(
                500.0,
                result
        );
    }

    // =========================================================
    // 22. DATE RANGE INVALID
    // =========================================================

    @Test
    void getTotalByDateRange_shouldThrowException_whenInvalid() {

        LocalDateTime start =
                LocalDateTime.of(
                        2026,
                        8,
                        31,
                        0,
                        0
                );

        LocalDateTime end =
                LocalDateTime.of(
                        2026,
                        8,
                        1,
                        0,
                        0
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> expenseService.getTotalByDateRange(
                        start,
                        end
                )
        );
    }

    // =========================================================
    // 23. TOTAL BY MONTH
    // =========================================================

    @Test
    void getTotalByMonth_shouldReturnTotal() {

        mockCurrentUser();

        Expense expense =
                createExpense(
                        1L,
                        "Lunch",
                        500.0,
                        "Food"
                );

        expense.setCreatedDate(
                LocalDateTime.of(
                        2026,
                        8,
                        15,
                        10,
                        0
                )
        );

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(expense));

        Double result =
                expenseService.getTotalByMonth(
                        2026,
                        8
                );

        assertEquals(
                500.0,
                result
        );
    }

    // =========================================================
    // 24. TOTAL BY YEAR
    // =========================================================

    @Test
    void getTotalByYear_shouldReturnTotal() {

        mockCurrentUser();

        Expense expense =
                createExpense(
                        1L,
                        "Laptop",
                        75000.0,
                        "Shopping"
                );

        expense.setCreatedDate(
                LocalDateTime.of(
                        2026,
                        5,
                        10,
                        10,
                        0
                )
        );

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of(expense));

        Double result =
                expenseService.getTotalByYear(2026);

        assertEquals(
                75000.0,
                result
        );
    }

    // =========================================================
    // 25. AVERAGE
    // =========================================================

    @Test
    void getAverageAmount_shouldReturnAverage() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        200.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Dinner",
                        400.0,
                        "Food"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        Double result =
                expenseService.getAverageAmount();

        assertEquals(
                300.0,
                result
        );
    }

    // =========================================================
    // 26. HIGHEST EXPENSE
    // =========================================================

    @Test
    void getHighestExpense_shouldReturnHighestExpense() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Laptop",
                        75000.0,
                        "Shopping"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        ExpenseResponse result =
                expenseService.getHighestExpense();

        assertEquals(
                "Laptop",
                result.getTitle()
        );

        assertEquals(
                75000.0,
                result.getAmount()
        );
    }

    // =========================================================
    // 27. HIGHEST - NO EXPENSES
    // =========================================================

    @Test
    void getHighestExpense_shouldThrowException_whenNoExpenses() {

        mockCurrentUser();

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of());

        assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.getHighestExpense()
        );
    }

    // =========================================================
    // 28. LOWEST EXPENSE
    // =========================================================

    @Test
    void getLowestExpense_shouldReturnLowestExpense() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Bus",
                        50.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        ExpenseResponse result =
                expenseService.getLowestExpense();

        assertEquals(
                "Bus",
                result.getTitle()
        );

        assertEquals(
                50.0,
                result.getAmount()
        );
    }

    // =========================================================
    // 29. LOWEST - NO EXPENSES
    // =========================================================

    @Test
    void getLowestExpense_shouldThrowException_whenNoExpenses() {

        mockCurrentUser();

        when(expenseRepository.findByUser(user))
                .thenReturn(List.of());

        assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.getLowestExpense()
        );
    }

    // =========================================================
    // 30. EXPENSE COUNT
    // =========================================================

    @Test
    void getExpenseCount_shouldReturnCount() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Bus",
                        50.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        long result =
                expenseService.getExpenseCount();

        assertEquals(
                2,
                result
        );
    }

    // =========================================================
    // 31. COUNT BY CATEGORY
    // =========================================================

    @Test
    void getExpenseCountByCategory_shouldReturnCount() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Dinner",
                        500.0,
                        "Food"
                );

        Expense expense3 =
                createExpense(
                        3L,
                        "Bus",
                        50.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2,
                                expense3
                        )
                );

        long result =
                expenseService.getExpenseCountByCategory(
                        "Food"
                );

        assertEquals(
                2,
                result
        );
    }

    // =========================================================
    // 32. COUNT ALL CATEGORIES
    // =========================================================

    @Test
    void getExpenseCountByCategory_shouldReturnAllCounts() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        250.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Dinner",
                        500.0,
                        "Food"
                );

        Expense expense3 =
                createExpense(
                        3L,
                        "Bus",
                        50.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2,
                                expense3
                        )
                );

        Map<String, Long> result =
                expenseService.getExpenseCountByCategory();

        assertEquals(
                2L,
                result.get("Food")
        );

        assertEquals(
                1L,
                result.get("Transport")
        );
    }

    // =========================================================
    // 33. MONTHLY SUMMARY
    // =========================================================

    @Test
    void getMonthlySummary_shouldReturnMonthlyTotals() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        500.0,
                        "Food"
                );

        expense1.setCreatedDate(
                LocalDateTime.of(
                        2026,
                        8,
                        10,
                        10,
                        0
                )
        );

        Expense expense2 =
                createExpense(
                        2L,
                        "Dinner",
                        1000.0,
                        "Food"
                );

        expense2.setCreatedDate(
                LocalDateTime.of(
                        2026,
                        8,
                        20,
                        10,
                        0
                )
        );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        Map<String, Double> result =
                expenseService.getMonthlySummary(
                        2026
                );

        assertEquals(
                1500.0,
                result.get("August")
        );
    }

    // =========================================================
    // 34. CATEGORY PERCENTAGES
    // =========================================================

    @Test
    void getCategoryPercentages_shouldReturnPercentages() {

        mockCurrentUser();

        Expense food =
                createExpense(
                        1L,
                        "Lunch",
                        500.0,
                        "Food"
                );

        Expense transport =
                createExpense(
                        2L,
                        "Bus",
                        500.0,
                        "Transport"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                food,
                                transport
                        )
                );

        Map<String, Double> result =
                expenseService.getCategoryPercentages();

        assertEquals(
                50.0,
                result.get("Food")
        );

        assertEquals(
                50.0,
                result.get("Transport")
        );
    }

    // =========================================================
    // 35. STATISTICS
    // =========================================================

    @Test
    void getStatistics_shouldReturnStatistics() {

        mockCurrentUser();

        Expense expense1 =
                createExpense(
                        1L,
                        "Lunch",
                        200.0,
                        "Food"
                );

        Expense expense2 =
                createExpense(
                        2L,
                        "Laptop",
                        1000.0,
                        "Shopping"
                );

        when(expenseRepository.findByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );

        ExpenseStatisticsResponse result =
                expenseService.getStatistics();

        assertEquals(
                1200.0,
                result.getTotalAmount()
        );

        assertEquals(
                600.0,
                result.getAverageAmount()
        );

        assertEquals(
                2L,
                result.getExpenseCount()
        );

        assertNotNull(
                result.getHighestExpense()
        );

        assertNotNull(
                result.getLowestExpense()
        );
    }

    // =========================================================
    // 36. PAGINATION EMPTY
    // =========================================================

    @Test
    void getExpensesWithPagination_shouldReturnEmptyPage() {

        mockCurrentUser();

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Expense> emptyPage =
                new PageImpl<>(
                        List.of(),
                        pageable,
                        0
                );

        when(
                expenseRepository.findByUserId(
                        user.getId(),
                        pageable
                )
        ).thenReturn(emptyPage);

        Page<ExpenseResponse> result =
                expenseService.getExpensesWithPagination(pageable);

        assertEquals(
                0,
                result.getTotalElements()
        );

        assertTrue(
                result.getContent().isEmpty()
        );
    }

    // =========================================================
    // 37. UPDATE - CREATED DATE REMAINS
    // =========================================================

    @Test
    void updateExpense_shouldKeepCreatedDateUnchanged() {

        mockCurrentUser();

        LocalDateTime originalCreatedDate =
                LocalDateTime.of(
                        2026,
                        8,
                        1,
                        10,
                        0
                );

        Expense existingExpense =
                createExpense(
                        1L,
                        "Lunch",
                        200.0,
                        "Food"
                );

        existingExpense.setCreatedDate(
                originalCreatedDate
        );

        ExpenseRequest request =
                new ExpenseRequest();

        request.setTitle("Dinner");
        request.setAmount(500.0);
        request.setCategory("Food");

        when(
                expenseRepository.findByIdAndUser(
                        1L,
                        user
                )
        ).thenReturn(
                Optional.of(existingExpense)
        );

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        ExpenseResponse result =
                expenseService.updateExpense(
                        1L,
                        request
                );

        assertEquals(
                originalCreatedDate,
                result.getCreatedDate()
        );
    }

    // =========================================================
    // 38. UPDATE - UPDATED DATE CHANGES
    // =========================================================

    @Test
    void updateExpense_shouldChangeUpdatedAt() {

        mockCurrentUser();

        LocalDateTime oldUpdatedAt =
                LocalDateTime.of(
                        2026,
                        8,
                        20,
                        10,
                        0
                );

        Expense existingExpense =
                createExpense(
                        1L,
                        "Lunch",
                        200.0,
                        "Food"
                );

        existingExpense.setUpdatedAt(
                oldUpdatedAt
        );

        ExpenseRequest request =
                new ExpenseRequest();

        request.setTitle("Dinner");
        request.setAmount(500.0);
        request.setCategory("Food");

        when(
                expenseRepository.findByIdAndUser(
                        1L,
                        user
                )
        ).thenReturn(
                Optional.of(existingExpense)
        );

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        ExpenseResponse result =
                expenseService.updateExpense(
                        1L,
                        request
                );

        assertNotEquals(
                oldUpdatedAt,
                result.getUpdatedAt()
        );
    }
}