<!-- TOC start (generated with https://github.com/derlin/bitdowntoc) -->

- [Secure](#secure)
- [Introduction](#introduction)
   * [Key Features](#key-features)
- [**JWT Tokens**](#jwt-tokens)
   * [**How it Works on the Backend**](#how-it-works-on-the-backend)
      + [**Short-lived `accessToken`**](#short-lived-accesstoken)
      + [**Long-lived `token`**](#long-lived-token)
      + [**Refreshing the `accessToken`**](#refreshing-the-accesstoken)
   * [Sequence Charts in Backend](#sequence-charts-in-backend)
      + [Login](#login)
      + [Register](#register)
      + [Refresh Token](#refresh-token)
   * [**How it Works on the Javafx Frontend**](#how-it-works-on-the-javafx-frontend)
   * [Good Practices](#good-practices)
      + [First Use Case](#first-use-case)
      + [Second Use Case](#second-use-case)
      + [Third Use Case](#third-use-case)
      + [Fourth Use Case](#fourth-use-case)
      + [Fifth Use Case](#fifth-use-case)
- [Serialization](#serialization)
   * [**Serialization Architecture**](#serialization-architecture)
   * [**Serialization Security**](#serialization-security)
      + [**`WhitelistValidator`**](#whitelistvalidator)
      + [**Initialization of `deserializationClassWhitelist`**](#initialization-of-deserializationclasswhitelist)
- [**SQL Injection**](#sql-injection)
   * [What Not To Do](#what-not-to-do)
   * [**Secure Repository Implementations**](#secure-repository-implementations)
      + [**VehicleRepository**](#vehiclerepository)
      + [**ReportRepository**](#reportrepository)
   * [**Security Measures**](#security-measures)
      + [**Use of JPQL with Named Parameters**](#use-of-jpql-with-named-parameters)
- [SonarLint Report](#sonarlint-report)
   * [Initial Situation](#initial-situation)
   * [Duplicated String](#duplicated-string)
   * [Empty Folder](#empty-folder)
   * [String Pattern](#string-pattern)
   * [End Situation](#end-situation)
- [OWASP ZAP](#owasp-zap)

<!-- TOC end -->

<!-- TOC --><a name="secure"></a>
# Secure

<!-- TOC --><a name="introduction"></a>
# Introduction

This project is an application designed to manage vehicles and their associated drivers, leveraging a Spring backend and a JavaFX frontend. It was created to meet the requirements of three distinct courses, resulting in some design choices that, while potentially unconventional or inefficient, serve to demonstrate alternative approaches and solutions.
The backend is in the present repository and the frontend is in https://github.com/MrCharlesSG/Insurance-Frontend.

<!-- TOC --><a name="key-features"></a>
## Key Features

1. **User Management**: Vehicles can register, log in, and log out of the system.
2. **Driver Association**: Vehicles can create driver profiles and associate them via email.
3. **Driver Management**: Vehicles can view and manage all associated drivers.
4. **Reporting**: Vehicles can create, and manage reports, detailing incidents and damages.
5. **Report Handling**: Vehicles can accept or reject reports made by others regarding their car.
6. **Future Enhancements**: Plans include the integration of an insurance model and expanded roles for drivers.

<!-- TOC --><a name="jwt-tokens"></a>
# **JWT Tokens**

All endpoints in the server, except for **`/auth`** and **`/vehicles`**, are protected. Users need an **`accessToken`** to access the protected endpoints. Nevertheless there are other endpoints not protected. Here’s why those specific endpoints aren’t protected:

- **`/auth`**: This endpoint must be publicly accessible so users can authenticate.
- **`/vehicles`**: This endpoint is used in a MuleProject, and managing authentication for all calls through the MuleProject was deemed unnecessary. Therefore, this endpoint is "public" in the current version of the project, as it handles non-sensitive information. It has also been stripped of some key features to make it more manageable. These key features are used by other services but not by the **`VehiclesController`**.

<!-- TOC --><a name="how-it-works-on-the-backend"></a>
## **How it Works on the Backend**

To access a protected endpoint, the user must provide a header with the keyword **`Authorization`** and the value **`Bearer *accessToken*`**.

Upon successful login or registration, the user receives a data structure like this:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTY0Nzk4OTU4NX0.Dw7U2YOZH8F4-tUNr1ZQV3QntTe19VR6FN5W_mmfT3s",
  "token": "dGhpcyBpcyBhIHNhbXBsZSB0b2tlbg=="
}
```

<aside>
When registering, the user also receives their information wrapped in a wrapper.
</aside>

The **`accessToken`** allows the user to access protected endpoints. However, it is valid for only 10 minutes, while the **`token`** is valid for seven days. Here’s why:

<!-- TOC --><a name="short-lived-accesstoken"></a>
### **Short-lived `accessToken`**

The **`accessToken`** is valid for 10 minutes to minimize security risks. Since the **`accessToken`** is used frequently in almost every request to protected endpoints, there is a higher risk of it being intercepted or stolen. By limiting its lifespan, the window of opportunity for an attacker to use a stolen token is significantly reduced, enhancing overall security.

<!-- TOC --><a name="long-lived-token"></a>
### **Long-lived `token`**

The **`token`**, on the other hand, is valid for seven days. This longer lifespan provides a more convenient user experience. Users don’t have to log in repeatedly throughout the day. Instead, they can use the **`token`** to refresh their **`accessToken`** when it expires. This way, users only need to log in once a week, balancing security and convenience.

<!-- TOC --><a name="refreshing-the-accesstoken"></a>
### **Refreshing the `accessToken`**

To refresh the **`accessToken`**, the user calls the **`/auth/api/v1/refreshToken`** endpoint with the **`token`** in the request body. This process extends the user's session without requiring them to re-enter their credentials frequently, thus maintaining a seamless and secure user experience.

<!-- TOC --><a name="sequence-charts-in-backend"></a>
## Sequence Charts in Backend

<!-- TOC --><a name="login"></a>
### Login

![AuthDiagrams-Login.drawio.png](images/AuthDiagrams-Login.drawio.png)

<!-- TOC --><a name="register"></a>
### Register

![The JwtReponseAspect is an aspect executed arround the function in the AuthController encharge of registering a vehicle. The scope of this aspect is to standarize the registering by wrapping the info of the just created user in a wrapper. The main reason is becouse in the future there will be a register of drivers. ](images/Untitled.png)

The JwtReponseAspect is an aspect executed arround the function in the AuthController encharge of registering a vehicle. The scope of this aspect is to standarize the registering by wrapping the info of the just created user in a wrapper. The main reason is becouse in the future there will be a register of drivers. 

<!-- TOC --><a name="refresh-token"></a>
### Refresh Token

![Untitled](images/Untitled%201.png)

<!-- TOC --><a name="how-it-works-on-the-javafx-frontend"></a>
## **How it Works on the Javafx Frontend**

The tokens in the frontend are mainly managed by the `TokenService` class. This class is encharge of providing the `accessToken`, initializing the session and refreshing the `accessToken` automatically till the `token` gets invalid. When closing the application and opening again the user won’t need to login again (if the `token` is valid), how is done this?

Every time we login or we refresh the tokens, the `TokenService` will serialize and save the tokens and its expirations days. So whenever we initialize the app, the `TokenService` will look to this serialized tokens and refresh them if the `token` is valid. If there is not token serialized or is expired the login screen is shown.

<aside>
The serialization is securily done, how will be explained later
</aside>

The automatically refresh token works as follow: a client ask for the `accessToken` by calling the `getAccessToken` function in the `TokenService` class which will return an String with the `accessToken`, and we have 4 options:

1. There is no access token in these case there is two possibilities:
    1. The token is serialized, so we call the `loadToken`
    2. There is no serialized token and the client miss call the function. In this case a `RuntimeException` is thrown.
2. The `token` has expired, so we throw an `IllegalAccessException` that must be managed by all the clients. The normal flow is that the clients send a message to the user and open the login screen.
    
    ![Untitled](images/Untitled%202.png)
    
3. The `accessToken` has expired but the `token` not, so we refresh the token by calling the REST API.
4. Everithing is ok and we just give the client the `accessToken`.

<!-- TOC --><a name="good-practices"></a>
## Good Practices

Integration test are done for `AuthController`, `DriversController`, and `ReportsController`. The scope of this test is to test all endpoints the REST API expose. This three controllers are (in this moment) all the functionallity the application has. As already mention before the `VehicleController` is a mocking controller for use in the integration of other tools regarding the Advance Interoperability for Information System project. 

The development of the project has follow an almost TDD (Test Driven Development), as almost all test where develop first and then the controllers where develop and tested. Nevertheless there has been a development for brand new test to success in the Coverage Report. Success in this context meant cover the 100% of the lines of the three controllers. After finishing development the test didn’t cover all the exceptions of controllers, nevertheless now the tests cover the 100% of the controllers.

![Untitled](images/Untitled%203.png)

The tests are normally develop following use cases, here are some examples:

<!-- TOC --><a name="first-use-case"></a>
### First Use Case

1. User Login
2. Then logout
3. Try to access resources and fails
4. Login again
5. Access resources

```java
@Test
    public void loginOkLogoutAndTryAccessResourceThenStatusForbiddenAndLoginAgainAndAccessResource_thenStatusOK() throws Exception{
        JSONObject jsonObject = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/auth/api/v1/logout")
                        .header("Authorization", "Bearer " + jsonObject.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/driver/byEmail")
                        .header("Authorization", "Bearer " + jsonObject.getString("accessToken"))
                        .param("email", "ger.pa@example.com")
                )
                .andExpect(status().isForbidden())
                .andDo(print());

        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver/byEmail")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .param("email", "ger.pa@example.com")
                )
                .andExpect(status().isOk())
                .andDo(print());

    }
```

<!-- TOC --><a name="second-use-case"></a>
### Second Use Case

1. User login
2. User reject a report that is in his waiting reports 
3. User get the reports
4. User get the waiting reports….

```java
@Test
    public void rejectAReportAndCheckAllTypesReports_thenStatusIsOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(delete("/report/reject/3")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/report/close-report.json"))
                )
                .andExpect(status().isOk());

        mvc.perform(get("/report")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());

        mvc.perform(get("/report/waiting")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());

        mvc.perform(get("/report/accepted")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(print());

        mvc.perform(get("/report/rejected")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andDo(print());

    }
```

<!-- TOC --><a name="third-use-case"></a>
### Third Use Case

1. User Login but insert incorrect token so he can’t refresh it

```java
    @Test
    public void refreshTokenAfterLoginCorrectlyWithWrongToken_thenResponseOK() throws Exception{
        JSONObject jsonObject = loginOkWithUser(mvc, correctUser());

        mvc.perform(post("/auth/api/v1/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject.getString("token")+"k")))
                .andExpect(status().isBadRequest());

    }
```

<!-- TOC --><a name="fourth-use-case"></a>
### Fourth Use Case

1. User login
2. User associate an existing driver to its vehicle
3. User gets all its drivers

```java
    @Test
    public void associateDriverViaEmail_thenStatusOK() throws Exception {
        JSONObject login = loginOkWithUser(mvc, correctUser());

        mvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andDo(print());

        mvc.perform(post("/driver/associate?email=vic.mar@example.com")
                        .header("Authorization", "Bearer " + login.getString("accessToken")))
                .andExpect(status().isOk());

        mvc.perform(get("/driver")
                        .header("Authorization", "Bearer " + login.getString("accessToken"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());
    }
```

<!-- TOC --><a name="fifth-use-case"></a>
### Fifth Use Case

1. User try to register incorrectly

```java
@Test
    public void conflictRegistrationAndGetToken_thenStatusBadRequest() throws Exception {
        mvc.perform(post("/auth/api/v1/register/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getStringOfJsonFile("/json/auth/conflict-requister-vehicle.json")))
                .andDo(print())
                .andExpect(status().isConflict());
    }
```

<!-- TOC --><a name="serialization"></a>
# Serialization

The security breach I wanted to cover in this chapter is the serialization. In this issue the attaquers try to serialize in one of our serialized files classes that we don’t need. The only part of my project that had this problem is when I serialize all the info for the logged user in the frontend.

<!-- TOC --><a name="serialization-architecture"></a>
## **Serialization Architecture**

To fully understand how I secure the serialization process, I first need to explain how serialization is implemented.

I use a generic abstract class defined as follows:

```java
javaCopiar código
public abstract class Serializer<T> {

    protected void save(T classToSave, String location) throws NonSerializableClassException {
        // Common logic for saving serializable classes, used by subclasses
    }

    protected T load(String location) throws NonSerializableClassException {
        // Common logic for loading serializable classes, used by subclasses
    }

    public abstract void save(T classToSave) throws NonSerializableClassException;
    public abstract T load() throws NonSerializableClassException;
}

```

This class contains two protected methods with the common logic for saving and loading serializable classes, utilized by subclasses. Additionally, it has two abstract methods that the subclasses must implement.

The subclasses are not generic; they directly specify the type they can serialize:

```java
javaCopiar código
public class TokenSerializer extends Serializer<Token> {
    private static final String SERIALIZATION_FOR_TOKEN_LOCATION = "data/token.ser";

    @Override
    public void save(Token classToSave) throws NonSerializableClassException {
        // Implementation
    }

    @Override
    public Token load() throws NonSerializableClassException {
        // Implementation
    }
}

public class VehicleSerializer extends Serializer<VehicleInfoDTO> {
    public static final String SERIALIZATION_FOR_AUTHENTICATED_VEHICLE_LOCATION = "data/authenticated-vehicle.ser";

    @Override
    public void save(VehicleInfoDTO classToSave) throws NonSerializableClassException {
        // Implementation
    }

    @Override
    public VehicleInfoDTO load() throws NonSerializableClassException {
        // Implementation
    }
}

```

The way these subclasses are implemented makes it impossible for clients to use **`Serializer`** to serialize classes other than **`VehicleInfoDTO`** and **`Token`**.

<!-- TOC --><a name="serialization-security"></a>
## **Serialization Security**

As you may notice, all functions in **`Serializer`** throw a **`NonSerializableClassException`**. This is a custom exception thrown by a class responsible for verifying if the file and serialized class are correct.

<!-- TOC --><a name="whitelistvalidator"></a>
### **`WhitelistValidator`**

The **`WhitelistValidator`** class is responsible for validating the classes involved in serialization.

```java
public class WhitelistValidator {

    private WhitelistValidator() {}

    private static Set<Class> deserializationClassWhitelist;

    static {
        // Initialization of deserializationClassWhitelist, explained later
    }

...

    public static void checkIfIsValidClass(Class clazz) throws NonSerializableClassException {
        if (!deserializationClassWhitelist.contains(clazz)) {
            throw new NonSerializableClassException("The class " + clazz + " is not allowed!");
        }
    }

    public static void validateSerializedFile(String binaryFile) throws IOException, NonSerializableClassException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binaryFile))) {
            Object readObject;
            while ((readObject = ois.readObject()) != null) {
                if (!deserializationClassWhitelist.contains(readObject.getClass())) {
                    throw new SecurityException("The class " + readObject.getClass() + " is not allowed!");
                } else {
                    if (readObject instanceof List<?> objectList) {
                        for (Object object : objectList) {
                            checkIfIsValidClass(object.getClass());
                        }
                        objectList.forEach(System.out::println);
                    }
                }
            }
        } catch (EOFException e) {
            System.out.println("Everything OK");
        } catch (Exception ex) {
            throw new SecurityException("There was a problem with deserialization!\n" + ex.getMessage(), ex);
        }
    }
}

```

The **`checkIfIsValidClass`** method checks if a class is in the whitelist, throwing a **`NonSerializableClassException`** if it is not. The **`validateSerializedFile`** method scans the file, checking all objects against the whitelist.

<!-- TOC --><a name="initialization-of-deserializationclasswhitelist"></a>
### **Initialization of `deserializationClassWhitelist`**

Only objects implemented by the generic types of **`Serializer`** subclasses can be serialized. Using reflection, a function iterates through all classes in the *serialization* package and retrieves the generic type from the classes that implement **`Serializer`**. This process is shown below:

```java
public class WhitelistValidator {

    private WhitelistValidator() {}

    private static Set<Class> deserializationClassWhitelist;

    static {
        deserializationClassWhitelist = new HashSet<>();
        try {
            registerAllowedClasses();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerAllowedClasses() throws ClassNotFoundException, IOException {
        String packageName = "hr.algebra.javafxinsurance.serialization";
        Class<?>[] serializerClasses = ReflectionUtils.getClasses(packageName);

        for (Class<?> clazz : serializerClasses) {
            if (Serializer.class.isAssignableFrom(clazz)) {
                Class<?>[] genericTypes = ReflectionUtils.getGenericTypes(clazz);
                deserializationClassWhitelist.addAll(Arrays.asList(genericTypes));
            }
        }
    }
    ...
}

package hr.algebra.javafxinsurance.utils;

import hr.algebra.javafxinsurance.serialization.Serializer;
import org.reflections.Reflections;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReflectionUtils {

    public static Class<?>[] getClasses(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends Serializer>> serializerClasses = reflections.getSubTypesOf(Serializer.class);
        return serializerClasses.toArray(new Class<?>[0]);
    }

    public static Class<?>[] getGenericTypes(Class<?> clazz) {
        List<Class<?>> genericTypes = new ArrayList<>();
        Type superclass = clazz.getGenericSuperclass();
        if (superclass instanceof ParameterizedType parameterizedType) {
            Type[] types = parameterizedType.getActualTypeArguments();
            for (Type type : types) {
                if (type instanceof Class<?>) {
                    genericTypes.add((Class<?>) type);
                }
            }
        }
        return genericTypes.toArray(new Class<?>[0]);
    }
}

```

This approach ensures that only the correct classes, defined by the generic types of **`Serializer`** subclasses, are allowed for serialization.

<!-- TOC --><a name="sql-injection"></a>
# **SQL Injection**

In this section, I'll document the measures taken to mitigate SQL injection vulnerabilities in the project repositories. As all repositories extend from **`JpaRepository`,** the main vulnerability involves using JPQL (Java Persistence Query Language) and the Spring Data JPA repository's **`@Query`** annotation with parameter binding to ensure that inputs are handled securely.

<!-- TOC --><a name="what-not-to-do"></a>
## What Not To Do

This example demonstrates the incorrect way of building and executing a query in a Spring Data JPA repository, which is susceptible to SQL injection attacks.

```java
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Vulnerable method
    @Query(value = "SELECT * FROM Vehicle v WHERE v.userInfo.username = ?1", nativeQuery = true)
    Optional<Vehicle> findByPlate(String plate);
}

```

In this example, the **`@Query`** annotation uses native SQL with a positional parameter (**`?1`**). The method directly takes user input (**`plate`**) and plugs it into the query without proper parameter binding, which can lead to SQL injection if the input is not properly sanitized. For example a malicious input will look like this: `"someUser' OR '1'='1"` and the query of the function will be 

`SELECT * FROM Vehicle v WHERE v.userInfo.username = 'someUser' OR '1'='1';` which will return all rows.

The way of solving it is by binding the user inputs. 

<!-- TOC --><a name="secure-repository-implementations"></a>
## **Secure Repository Implementations**

This are some examples of how the **`@Query`** annotation should be correctly implemented helped by the `@Param` annotation in the function parameter.

<!-- TOC --><a name="vehiclerepository"></a>
### **VehicleRepository**

Similarly, the **`VehicleRepository`** uses parameter binding in custom queries to prevent SQL injection.

```java
javaCopiar código
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.userInfo.id = :userInfoId")
    Optional<Vehicle> findByUserInfoId(@Param("userInfoId") long userInfoId);

    @Query("SELECT v FROM Vehicle v WHERE v.userInfo.username = :plate")
    Optional<Vehicle> findByPlate(@Param("plate") String plate);
}
```

<aside>
 In the same case of before the JPA provider will safely escape the input and execute the equivalent of:

```sql
SELECT v FROM Vehicle v WHERE v.userInfo.username = 'someUser'' OR ''1''=''1';
```

</aside>

<!-- TOC --><a name="reportrepository"></a>
### **ReportRepository**

The **`ReportRepository`** contains several custom query methods. Each query uses named parameters to safely bind user inputs, preventing SQL injection attacks.

```java
javaCopiar código
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r " +
           "JOIN r.infoReportDriverA a " +
           "JOIN r.infoReportDriverB b " +
           "WHERE (a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId) " +
           "AND (a.status = REJECTED OR b.status = REJECTED)")
    List<Report> findAllByVehicleIdAndRejected(@Param("vehicleId") Long vehicleId);

    @Query("SELECT r FROM Report r " +
           "JOIN r.infoReportDriverA a " +
           "JOIN r.infoReportDriverB b " +
           "WHERE (a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId) " +
           "AND (a.status = ACCEPTED AND b.status = ACCEPTED)")
    List<Report> findAllByVehicleIdAndAccepted(@Param("vehicleId") Long vehicleId);

    @Query("SELECT r FROM Report r " +
           "JOIN r.infoReportDriverA a " +
           "JOIN r.infoReportDriverB b " +
           "WHERE (a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId) " +
           "AND (a.status = WAITING OR b.status = WAITING)")
    List<Report> findAllByVehicleIdAndWaiting(@Param("vehicleId") Long vehicleId);

    @Query("SELECT r FROM Report r " +
           "JOIN r.infoReportDriverA a " +
           "JOIN r.infoReportDriverB b " +
           "WHERE a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId")
    List<Report> findAllByVehicleId(@Param("vehicleId") Long vehicleId);
}

```

<!-- TOC --><a name="security-measures"></a>
## **Security Measures**

<!-- TOC --><a name="use-of-jpql-with-named-parameters"></a>
### **Use of JPQL with Named Parameters**

By using JPQL with named parameters, the queries are automatically safe from SQL injection attacks. JPQL, part of the JPA specification, translates these queries into the native SQL of the underlying database. The use of **`@Param`** annotations ensures that the parameters are bound securely:

```java
@Query("SELECT r FROM Report r " +
       "JOIN r.infoReportDriverA a " +
       "JOIN r.infoReportDriverB b " +
       "WHERE (a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId) " +
       "AND (a.status = REJECTED OR b.status = REJECTED)")
List<Report> findAllByVehicleIdAndRejected(@Param("vehicleId") Long vehicleId);

```

<!-- TOC --><a name="sonarlint-report"></a>
# SonarLint Report

In this section I’ll document the report I did with SonarLint after finishing the project. 

<!-- TOC --><a name="initial-situation"></a>
## Initial Situation

The initial situation was as follow:

![Untitled](images/Untitled%204.png)

There where 91 issues in total. There where 9 issues (10%) with high impact, 43 issues (48%) with medium impact and 38 (42%) with low impact. The objective is to reduce to 0 the hight impact issues and to reduce in half the medium issues. Here are some issues and how I solve them.

<!-- TOC --><a name="duplicated-string"></a>
## Duplicated String

![Untitled](images/Untitled%205.png)

6 high impact issues where in the `ReportController`, and where the same issue: “String literals should not be duplicated”. All this string where used for taking metrics of the controller. For solving all those 6 issues I created a class call `Constants` with all of the Strings. 

```java
package hr.algebra.insurancebackend.constants;

public class Constants {

    private Constants(){}
    public static final class ReportControllerEndpoints {
        private ReportControllerEndpoints(){}
        public static final String REPORT = "report";
        public static final String REJECTED = "/rejected";
        public static final String WAITING = "/waiting";
        public static final String ACCEPTED = "/accepted";
        public static final String ACCEPT_ID = "/accept/{id}";
        public static final String REJECT_ID = "/reject/{id}";
    }

    public static final class Methods {
        private Methods(){}
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String DELETE = "DELETE";
    }

    public static final class Metrics {
        private Metrics() {}
        public static final String HTTP_REQUESTS = "http.requests";
        public static final String METHOD = "method";
        public static final String ENDPOINT = "endpoint";
        public static final String ERRORS_SUFFIX = ".errors";
    }
}
```

This is the result of the last scan (those small impact issues where alredy solved).

![Untitled](images/Untitled%206.png)

There where the same issue of duplicated Strings in the `DriverController`

![Untitled](images/Untitled%207.png)

I solved by creating a class in the `constant` package like follows

```java
public class ErrorMessages {
    public static final String DRIVER_NOT_FOUND_EMAIL = "Could not found driver with email :";
}
```

As you can see there are no more issues in the `DriverController`

![Untitled](images/Untitled%208.png)

<!-- TOC --><a name="empty-folder"></a>
## Empty Folder

Other issue was the implementation of a method that was empty. 

![Untitled](images/Untitled%209.png)

The problem was in the `EmailValidator` class, which implement the `ConstraintValidator<A,T>` interface. This is the inheritance of an old implementation of `EmailValidator`. In the begining this class was supose to be use in an anotation. Nevertheles I want to maintain this annotation for the future, so I just unimplemented the function. This means I don’t overwrite it and use the parent implementation. This parent implementation is a default function of the interface.

```java
// Class at the begining
@Component
public class EmailValidator
        implements ConstraintValidator<ValidEmail, String> {

    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return (validateEmail(email));
    }
    public boolean isValidEmail(String email) {
        return (validateEmail(email));
    }
    private boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

// class solved
@Component
public class EmailValidator
        implements ConstraintValidator<ValidEmail, String> {

    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return (validateEmail(email));
    }
    public boolean isValidEmail(String email) {
        return (validateEmail(email));
    }
    private boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

// The initialize function that uses the class now
public interface ConstraintValidator<A extends Annotation, T> {
    default void initialize(A constraintAnnotation) {
    }
    ....
}

```

This is the result of the last scan

![Untitled](images/Untitled%2010.png)

The same problem was in the `PasswordMatchesValidator` and was solved the same way.

Whith so I finished with all of the high risk errors.

[Untitled](images/Untitled.mp4)

<!-- TOC --><a name="string-pattern"></a>
## String Pattern

An issue that seems to me interesting is the following

In the `EmailValidator` class there is a string that store the pattern of the email.

```java
@Component
public class EmailValidator
        implements ConstraintValidator<ValidEmail, String> {

    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
	...
}
```

![Untitled](images/Untitled%2011.png)

This issue is ralated to the potential inefficiency and risk of stack overflow in the regular expression used for validating email addresses.

The original regular expression was:

```java
public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

```

SonarLint flagged the repetition in the regular expression as potentially leading to stack overflow errors for large inputs. This is because the `(.[_A-Za-z0-9-]+)*` part of the regex allows for zero or more repetitions, which can lead to excessive backtracking, especially for large inputs.

To solved the regex was optimized and simplified to:

```java
public static final String EMAIL_PATTERN = "^[A-Za-z0-9_.+-]+@[A-Za-z0-9-]+\\\\.[A-Za-z0-9-.]+$";

```

In this optimized version, the repetition is removed, avoiding the risk of excessive backtracking and potential stack overflow.

![Untitled](images/Untitled%2012.png)

<!-- TOC --><a name="end-situation"></a>
## End Situation

At the end I had 35 issues, none of them with high impact. The final percentage of each where:

- 7 (20%) with low impact
- 28 (80%) with medium impact. In fact 22 (80%) of those issues are for changing the `Autowired` with the `AllArgsConstructor`, the problem is that when I remove some of then does not autowired correctly.

There is a video that shows how is the final situation of the SonarLint scan

<!-- TOC --><a name="owasp-zap"></a>
# OWASP ZAP

After finishing the project, testing of the REST API using OWASP ZAP, a tool designed to scan for potential vulnerabilities in web applications and APIs, were conducted. Configuring the tool presented a significant challenge. Initially, I created a script to automate the inclusion of the `accessToken` in the request headers. Secondly, with OpenAPI, I systematically scanned all endpoints within the application. You can view the scan process in the attached video files.

The scan results are illustrated below:

![images/Untitled%2013.png](images/Untitled%2013.png)

The findings revealed only informational alerts. The first alert highlighted an authentication request, categorized as informational rather than a critical vulnerability, suggesting there was no immediate security issue requiring remediation.

However, the second alert recommended a change: it advised against passing sensitive information through URLs and instead suggested transmitting such data through the request body. Notably, all endpoints in the `driver` section were initially passing sensitive information via URLs. To address this, I refactored these endpoints to accept data through the request body.

Initially, the controller methods appeared as follows:

```java
javaCopiar código
@PostMapping("/associate")
public DriverDTO associateDriver(@PathParam("email") String email) {
    // Method implementation
}

@DeleteMapping("/disassociate")
public DriverDTO disassociateDriver(@PathParam("email") String email) {
    // Method implementation
}
```

Sensitive information, such as email addresses, was passed directly in the URL parameters. Both `associateDriver` and `disassociateDriver` methods were updated to accept an `EmailRequestDTO` object in the request body which just storage the email, ensuring secure handling of sensitive data.

Here's how the refactored controller methods now appear:

```java
javaCopiar código
@PostMapping("/associate")
public DriverDTO associateDriver(@RequestBody EmailRequestDTO email) {
    // Method implementation
}

@DeleteMapping("/disassociate")
public DriverDTO disassociateDriver(@RequestBody EmailRequestDTO email) {
    // Method implementation
}
```

It's important to note that while OWASP ZAP recommended changing the method for retrieving sensitive data via `GET` requests to adhere to their best practices, this adjustment was not adopted due to standard RESTful conventions.

The last scan resulted as follows:

![Untitled](images/Untitled%2014.png)
