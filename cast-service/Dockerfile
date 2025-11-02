FROM python:3.8-slim

WORKDIR /app

# Copier les dépendances
COPY ./requirements.txt /app/requirements.txt

# Installer gcc et nettoyer apt
RUN apt-get update \
    && apt-get install -y gcc \
    && apt-get clean

# Installer les dépendances Python
RUN pip install --no-cache-dir -r /app/requirements.txt

# Copier le reste de l'application
COPY . /app/

# Exposer le port utilisé par l'application
EXPOSE 8000

# Commande pour lancer l'application (ex: FastAPI avec uvicorn)
CMD ["python3", "-c", "import uvicorn; uvicorn.run('app.main:app', host='0.0.0.0', port=8000, loop='asyncio')"]

