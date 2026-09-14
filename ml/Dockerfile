FROM python:3.12-slim

WORKDIR /app

COPY ml/requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY ml/ .
COPY expense_category_model.pkl .

EXPOSE 8000

CMD ["uvicorn", "ai_api:app", "--host", "0.0.0.0", "--port", "8000"]