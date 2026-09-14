import { useState } from "react";

function ExpenseForm({onExpenseAdded}) {
    const [amount, setAmount] = useState("");
    const [category, setCategory] = useState("");
    const [description, setDescription] = useState("");
    const [message, setMessage] = useState("");
    async function handleSubmit(event) {
        event.preventDefault();
        if (!amount || Number(amount) <= 0) {
            setMessage("❌ Please enter a valid amount.");
            return;
        }

        if (!category.trim()) {
            setMessage("❌ Please enter a category.");
            return;
        }

        if (!description.trim()) {
            setMessage("❌ Please enter a description.");
            return;
        }

        console.log("1. Submit function started");

        const expense = {
            title: description,
            category: category,
            amount: Number(amount)
        };

        console.log("2. Expense:", expense);

        const token = localStorage.getItem("token");

        const response = await fetch("http://localhost:8080/api/expenses", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(expense)
        });

        console.log("3. Response received:", response);
        console.log("4. Response status:", response.status);
        if (response.ok) {
            setAmount("");
            setCategory("");
            setDescription("");

            setMessage("✅ Expense added successfully!");

            onExpenseAdded();

            setTimeout(() => {
                setMessage("");
            }, 3000);
        } else {
            setMessage("❌ Failed to add expense.");
        }
    }

    return (
        <div className="expense-form-card">

            <h2>➕ Add Expense</h2>
            {message && (
                <p className="form-message">
                    {message}
                </p>
            )}

            <form onSubmit={handleSubmit}>

                <div className="expense-form-row">

                    <div className="form-group">
                        <label>Amount</label>
                        <input
                            type="number"
                            placeholder="Enter amount"
                            value={amount}
                            onChange={(event) =>
                                setAmount(event.target.value)
                            }
                        />
                    </div>

                    <div className="form-group">
                        <label>Category</label>
                        <select
                            value={category}
                            onChange={(event) => setCategory(event.target.value)}
                        >
                            <option value="">Select Category</option>
                            <option value="Food">🍔 Food</option>
                            <option value="Travel">✈️ Travel</option>
                            <option value="Shopping">🛍️ Shopping</option>
                            <option value="Bills">💡 Bills</option>
                            <option value="Entertainment">🎬 Entertainment</option>
                            <option value="Health">💊 Health</option>
                            <option value="Education">📚 Education</option>
                            <option value="Other">📦 Other</option>
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Description</label>
                        <input
                            type="text"
                            placeholder="What did you spend on?"
                            value={description}
                            onChange={(event) =>
                                setDescription(event.target.value)
                            }
                        />
                    </div>

                </div>

                <button
                    className="primary-button add-button"
                    type="submit"
                >
                    Add Expense
                </button>

            </form>

        </div>
    );
}

export default ExpenseForm;