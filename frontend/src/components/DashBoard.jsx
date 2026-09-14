import ExpenseForm from "./ExpenseForm";
import ExpenseList from "./ExpenseList";
import ExpenseStats from "./ExpenseStats";
import { useState } from "react";
import CategorySummary from "./CategorySummary";

function Dashboard() {
    const [refresh, setRefresh] = useState(false);

    function refreshDashBoard() {
        setRefresh(!refresh);
    }

    function handleLogout() {
        localStorage.removeItem("token");
        window.location.reload();
    }

    return (
        <div>
            <div className="dashboard-container">
            <h1>💰 Expense Tracker Dashboard</h1>
            </div>

            <button onClick={handleLogout}>
                Logout
            </button>

            <ExpenseStats refresh={refresh} />
            <CategorySummary refresh={refresh} />

            <hr />

            <ExpenseForm
                onExpenseAdded={refreshDashBoard}
            />

            <hr />

            <ExpenseList refresh={refresh} />

        </div>
    );
}

export default Dashboard;