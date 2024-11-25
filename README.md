# 🚀 **FreelyForm** - Spring Boot Application with MongoDB

### Powered by **Java 21**

---

## 🛠️ **How to Run**

Get the project up and running in just a few steps:

1. Start the Dockerized application : 
    ```bash
   docker compose up
   ```
2. To use debugging mode or start the application locally:
    ```bash
   docker compose up mongo 
   ```
   Then run the application from your IDE.
3. To start the application in production mode:
    ```bash
   docker-compose -f docker-compose.prod.yml up
   ```

## 👩‍💻 **For Developers**

### 🔥 **To connect as an admin**

Information about admin login will show in red in the terminal when launching the app for the first time, the password will be shown only once.
You can delete the current admin account if needed via the mongo express UI.

---

## 📊 **Accessing MongoDB Data**

Want to check or manipulate the data in your MongoDB instance? No problem! Simply head over to:

🌐 [Mongo Express UI](http://localhost:8081)

This provides an easy-to-use interface for your database operations. 🗃️
Username: admin, Password: pass

---

## 📚 **API Documentation**

Dive into the API and interact with its endpoints seamlessly through **Swagger UI**! Explore, test, and experiment:

🔗 **[Swagger UI - API Documentation](http://localhost:8080/swagger-ui/index.html)**

Click the link to access the interactive documentation and start making requests directly from your browser! 🚀



