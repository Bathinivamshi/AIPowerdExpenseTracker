import { useEffect, useState } from "react";

function CategorySummary({ refresh }) {
    const [expenses, setExpenses] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");

        fetch("http://localhost:8080/api/expenses", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then((response) => response.json())
            .then((data) => setExpenses(data))
            .catch((error) => console.error(error));
    }, [refresh]);

    const categoryTotals = {};

    expenses.forEach((expense) => {
        const category = expense.category;

        if (!categoryTotals[category]) {
            categoryTotals[category] = 0;
        }

        categoryTotals[category] += Number(expense.amount);
    });

    return (
        <div className="category-summary">

            <h2>📊 Category-wise Spending</h2>

            <div className="category-summary-grid">

                {Object.keys(categoryTotals).length === 0 ? (
                    <p>No expenses yet.</p>
                ) : (
                    Object.entries(categoryTotals).map(
                        ([category, total]) => (
                            <div
                                className="category-card"
                                key={category}
                            >
                                <h3>{category}</h3>

                                <p>₹{total.toFixed(2)}</p>
                            </div>
                        )
                    )
                )}

            </div>

        </div>
    );
}

export default CategorySummary;