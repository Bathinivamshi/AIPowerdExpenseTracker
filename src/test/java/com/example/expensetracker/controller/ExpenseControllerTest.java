package com.example.expensetracker.controller;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.dto.ExpenseStatisticsResponse;
import com.example.expensetracker.exception.ExpenseNotFoundException;
import com.example.expensetracker.exception.GlobalExceptionHandler;
import com.example.expensetracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.context.annotation.Import;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getExpenses_shouldReturnAllExpenses() throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");

        when(expenseService.getExpenses(
                isNull(),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(List.of(expense));

        mockMvc.perform(get("/api/expenses"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Lunch"))
                .andExpect(jsonPath("$[0].amount").value(250.0))
                .andExpect(jsonPath("$[0].category").value("Food"));
    }


    @Test
    void getExpensesByCategory_shouldReturnExpenses() throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");

        when(expenseService.getExpenses(
                eq("Food"),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(List.of(expense));

        mockMvc.perform(
                        get("/api/expenses")
                                .param("category", "Food")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].category")
                        .value("Food"));
    }


    @Test
    void getExpensesByAmount_shouldReturnExpenses() throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");

        when(expenseService.getExpenses(
                isNull(),
                eq(100.0),
                eq(500.0),
                isNull()
        )).thenReturn(List.of(expense));

        mockMvc.perform(
                        get("/api/expenses")
                                .param("min", "100")
                                .param("max", "500")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].amount")
                        .value(250.0));
    }


    @Test
    void getExpensesWithFilters_shouldReturnExpenses() throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");

        when(expenseService.getExpenses(
                eq("Food"),
                eq(100.0),
                eq(500.0),
                eq("ASC")
        )).thenReturn(List.of(expense));

        mockMvc.perform(
                        get("/api/expenses")
                                .param("category", "Food")
                                .param("min", "100")
                                .param("max", "500")
                                .param("sort", "ASC")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].title")
                        .value("Lunch"));
    }


    @Test
    void getExpenseById_shouldReturnExpense() throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");

        when(expenseService.getExpenseById(1L))
                .thenReturn(expense);

        mockMvc.perform(
                        get("/api/expenses/1")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lunch"))
                .andExpect(jsonPath("$.amount").value(250.0))
                .andExpect(jsonPath("$.category").value("Food"));
    }


    @Test
    void getExpenseById_whenNotFound_shouldReturn404() throws Exception {

        when(expenseService.getExpenseById(999L))
                .thenThrow(
                        new ExpenseNotFoundException(
                                "Expense not found"
                        )
                );

        mockMvc.perform(
                        get("/api/expenses/999")
                )

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.status")
                        .value(404))

                .andExpect(jsonPath("$.message")
                        .value("Expense not found"));
    }


    @Test
    void addExpense_shouldCreateExpense() throws Exception {

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("Dinner");
        request.setAmount(500.0);
        request.setCategory("Food");


        ExpenseResponse response = new ExpenseResponse();

        response.setId(2L);
        response.setTitle("Dinner");
        response.setAmount(500.0);
        response.setCategory("Food");


        when(expenseService.addExpense(
                any(ExpenseRequest.class)
        )).thenReturn(response);


        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Dinner"))
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.category").value("Food"));
    }


    @Test
    void addExpense_withInvalidData_shouldReturn400() throws Exception {

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("");
        request.setAmount(-100.0);
        request.setCategory("");


        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )

                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.status")
                        .value(400))

                .andExpect(jsonPath("$.message")
                        .value("Validation failed."));
    }


    @Test
    void updateExpense_shouldUpdateExpense() throws Exception {

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("Updated Lunch");
        request.setAmount(300.0);
        request.setCategory("Food");


        ExpenseResponse response = new ExpenseResponse();

        response.setId(1L);
        response.setTitle("Updated Lunch");
        response.setAmount(300.0);
        response.setCategory("Food");


        when(expenseService.updateExpense(
                eq(1L),
                any(ExpenseRequest.class)
        )).thenReturn(response);


        mockMvc.perform(
                        put("/api/expenses/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Updated Lunch"))
                .andExpect(jsonPath("$.amount")
                        .value(300.0))
                .andExpect(jsonPath("$.category")
                        .value("Food"));
    }


    @Test
    void updateExpense_withInvalidData_shouldReturn400()
            throws Exception {

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("");
        request.setAmount(-100.0);
        request.setCategory("");


        mockMvc.perform(
                        put("/api/expenses/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )

                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.status")
                        .value(400));
    }


    @Test
    void deleteExpense_shouldDeleteExpense() throws Exception {

        doNothing()
                .when(expenseService)
                .deleteExpense(1L);


        mockMvc.perform(
                        delete("/api/expenses/1")
                )

                .andExpect(status().isNoContent());


        verify(expenseService)
                .deleteExpense(1L);
    }


    @Test
    void searchExpenses_shouldReturnMatchingExpenses()
            throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");


        when(expenseService.searchExpenses("Lunch"))
                .thenReturn(List.of(expense));


        mockMvc.perform(
                        get("/api/expenses/search")
                                .param("keyword", "Lunch")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].title")
                        .value("Lunch"));
    }


    @Test
    void sortExpenses_shouldReturnSortedExpenses()
            throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");


        when(expenseService.sortExpenses(
                "amount",
                "asc"
        )).thenReturn(List.of(expense));


        mockMvc.perform(
                        get("/api/expenses/sort")
                                .param("field", "amount")
                                .param("direction", "asc")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].amount")
                        .value(250.0));
    }


    @Test
    void getExpensesWithPagination_shouldReturnPage()
            throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(250.0);
        expense.setCategory("Food");


        Page<ExpenseResponse> page =
                new PageImpl<>(List.of(expense));


        when(expenseService.getExpensesWithPagination(any()))
                .thenReturn(page);


        mockMvc.perform(
                        get("/api/expenses/page")
                                .param("page", "0")
                                .param("size", "5")
                )

                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.content[0].title")
                                .value("Lunch")
                );
    }


    @Test
    void getTotalAmount_shouldReturnTotal()
            throws Exception {

        when(expenseService.getTotalAmount())
                .thenReturn(5000.0);


        mockMvc.perform(
                        get("/api/expenses/total")
                )

                .andExpect(status().isOk())

                .andExpect(content().string("5000.0"));
    }


    @Test
    void getTotalAmountByCategory_shouldReturnTotals()
            throws Exception {

        when(expenseService.getTotalAmountByCategory())
                .thenReturn(
                        Map.of(
                                "Food", 1500.0,
                                "Travel", 2000.0
                        )
                );


        mockMvc.perform(
                        get("/api/expenses/total/category")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.Food")
                        .value(1500.0))

                .andExpect(jsonPath("$.Travel")
                        .value(2000.0));
    }


    @Test
    void getTotalByDateRange_shouldReturnTotal()
            throws Exception {

        when(expenseService.getTotalByDateRange(
                any(),
                any()
        )).thenReturn(1000.0);


        mockMvc.perform(
                        get("/api/expenses/total/date-range")
                                .param(
                                        "start",
                                        "2026-08-01T00:00:00"
                                )
                                .param(
                                        "end",
                                        "2026-08-31T23:59:59"
                                )
                )

                .andExpect(status().isOk())

                .andExpect(content().string("1000.0"));
    }


    @Test
    void getTotalByMonth_shouldReturnTotal()
            throws Exception {

        when(expenseService.getTotalByMonth(
                2026,
                8
        )).thenReturn(2500.0);


        mockMvc.perform(
                        get("/api/expenses/total/month")
                                .param("year", "2026")
                                .param("month", "8")
                )

                .andExpect(status().isOk())

                .andExpect(content().string("2500.0"));
    }


    @Test
    void getTotalByYear_shouldReturnTotal()
            throws Exception {

        when(expenseService.getTotalByYear(2026))
                .thenReturn(15000.0);


        mockMvc.perform(
                        get("/api/expenses/total/year")
                                .param("year", "2026"))

                .andExpect(status().isOk())

                .andExpect(content().string("15000.0"));
    }


    @Test
    void getAverageAmount_shouldReturnAverage()
            throws Exception {

        when(expenseService.getAverageAmount())
                .thenReturn(250.0);


        mockMvc.perform(
                        get("/api/expenses/average")
                )

                .andExpect(status().isOk())

                .andExpect(content().string("250.0"));
    }


    @Test
    void getHighestExpense_shouldReturnHighestExpense()
            throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(2L);
        expense.setTitle("Shopping");
        expense.setAmount(2000.0);
        expense.setCategory("Shopping");


        when(expenseService.getHighestExpense())
                .thenReturn(expense);


        mockMvc.perform(
                        get("/api/expenses/highest")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.amount")
                        .value(2000.0));
    }


    @Test
    void getLowestExpense_shouldReturnLowestExpense()
            throws Exception {

        ExpenseResponse expense = new ExpenseResponse();

        expense.setId(1L);
        expense.setTitle("Bus");
        expense.setAmount(50.0);
        expense.setCategory("Transport");


        when(expenseService.getLowestExpense())
                .thenReturn(expense);


        mockMvc.perform(
                        get("/api/expenses/lowest")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.amount")
                        .value(50.0));
    }


    @Test
    void getExpenseCount_shouldReturnCount()
            throws Exception {

        when(expenseService.getExpenseCount())
                .thenReturn(10L);


        mockMvc.perform(
                        get("/api/expenses/count")
                )

                .andExpect(status().isOk())

                .andExpect(content().string("10"));
    }


    @Test
    void getExpenseCountByCategory_shouldReturnCount()
            throws Exception {

        when(expenseService.getExpenseCountByCategory("Food"))
                .thenReturn(5L);


        mockMvc.perform(
                        get("/api/expenses/count/category")
                                .param("category", "Food")
                )

                .andExpect(status().isOk())

                .andExpect(content().string("5"));
    }


    @Test
    void getExpenseCountByCategory_shouldReturnAllCounts()
            throws Exception {

        when(expenseService.getExpenseCountByCategory())
                .thenReturn(
                        Map.of(
                                "Food", 5L,
                                "Travel", 3L
                        )
                );


        mockMvc.perform(
                        get("/api/expenses/count/category/all")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.Food")
                        .value(5))

                .andExpect(jsonPath("$.Travel")
                        .value(3));
    }


    @Test
    void getMonthlySummary_shouldReturnSummary()
            throws Exception {

        when(expenseService.getMonthlySummary(2026))
                .thenReturn(
                        Map.of(
                                "January", 1000.0,
                                "February", 1500.0
                        )
                );


        mockMvc.perform(
                        get("/api/expenses/summary/monthly")
                                .param("year", "2026")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.January")
                        .value(1000.0))

                .andExpect(jsonPath("$.February")
                        .value(1500.0));
    }


    @Test
    void getCategoryPercentages_shouldReturnPercentages()
            throws Exception {

        when(expenseService.getCategoryPercentages())
                .thenReturn(
                        Map.of(
                                "Food", 50.0,
                                "Travel", 30.0
                        )
                );


        mockMvc.perform(
                        get("/api/expenses/percentage/category")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.Food")
                        .value(50.0))

                .andExpect(jsonPath("$.Travel")
                        .value(30.0));
    }


    @Test
    void getStatistics_shouldReturnStatistics()
            throws Exception {

        ExpenseResponse highest = new ExpenseResponse();

        highest.setId(10L);
        highest.setTitle("Laptop");
        highest.setAmount(50000.0);
        highest.setCategory("Shopping");


        ExpenseResponse lowest = new ExpenseResponse();

        lowest.setId(1L);
        lowest.setTitle("Bus");
        lowest.setAmount(50.0);
        lowest.setCategory("Transport");


        ExpenseStatisticsResponse statistics =
                new ExpenseStatisticsResponse(
                        75000.0,
                        3000.0,
                        25L,
                        highest,
                        lowest
                );


        when(expenseService.getStatistics())
                .thenReturn(statistics);


        mockMvc.perform(
                        get("/api/expenses/statistics")
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.totalAmount")
                        .value(75000.0))

                .andExpect(jsonPath("$.averageAmount")
                        .value(3000.0))

                .andExpect(jsonPath("$.expenseCount")
                        .value(25))

                .andExpect(jsonPath("$.highestExpense.amount")
                        .value(50000.0))

                .andExpect(jsonPath("$.lowestExpense.amount")
                        .value(50.0));
    }
}