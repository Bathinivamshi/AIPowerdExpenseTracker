import { useState } from "react";

function ExpenseForm({ onExpenseAdded }) {
    const [amount, setAmount] = useState("");
    const [category, setCategory] = useState("");
    const [description, setDescription] = useState("");
    const [message, setMessage] = useState("");

    const [aiSuggestion, setAiSuggestion] = useState(null);
    const [aiLoading, setAiLoading] = useState(false);

    async function predictCategory() {
        if (!description.trim()) {
            setAiSuggestion(null);
            return;
        }

        const token = localStorage.getItem("token");

        if (!token) {
            setMessage("❌ Please login first.");
            return;
        }

        setAiLoading(true);
        setAiSuggestion(null);

        try {
            const response = await fetch(
                "http://localhost:8080/api/ai/predict-category",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        description: description
                    })
                }
            );

            if (!response.ok) {
                throw new Error("AI prediction failed");
            }

            const data = await response.json();

            console.log("AI Prediction:", data);

            if (data.confidence >= 70) {
                setAiSuggestion(data);
            } else {
                setAiSuggestion({
                    category: data.category,
                    confidence: data.confidence,
                    lowConfidence: true
                });
            }

        } catch (error) {
            console.error("AI Error:", error);
            setAiSuggestion(null);
        } finally {
            setAiLoading(false);
        }
    }

    function acceptAiSuggestion() {
        if (aiSuggestion) {
            setCategory(aiSuggestion.category);
            setMessage("🤖 AI category selected!");
            setAiSuggestion(null);
        }
    }

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

        const response = await fetch(
            "http://localhost:8080/api/expenses",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(expense)
            }
        );

        console.log("3. Response received:", response);
        console.log("4. Response status:", response.status);

        if (response.ok) {
            setAmount("");
            setCategory("");
            setDescription("");
            setAiSuggestion(null);

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
                            onChange={(event) =>
                                setCategory(event.target.value)
                            }
                        >
                            <option value="">Select Category</option>
                            <option value="food">🍔 Food</option>
                            <option value="travel">✈️ Travel</option>
                            <option value="shopping">🛍️ Shopping</option>
                            <option value="utilities">💡 Bills</option>
                            <option value="entertainment">🎬 Entertainment</option>
                            <option value="healthcare">💊 Health</option>
                            <option value="education">📚 Education</option>
                            <option value="other">📦 Other</option>
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
                            onBlur={predictCategory}
                        />
                    </div>

                </div>

                {aiLoading && (
                    <p className="ai-message">
                        🤖 AI is analyzing your expense...
                    </p>
                )}

                {aiSuggestion && (
                    <div className="ai-suggestion">
                        <span>
                            🤖 AI Suggestion:{" "}
                            <strong>{aiSuggestion.category}</strong>
                            {" "}
                            ({aiSuggestion.confidence}% confidence)
                        </span>

                        {!aiSuggestion.lowConfidence && (
                            <button
                                type="button"
                                onClick={acceptAiSuggestion}
                            >
                                Use AI Category
                            </button>
                        )}

                        {aiSuggestion.lowConfidence && (
                            <span>
                                ⚠️ Low confidence — please choose manually.
                            </span>
                        )}
                    </div>
                )}

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