import { useEffect, useState } from "react";

function ExpenseList({ refresh }) {
    const [expenses, setExpenses] = useState([]);
    const [editingExpense, setEditingExpense] = useState(null);

    const [editAmount, setEditAmount] = useState("");
    const [editCategory, setEditCategory] = useState("");
    const [editTitle, setEditTitle] = useState("");
    const [editDate, setEditDate] = useState("");

    const [message, setMessage] = useState("");

    const [search, setSearch] = useState("");
    const [filterCategory, setFilterCategory] = useState("");
    const [sortBy, setSortBy] = useState("newest");

    // Load expenses
    async function loadExpenses() {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch(
                "http://localhost:8080/api/expenses",
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            if (response.ok) {
                const data = await response.json();
                setExpenses(data);
            } else {
                setMessage("❌ Failed to load expenses.");
            }
        } catch (error) {
            console.error(error);
            setMessage("❌ Server error.");
        }
    }

    useEffect(() => {
        loadExpenses();
    }, [refresh]);

    // Delete expense
    async function handleDelete(id) {
        const token = localStorage.getItem("token");

        try {
            const response = await fetch(
                `http://localhost:8080/api/expenses/${id}`,
                {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            if (response.ok) {
                setExpenses(
                    expenses.filter((expense) => expense.id !== id)
                );

                setMessage("✅ Expense deleted successfully!");

                setTimeout(() => {
                    setMessage("");
                }, 3000);
            } else {
                setMessage("❌ Failed to delete expense.");
            }
        } catch (error) {
            console.error(error);
            setMessage("❌ Server error.");
        }
    }

    // Start editing
    function handleEdit(expense) {
        setEditingExpense(expense);

        setEditAmount(expense.amount);
        setEditCategory(expense.category);
        setEditTitle(expense.title);
        setEditDate(expense.createdDate || "");
    }

    // Update expense
    async function handleUpdate() {
        if (!editAmount || Number(editAmount) <= 0) {
            setMessage("❌ Please enter a valid amount.");
            return;
        }

        if (!editCategory.trim()) {
            setMessage("❌ Please select a category.");
            return;
        }

        if (!editTitle.trim()) {
            setMessage("❌ Please enter a description.");
            return;
        }

        if (!editDate) {
            setMessage("❌ Please select a date.");
            return;
        }

        const token = localStorage.getItem("token");

        const updatedExpense = {
            title: editTitle,
            category: editCategory,
            amount: Number(editAmount),
            createdDate: editDate
        };

        try {
            const response = await fetch(
                `http://localhost:8080/api/expenses/${editingExpense.id}`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`
                    },
                    body: JSON.stringify(updatedExpense)
                }
            );

            if (response.ok) {
                const updatedData = await response.json();

                setExpenses(
                    expenses.map((expense) =>
                        expense.id === editingExpense.id
                            ? updatedData
                            : expense
                    )
                );

                setEditingExpense(null);

                setMessage("✅ Expense updated successfully!");

                setTimeout(() => {
                    setMessage("");
                }, 3000);
            } else {
                setMessage("❌ Failed to update expense.");
            }
        } catch (error) {
            console.error(error);
            setMessage("❌ Server error.");
        }
    }

    // Search + category filter
    const filteredExpenses = expenses.filter((expense) => {
        const matchesSearch = expense.title
            .toLowerCase()
            .includes(search.toLowerCase());

        const matchesCategory =
            filterCategory === "" ||
            expense.category === filterCategory;

        return matchesSearch && matchesCategory;
    });

    // Sorting
    const sortedExpenses = [...filteredExpenses].sort((a, b) => {
        if (sortBy === "high") {
            return Number(b.amount) - Number(a.amount);
        }

        if (sortBy === "low") {
            return Number(a.amount) - Number(b.amount);
        }

        if (sortBy === "oldest") {
            return (
                new Date(a.createdDate || 0) -
                new Date(b.createdDate || 0)
            );
        }

        return (
            new Date(b.createdDate || 0) -
            new Date(a.createdDate || 0)
        );
    });

    return (
        <div className="expense-list-card">

            <h2>📋 Expense List</h2>

            {/* Message */}
            {message && (
                <p className="form-message">
                    {message}
                </p>
            )}

            {/* Filters */}
            <div className="expense-filters">

                {/* Search */}
                <input
                    type="text"
                    placeholder="🔍 Search expenses..."
                    value={search}
                    onChange={(event) =>
                        setSearch(event.target.value)
                    }
                />

                {/* Category Filter */}
                <select
                    value={filterCategory}
                    onChange={(event) =>
                        setFilterCategory(event.target.value)
                    }
                >
                    <option value="">All Categories</option>
                    <option value="Food">🍔 Food</option>
                    <option value="Travel">✈️ Travel</option>
                    <option value="Shopping">🛍️ Shopping</option>
                    <option value="Bills">💡 Bills</option>
                    <option value="Entertainment">
                        🎬 Entertainment
                    </option>
                    <option value="Health">💊 Health</option>
                    <option value="Education">📚 Education</option>
                    <option value="Other">📦 Other</option>
                </select>

                {/* Sort */}
                <select
                    value={sortBy}
                    onChange={(event) =>
                        setSortBy(event.target.value)
                    }
                >
                    <option value="newest">Newest First</option>
                    <option value="oldest">Oldest First</option>
                    <option value="high">Highest Amount</option>
                    <option value="low">Lowest Amount</option>
                </select>

            </div>

            {/* Table */}
            <div className="expense-table">

                <div className="expense-table-header">
                    <span>Title</span>
                    <span>Category</span>
                    <span>Amount</span>
                    <span>Date</span>
                    <span>Actions</span>
                </div>

                {sortedExpenses.length === 0 ? (
                    <div className="expense-empty">
                        No expenses found.
                    </div>
                ) : (
                    sortedExpenses.map((expense) => (
                        <div
                            className="expense-table-row"
                            key={expense.id}
                        >
                            <span>{expense.title}</span>

                            <span>{expense.category}</span>

                            <span>₹{expense.amount}</span>

                            <span>
                                {expense.createdDate || "No date"}
                            </span>

                            <div className="expense-actions">
                                <button
                                    className="edit-button"
                                    onClick={() =>
                                        handleEdit(expense)
                                    }
                                >
                                    ✏️ Edit
                                </button>

                                <button
                                    className="delete-button"
                                    onClick={() =>
                                        handleDelete(expense.id)
                                    }
                                >
                                    🗑️ Delete
                                </button>
                            </div>
                        </div>
                    ))
                )}

            </div>

            {/* Edit Section */}
            {editingExpense && (
                <div className="edit-expense-card">

                    <h3>✏️ Edit Expense</h3>

                    <div className="form-group">
                        <label>Description</label>

                        <input
                            type="text"
                            value={editTitle}
                            onChange={(event) =>
                                setEditTitle(event.target.value)
                            }
                        />
                    </div>

                    <div className="form-group">
                        <label>Category</label>

                        <select
                            value={editCategory}
                            onChange={(event) =>
                                setEditCategory(event.target.value)
                            }
                        >
                            <option value="">Select Category</option>
                            <option value="Food">🍔 Food</option>
                            <option value="Travel">✈️ Travel</option>
                            <option value="Shopping">🛍️ Shopping</option>
                            <option value="Bills">💡 Bills</option>
                            <option value="Entertainment">
                                🎬 Entertainment
                            </option>
                            <option value="Health">💊 Health</option>
                            <option value="Education">📚 Education</option>
                            <option value="Other">📦 Other</option>
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Amount</label>

                        <input
                            type="number"
                            value={editAmount}
                            onChange={(event) =>
                                setEditAmount(event.target.value)
                            }
                        />
                    </div>

                    <div className="form-group">
                        <label>Date</label>

                        <input
                            type="date"
                            value={editDate}
                            onChange={(event) =>
                                setEditDate(event.target.value)
                            }
                        />
                    </div>

                    <button
                        className="primary-button"
                        onClick={handleUpdate}
                    >
                        💾 Save Changes
                    </button>

                    <button
                        className="secondary-button"
                        onClick={() =>
                            setEditingExpense(null)
                        }
                    >
                        Cancel
                    </button>

                </div>
            )}

        </div>
    );
}

export default ExpenseList;