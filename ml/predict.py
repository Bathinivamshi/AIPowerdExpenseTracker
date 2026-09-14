import joblib

# Load trained model
model = joblib.load("expense_category_model.pkl")

# Test expenses
expenses = [
    "Had lunch at Domino's",
    "Uber ride to office",
    "Bought new shoes",
    "Netflix subscription",
    "Electricity bill"
]

for expense in expenses:
    prediction = model.predict([expense])[0]
    probabilities = model.predict_proba([expense])[0]
    confidence = max(probabilities) * 100

    print(f"Expense: {expense}")
    print(f"Category: {prediction}")
    print(f"Confidence: {confidence:.2f}%")
    print("-" * 40)