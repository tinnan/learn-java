# Data JPA demo
### Running MongoDB demo
1. Start Docker in your machine.
2. Run `docker compose up -d`.
3. Run the app main class `com.example.demo.DemoApplication` with active profile `mongo`.

### Running relationship demo
1. Run the app main class `com.example.demorelations.DemoRelationsApplication`.
2. The app has H2 console running at `http://localhost:8080/h2-console`. Access with URL `jdbc:h2:mem:testdb` User 
   `sa` Password `password`.

### DemoRelationsApplication data model
Referenced from this lesson https://stackabuse.com/a-guide-to-jpa-with-hibernate-relationship-mapping/
![img.png](img.png)