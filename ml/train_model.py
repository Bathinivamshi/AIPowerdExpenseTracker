import pandas as pd
import joblib

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score, classification_report


# ==============================
# 1. Load datasets
# ==============================

train_path = "ml/data/train_transactions.csv"
test_path = "ml/data/test_transactions.csv"

train_df = pd.read_csv(train_path)
test_df = pd.read_csv(test_path)

print("Training samples:", len(train_df))
print("Testing samples:", len(test_df))

print("\nCategories:")
print(train_df["category"].value_counts())


# ==============================
# 2. Prepare data
# ==============================

X_train = train_df["transaction_text"].astype(str)
y_train = train_df["category"]

X_test = test_df["transaction_text"].astype(str)
y_test = test_df["category"]


# ==============================
# 3. Create ML pipeline
# ==============================

model = Pipeline([
    (
        "tfidf",
        TfidfVectorizer(
            lowercase=True,
            ngram_range=(1, 2)
        )
    ),
    (
        "classifier",
        LogisticRegression(
            max_iter=1000
        )
    )
])


# ==============================
# 4. Train model
# ==============================

print("\nTraining model...")

model.fit(X_train, y_train)

print("✅ Model training completed!")


# ==============================
# 5. Evaluate model
# ==============================

print("\nEvaluating model...")

y_pred = model.predict(X_test)

accuracy = accuracy_score(y_test, y_pred)

print("\n==============================")
print("MODEL PERFORMANCE")
print("==============================")

print(f"Accuracy: {accuracy * 100:.2f}%")

print("\nClassification Report:")
print(classification_report(y_test, y_pred))


# ==============================
# 6. Save model
# ==============================

model_path = "expense_category_model.pkl"

joblib.dump(model, model_path)

print("\n==============================")
print("MODEL SAVED")
print("==============================")

print(f"📁 {model_path}")