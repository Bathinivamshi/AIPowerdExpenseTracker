from fastapi import FastAPI
from pydantic import BaseModel
import joblib


app = FastAPI(title="Expense AI Service")


# Load trained ML model
model = joblib.load("expense_category_model.pkl")


class ExpenseRequest(BaseModel):
    description: str


@app.get("/")
def home():
    return {
        "message": "Expense AI Service is running"
    }


@app.post("/predict")
def predict_category(request: ExpenseRequest):

    description = request.description.strip()

    if not description:
        return {
            "error": "Description cannot be empty"
        }

    # Predict category
    prediction = model.predict([description])[0]

    # Get probabilities
    probabilities = model.predict_proba([description])[0]

    # Highest probability
    confidence = max(probabilities) * 100

    return {
        "category": prediction,
        "confidence": round(confidence, 2)
    }