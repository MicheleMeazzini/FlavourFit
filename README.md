# FlavourFit: A Large-Scale and Multistructured Recipe Database

## 📌 Project Overview
FlavourFit is a Large-Scale Multi-Structured Databases project designed to store and manage a collection of recipes and user interactions. The project includes a distributed data layer built using two NoSQL architectures (Document DB and Graph DB). The system is designed to efficiently store, retrieve, and analyze a real dataset with a volume of more than 700MB.

The project also features a RESTful API for interacting with the database and exposing key functionalities to users.

## 📂 Repository Structure
```
/FlavourFit
│── /backend          # Backend service (API, DB connection, services)
│   │── /routes       # REST API endpoints
│   │── /models       # Database schemas & models
│   │── /services     # Business logic & DB interaction
│   │── app.py        # Main entry point (Flask/FastAPI/Django)
│   │── requirements.txt  # Python dependencies
│── /data             # Sample dataset (CSV, JSON)
│── /queries          # Queries for each DB type (MongoDB, Neo4J, Redis, etc.)
│── /schema           # Database schema, indexes, and sharding strategy
│── /docs             # Documentation (UML, API specs, CAP theorem analysis)
│── /tests            # Test cases for APIs and DB interactions
│── /deploy           # Deployment scripts (Docker, Kubernetes, Virtual Lab)
│── README.md         # Project overview and setup guide
│── .gitignore        # Ignore unnecessary files
```

## 🛠 Technologies Used
- **NoSQL Databases**: MongoDB (DocumentDB), Neo4J (GraphDB)
- **API Framework**: Flask / FastAPI / Django
- **Data Storage Formats**: JSON, CSV
- **Deployment**: Docker, Kubernetes, Virtual Lab UNIPI
- **Testing**: Postman, Unit Tests

## 📖 Features
✔️ CRUD operations for each NoSQL database
✔️ Data analytics and statistics on the dataset
✔️ Aggregation pipelines for Document DB
✔️ Indexing for optimized query performance
✔️ Graph queries
✔️ Sharding and replication management
✔️ RESTful API for user interaction
✔️ Local and Virtual Cluster deployment
✔️ Consistency management between different DB architectures
✔️ CAP theorem analysis

## 🚀 Setup and Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/FlavourFit.git
   ```
2. Navigate into the project directory:
   ```sh
   cd FlavourFit
   ```
3. Install backend dependencies:
   ```sh
   pip install -r backend/requirements.txt
   ```
4. Set up the databases:
   ```sh
   docker-compose up -d
   ```
5. Run the backend service:
   ```sh
   python backend/app.py
   ```
6. Access the API via Postman or a web browser.

## 👤 Authors
**Michele Meazzini, Angela Ungolo, Stefano Micheloni**
---

🔍 *Feel free to contribute by submitting pull requests or issues!*

