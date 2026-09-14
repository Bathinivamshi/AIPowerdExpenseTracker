import { useEffect, useState } from "react";


function ExpenseStats({ refresh }) {
    const [total, setTotal] = useState(0);
    const [average, setAverage] = useState(0);
    const [count, setCount] = useState(0);

    useEffect(() => {
        const token = localStorage.getItem("token");

        fetch("http://localhost:8080/api/expenses/total", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then((response) => response.json())
            .then((data) => setTotal(data));

        fetch("http://localhost:8080/api/expenses/average", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then((response) => response.json())
            .then((data) => setAverage(data));

        fetch("http://localhost:8080/api/expenses/count", {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
            .then((response) => response.json())
            .then((data) => setCount(data));
    }, [refresh]);

    return (
        <div className="stats-container">

            <div className="stat-card">
                <h3>💰 Total Spent</h3>
                <p>₹{total}</p>
            </div>

            <div className="stat-card">
                <h3>📊 Average Expense</h3>
                <p>₹{Number(average).toFixed(2)}</p>
            </div>

            <div className="stat-card">
                <h3>🧾 Expenses</h3>
                <p>{count}</p>
            </div>

        </div>
    );
}

export default ExpenseStats;