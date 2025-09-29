import re

import spacy
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import T5Tokenizer, T5ForConditionalGeneration
import torch

app = FastAPI()

model_path = "t5_experiment_three"  # model not saved in project due to memory limitations on the project upload
tokenizer = T5Tokenizer.from_pretrained(model_path)
model = T5ForConditionalGeneration.from_pretrained(model_path)
nlp = spacy.load("ro_core_news_lg")
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)


class NewsItem(BaseModel):
    content: str


def preprocess_text(news_text):
    url_pattern = re.compile(r'https?://\S+')

    def remove_urls(text):
        return url_pattern.sub('', text)

    news_text = remove_urls(news_text)  # remove urls

    news_text = re.sub(r'\d', '', news_text)  # remove digits

    def lemmatize_and_filter(text):
        doc = nlp(text)
        return ' '.join(
            [token.lemma_ for token in doc if not token.is_stop and not token.is_punct and not token.is_space])

    news_text = lemmatize_and_filter(news_text)

    news_text = news_text.lower()

    return news_text


@app.post("/predict")
def predict(news_item: NewsItem):
    news_text = preprocess_text(news_item.content)
    try:
        input_text = "clasifică știre: " + news_text
        inputs = tokenizer(input_text, return_tensors="pt", padding=True, truncation=True, max_length=512)
        inputs = {k: v.to(device) for k, v in inputs.items()}
        output = model.generate(**inputs, max_length=10)
        prediction = tokenizer.decode(output[0], skip_special_tokens=True).strip().lower()
        if prediction == "fals":
            prediction = "fake"
        elif prediction == "real":
            prediction = "real"
        return {"prediction": prediction}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
