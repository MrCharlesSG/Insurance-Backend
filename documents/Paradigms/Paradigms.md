# Paradigms

# Introduction

This project is an application designed to manage vehicles and their associated drivers, leveraging a Spring backend and a JavaFX frontend. It was created to meet the requirements of three distinct courses, resulting in some design choices that, while potentially unconventional or inefficient, serve to demonstrate alternative approaches and solutions.

## Key Features

1. **User Management**: Vehicles can register, log in, and log out of the system.
2. **Driver Association**: Vehicles can create driver profiles and associate them via email.
3. **Driver Management**: Vehicles can view and manage all associated drivers.
4. **Reporting**: Vehicles can create, and manage reports, detailing incidents and damages.
5. **Report Handling**: Vehicles can accept or reject reports made by others regarding their car.
6. **Future Enhancements**: Plans include the integration of an insurance model and expanded roles for drivers.

# Testing

The goal of testing is to deliver the software with the least defects as possible. For that reason we try to find the most errors in production as posible by testing the software in advance.  

<aside>
<img src="https://www.notion.so/icons/light-bulb_blue.svg" alt="https://www.notion.so/icons/light-bulb_blue.svg" width="40px" /> A defect is a bug in comercialization, an error is a bug in production

</aside>

There are various types of testing such us unit test, integration test, system test or UI test. In the case of my project I decide to develop unit and integration test.

The core functionallity of the project is the Spring Boot server. This backend is the only thing that is tested in the project. It has two approaches for testing, the integration test done to each of the controllers but the `VehiclesController`, and the Unit test done only to `VehiclesService`. Why this approach for the project? 

## UI test

I dind’t test my UI becouse is not the most prioretized part of the project. In fact, if the project continue, the UI is going to change to a mobile scope instead of the desktop that is now.  Actually the actuall frontend of the project is just done for fullfilling some requirements of Advance Information System Interoperability subject. With so said, when testing in my project my main objective is to test the core functions of it.

## Integration test

Integration test are done for `AuthController`, `DriversController`, and `ReportsController`. The scope of this test is to test all endpoints the REST API expose. This three controllers are (in this moment) all the functionallity the application has. Why only Integration?

There are some discussions arround the Internet, that maybe the Unit test are not usefull at all in the Spring Boot framework becouse they tent to be slower in relative terms to the integration ones. This is becouse when we unit test in Spring, it starts autowiring all the services and repositories we require, this is almost starting the application which is what the integration test does. But, this is not the main reason I only make integration test for the core functionallity. In the case of my project, as it is a small one, for me was enough to only cover the uses cases I predicted.

The development of the project has follow an almost TDD (Test Driven Development), as almost all test where develop first and then the controllers where develop and tested. Nevertheless there has been a development for brand new test to success in the Coverage Report. Success in this context meant cover the 100% of the lines of the three controllers with the tests. After finishing development the test didn’t cover all the exceptions of controllers, nevertheless now the tests cover the 100% of the controllers.

![Untitled](images/Untitled.png)

![Untitled](images/Untitled%201.png)

### First Use Case

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

### Second Use Case

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

### Conclusion

For concluding with the integration test, most of the test of the `AuthController` test the security, and the test in the `ReportController`, and `DriversController` the use cases of the project.

## Unit Test

There is one controller that is not test with the integration approach that is the `VehiclesController`. This is because of the singularity of it. This controller is not used any moment in the application. The only reason of this controller to exist is to create a unsecured and simple endpoint for make simple to “play” with it. What “play” means is that this endpoint is used to simplified the implementations of some requirements of other subjects.

As I already said the `VehiclesController` is simple, is as simple as it just recieve the call and call the correct function in `VehiclesService` and just manage some exceptions and empty optionals. Whith so, I decided to unit test this service. Here are some test examples:

```java
    @Test
    void testCreateVehicle() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Optional<VehicleInfoDTO> result = vehicleService.createVehicle(testVehicleDTO, testUser);

        assertTrue(result.isPresent());

        assertEquals(testVehicleDTO, result.get());

    }

    @Test
    void testGetVehicleOfUserInfo() {
        when(vehicleRepository.findByUserInfoId(testUser.getId())).thenReturn(Optional.of(testVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleOfUserInfo(testUser);

        assertTrue(result.isPresent());
        assertEquals(testVehicle, result.get());
    }

    @Test
    void testGetAuthenticatedVehicle() {
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(vehicleRepository.findByUserInfoId(testUser.getId())).thenReturn(Optional.of(testVehicle));

        Vehicle result = vehicleService.getAuthenticatedVehicle();

        assertEquals(testVehicle, result);
    }
```

# Aspects

The project implements in total 6 aspects. The scope of the aspects is divided in two main things:

1. Validation. Five of the aspects the single reason they exist is to validate the input data of all functions of the services.
    1. `DriverValidatorAspect` it executer before the execution of the function `DriverService.createDriver(..))`. It validates that the `DriverDTO` class that is in the input of the function is correct
    2. `EmailValidatorAspect` it executes before the execution of the following three functions. It validates that the input is an email by calling the `EmailValidator` class. The functions are:
        1. `DriverService.associateDriver(String)`
        2. `DriverService.disassociateDriver(String)`
        3. `DriverService.getByEmail(String)`
    3. `ReportValidationAspect` it executes in two situations. In both cases it validates the input.
        1. Before the accept report request in the function `ReportService.acceptReport(..)`
        2. Before the open report request in the function `ReportService.openAReport(..)`
    4. `AuthRequestValidatorAspect` it validates every login request. It executes before `AuthService.login(..)`
    5. `SignUpVehicleValidatorAspect` validates only the registration input of a vehicle before the execution of the function `AuthService.registerVehicle(..)`
    
    ```java
    @Aspect
    @Component
    public class DriverValidatorAspect {
    
        @Autowired
        private DriverRepository driverRepository;
    
        @Autowired
        private EmailValidator emailValidator;
    
        @Pointcut("execution(* hr.algebra.insurancebackend.service.DriverService.createDriver(..))")
        public void createDriverPointcut() {}
    
        @Before("createDriverPointcut() && args(driverDTO)")
    
        public void validateDriverDTO(DriverDTO driverDTO) throws ValidationException {
            // More validations
            if (driverDTO.getEmail() == null || driverDTO.getEmail().isBlank()) {
                throw new ValidationException("Driver's email cannot be empty");
            }
            if (!emailValidator.isValidEmail(driverDTO.getEmail())) {
                throw new ValidationException("Invalid email format");
            }
    	      // More Validations
    
        }
    }
    ```
    
2. The purpose of the other aspect (`JwtResponseAspect`) is to standarize the output of the registration. Basically it wraps the returned value of the registration function with the refresh and access token:
    
    ```java
    public class AccessTokenWrapper<T> {
    
        private T wrapped;
    
        private JwtResponseDTO token;
    
    }
    ```
    
    The advantages of this approach is that in the future if I want to add for example the driver registration function, I could just extend the aspect so it manages that function to. But I won’t need to code the logic of creating the token again.
    
    ```java
    @Aspect
    @Component
    public class JwtResponseAspect {
        @Autowired
        private RefreshTokenService refreshTokenService;
    
        @Around("execution(* hr.algebra.insurancebackend.security.controller.AuthController.registrationOfVehicleAndGetToken(..))")
        public AccessTokenWrapper authControllerRegisterMethod(ProceedingJoinPoint joinPoint) throws Throwable {
            AccessTokenWrapper result = (AccessTokenWrapper) joinPoint.proceed();
            Object wrapped = result.getWrapped();
            if (wrapped instanceof UsernameProvider usernameProvider) {
                String username = usernameProvider.getUsername();
                JwtResponseDTO refreshTokenAndGenerateResponse = refreshTokenService.createRefreshTokenAndToken(username);
                return AccessTokenWrapper
                        .builder()
                        .wrapped(wrapped)
                        .token(refreshTokenAndGenerateResponse)
                        .build();
            } else {
                throw new RuntimeException("Something went wrong in the server");
            }
        }
    }
    ```
    

# Git

The strategy used for the backend development follows a Git branching model known as "Feature Branch Workflow". Here's a more detailed explanation:

## **Feature Branch Workflow**

### **Branching Model**

Each new feature, bug fix, or requirement is developed in its own branch. This isolates changes related to specific tasks from the main development line. When a new task is started, a new branch is created from the main development branch (**`main`**). This branch has a descriptive name such as `reports`, `drives`, `vehicles-for-interoperability`.

### **Development Cycle**

Task and requirement are develop as descrived in advance. Doing commits during development is crucial so work is saved and can be tracked. After development and testing, branches are pushed and shared.

### **Code Review and Integration**

Once the feature is complete or the task is finished, a pull request (PR) is opened to merge their feature branch into the `main` branch. If there where other team members, they could review the code changes in the PR, providing feedback, suggestions, or approval.

### **Merge and Deployment**

After the PR is approved and any necessary changes are addressed, the feature branch is merged into the main development branch. The changes are now part of the `main` branch and are available for further testing, integration and improvement.

### **Benefits:**

- **Isolation**: Each feature or task is developed in isolation, reducing the risk of conflicts with other changes and making it easier to manage the complexity of the codebase.
- **Collaboration**: Developers can work on different features concurrently without interfering with each other's work. Pull requests facilitate collaboration and code review.
- **Traceability**: Changes are organized into separate branches, providing a clear history of the development process and allowing developers to track progress and understand the context of each change.

![Untitled](images/Untitled%202.png)

# SOLID

Here are some examples of SOLID principles followed for developing the solution

## Example 1

Here is a good example of the Open/Closed Principle. In the frontend, when we want to filter reports, we have two options for developing a solution:

1. Having separate functions for each type of filter: one for getting all reports, another for getting reports waiting for acceptance or rejection, another for getting rejected reports, and another for accepted reports.
2. Using a **`switch`** statement that handles all types of filters.

<aside>
<img src="https://www.notion.so/icons/light-bulb_blue.svg" alt="https://www.notion.so/icons/light-bulb_blue.svg" width="40px" /> The Open/Closed Principle suggest that classes should be open for extension but closed for modification.

</aside>

Both solutions have the same problem: they are not maintainable. Imagine we wanted to add a new status for the reports. We would have to add a new function or a new case to the **`switch`** statement.

To solve this, we create a class for each type of report status, all extending the same class or implementing the same interface. In my project, it is solved as follows:

```java
public enum ReportService {
    INSTANCE;
....
    public List<ReportDTO> getReportsFiltered(ReportStatusFilter filter) throws IllegalAccessException {
        HttpEntity<String> entity = new HttpEntity<>(AuthService.INSTANCE.getAuthHeader());

        RestTemplate restTemplate = new RestTemplate();
        ParameterizedTypeReference<List<ReportDTO>> responseType = new ParameterizedTypeReference<List<ReportDTO>>() {};

        ResponseEntity<List<ReportDTO>> responseEntity = restTemplate.exchange(
                BASIC_URL+filter.getUrl(),
                HttpMethod.GET,
                entity,
                responseType
        );
        return responseEntity.getBody();
    }
    ....
}

public enum ReportStatusFilter {
    ALL("All", "", ""), 
    WAITING("Waiting", "/waiting", "#312e81"), 
    REJECTED("Rejected", "/rejected", "#881337"), 
    ACCEPTED("Accepted", "/accepted", "#064e3b");

    private String text;
    private String url;

    private String color;

    ReportStatusFilter(String text, String url, String color) {
        this.text = text;
        this.url = url;
        this.color=color;
    }
    
    public String getUrl() {
        return url;
    }
    ....
}
```

<aside>
<img src="https://www.notion.so/icons/light-bulb_blue.svg" alt="https://www.notion.so/icons/light-bulb_blue.svg" width="40px" /> As you may notice all my Services in the frontend are Singletons

</aside>

## Example 2

As you may notice we follow another principle just with the very same example in before. Becouse we as a client when we get a `ReportStatus` we may just need the url or the color. For that we can follow the Interface Segregation principle that suggest to have small Interfaces that segregate the classes into functions. In the project is solved as followed:

```java
public enum ReportStatus implements ReportStatusFilter, ReportStatusColor {
    ALL("All", "", ""),
    WAITING("Waiting", "/waiting", "#312e81"),
    REJECTED("Rejected", "/rejected", "#881337"),
    ACCEPTED("Accepted", "/accepted", "#064e3b");

    private final String text;
    private final String url;

    private final String color;

    ReportStatus(String text, String url, String color) {
        this.text = text;
        this.url = url;
        this.color=color;
    }

    public static List<ReportStatus> getAll(){
        return Arrays.asList(values());
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public Color getColor() {
        return Color.web(color);
    }
}

public interface ReportStatusFilter {
    String getUrl();
}

public interface ReportStatusColor {
    Color getColor();
}

//example usages
//in ReportService
public List<ReportDTO> getReportsFiltered(ReportStatusFilter filter) throws IllegalAccessException {
        HttpEntity<String> entity = new HttpEntity<>(AuthService.INSTANCE.getAuthHeader());

        RestTemplate restTemplate = new RestTemplate();
        ParameterizedTypeReference<List<ReportDTO>> responseType = new ParameterizedTypeReference<List<ReportDTO>>() {};

        ResponseEntity<List<ReportDTO>> responseEntity = restTemplate.exchange(
                BASIC_URL+filter.getUrl(),
                HttpMethod.GET,
                entity,
                responseType
        );
        return responseEntity.getBody();
    }
```

## Example 3

An example of the Dependency Inversion Principle. The DIP seeks for the decoupling of software modules. This way, instead of high-level module depending on low-level modules, both will depened on abstractions. In the project we can see this principles in all repositories and services. Every repository or service has an interface an a class implementing it. Moreover, Spring helps in this task since you don’t have to determine what implementation the classes need to take if there is only one implementation. So for example we have the `AuthService` interface and the `AuthServiceImpl` that implements that class:

```java
public interface AuthService {
    JwtResponseDTO login(AuthRequestDTO authRequestDTO);

    AccessTokenWrapper<VehicleInfoDTO> registerVehicle(SignUpVehicleDTO signUpVehicleDTO);

    String getCurrentUsername();

    UserInfo getCurrentUser();
}

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;
    private RegisterUserService registerUserService;

    @Lazy
    private VehicleService vehicleService;
    private RefreshTokenService refreshTokenService;

    @Override
    public JwtResponseDTO login(AuthRequestDTO authRequestDTO) {
        try {
            Authentication authenticationUsername = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
            if (authenticationUsername.isAuthenticated()) {
                return refreshTokenService.createRefreshTokenAndToken(authRequestDTO.getUsername());
            } else {
                throw new UsernameNotFoundException("invalid user request..!!");
            }
        }catch (AuthenticationException e){
            throw new UsernameNotFoundException("invalid user request..!!");
        }

    }
    ....
 }
 
 //An example of how we use this class in the controller
 @RestController
@RequestMapping("auth")
@AllArgsConstructor
@Validated
public class AuthController {

    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    private TokenBlackListService tokenBlacklist;

    @PostMapping("/api/v1/login")
    public Object authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        try{
            return authService.login(authRequestDTO);
        }catch (UsernameNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    ....
 }
```

## Example 4

The following example is the crown jewel. It follows 3 principles in three classes:

```java
public abstract class Serializer <T> {

    protected void save(T classToSave, String location){
        try (FileOutputStream fileOut = new FileOutputStream(location);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(classToSave);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    protected T load(String location) {
        T classToLoad = null;
        try (FileInputStream fileIn = new FileInputStream(location);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            classToLoad= (T) in.readObject();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
        return classToLoad;
    }

    public abstract void save(T classToSave);
    public abstract T load();
}

public class TokenSerializer extends Serializer<Token>{
    private static final String SERIALIZATION_FOR_TOKEN_LOCATION="data/token.ser";

    @Override
    public void save(Token classToSave) {
        this.save(classToSave, SERIALIZATION_FOR_TOKEN_LOCATION);
    }

    @Override
    public Token load() {
        return load(SERIALIZATION_FOR_TOKEN_LOCATION);
    }
}

public class VehicleSerializer extends Serializer<VehicleInfoDTO>{
    public static final String SERIALIZATION_FOR_AUTHENTICATED_VEHICLE_LOCATION="data/authenticated-vehicle.ser";

    @Override
    public void save(VehicleInfoDTO classToSave) {
        this.save(classToSave, SERIALIZATION_FOR_AUTHENTICATED_VEHICLE_LOCATION);
    }

    @Override
    public VehicleInfoDTO load() {
        return load(SERIALIZATION_FOR_AUTHENTICATED_VEHICLE_LOCATION);
    }
}
```

This code is part of the frontend of javafx and is used for saving the current loging user for the future. The principles followed are:

1. **Single Responsibility Principle (SRP)**:
    - Each class has a single responsibility.
    - **`Serializer`** is responsible for generic serialization operations.
    - **`TokenSerializer`** and **`VehicleSerializer`** are responsible for serializing specific types of objects (**`Token`** and **`VehicleInfoDTO`**, respectively).
2. **Open/Closed Principle (OCP)**:
    - The **`Serializer`** class is open for extension and closed for modification. It provides abstract methods (**`save`** and **`load`**) that subclasses must implement, allowing for extension to support serialization of different types of objects without modifying the existing code.
3. **Liskov Substitution Principle (LSP)**:
    - The subclasses (**`TokenSerializer`** and **`VehicleSerializer`**) can be substituted for their base class (**`Serializer`**) without affecting the behavior of the program.

# **Functional Programming**

Utilizing functional programming in Spring aids in handling **`null`** values, transforming data, and avoiding side effects. Here are some examples demonstrating how I transformed purely object-oriented functions into purely functional ones.

In my project, all the functions in **`DriverService`** employ a functional programming approach. Below is an overview of these functions:

## **Example 1**

The **`createDriver`** function in the **`DriverService`** class originally looks like this in a non-functional approach:

```java
@Override
public Optional<DriverDTO> createDriver(DriverDTO driverDTO) throws ValidationException {
    try {
        checkDriverDTO(driverDTO);
    } catch (IllegalArgumentException e) {
        return Optional.empty();
    }
    // Get vehicle
    Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
    Driver driver = new Driver(driverDTO);

    // SAVE
    Driver savedDriver = driverRepository.save(driver);
    vehicle.getDrivers().add(savedDriver);
    vehicleRepository.save(vehicle);

    return Optional.of(new DriverDTO(savedDriver));
}

```

To better handle **`Optional`** and exceptions, I transformed it into a purely functional approach:

```java
@Override
public Optional<DriverDTO> createDriver(DriverDTO driverDTO) throws ValidationException {
    return Optional.ofNullable(driverDTO)
            .filter(dto -> {
                try {
                    checkDriverDTO(dto);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            })
            .map(dto -> {
                Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
                Driver driver = new Driver(dto);
                Driver savedDriver = driverRepository.save(driver);
                vehicle.getDrivers().add(savedDriver);
                vehicleRepository.save(vehicle);
                return new DriverDTO(savedDriver);
            });
}

```

- **`Optional.ofNullable(driverDTO)`** ensures that **`driverDTO`** is not **`null`**.
- **`.filter(dto -> { ... })`** checks if **`checkDriverDTO`** throws an exception and only allows continuation if it doesn't.
- **`.map(dto -> { ... })`** performs all necessary operations to create and save the **`Driver`**, and finally returns the created **`DriverDTO`**.

## **Example 2**

Here is how a **`getById`** function might look in an object-oriented approach:

```java
@Override
public Optional<DriverDTO> getById(Long id) {
    Optional<Driver> byId = driverRepository.findById(id);
    return byId.isPresent() ?
           Optional.of(new DriverDTO(byId.get()))
           :
           Optional.empty();
}

```

In a functional programming approach, it looks like this:

```java
@Override
public Optional<DriverDTO> getById(Long id) {
    return Optional.ofNullable(id)
                   .flatMap(driverRepository::findById)
                   .map(DriverDTO::new);
}

```

Similarly, the **`getByEmail`** function:

```java
javaCopiar código
@Override
public Optional<DriverDTO> getByEmail(String email) throws ValidationException {
    return Optional.ofNullable(email)
                   .flatMap(driverRepository::findByEmail)
                   .map(DriverDTO::new);
}

```

## **Example 3**

Here is how the **`getAllDriversOfAuthenticatedVehicle`** function looks in an almost purely functional programming approach:

```java
@Override
public List<DriverDTO> getAllDriversOfAuthenticatedVehicle() {
    Vehicle vehicle = vehicleService.getAuthenticatedVehicle();

    return vehicle.getDrivers()
                  .stream()
                  .map(DriverDTO::new)
                  .toList();
}

```

And here is how it looks in a more functional approach:

```java
@Override
public List<DriverDTO> getAllDriversOfAuthenticatedVehicle() {
    return Optional.ofNullable(vehicleService.getAuthenticatedVehicle())
                   .map(Vehicle::getDrivers)
                   .stream()
                   .flatMap(Set::stream)
                   .map(DriverDTO::new)
                   .toList();
}

```

## **Example 4**

Sometimes, functional programming may not look more concise than the non-functional approach. Here is the original function:

```java
private void checkDriverDTO(DriverDTO driverDTO) throws IllegalArgumentException {
    if (driverRepository.findByEmail(driverDTO.getEmail()).isPresent())
        throw new IllegalArgumentException("Driver with this email already exists");
}

```

And here is the functional approach:

```java
private void checkDriverDTO(DriverDTO driverDTO) throws IllegalArgumentException {
    Optional.ofNullable(driverDTO)
            .map(DriverDTO::getEmail)
            .flatMap(driverRepository::findByEmail)
            .ifPresent(driver -> {
                throw new IllegalArgumentException("Driver with this email already exists");
            });
}

```

## **Example 5**

Here are the **`associate`** and **`disassociate`** functions in a conventional Java way:

```java
@Override
public Optional<DriverDTO> associateDriver(String email) throws ValidationException {
    Optional<Driver> byEmail = driverRepository.findByEmail(email);
    if (byEmail.isPresent()) {
        Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
        vehicle.getDrivers().add(byEmail.get());
        vehicleRepository.save(vehicle);
        return driverRepository.findByEmail(email).map(DriverDTO::new);
    }
    return Optional.empty();
}

@Override
public Optional<DriverDTO> disassociateDriver(String email) throws ValidationException {
    Optional<Driver> byEmail = driverRepository.findByEmail(email);
    if (byEmail.isPresent()) {
        Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
        long driverByEmailID = byEmail.get().getId();
        vehicle.getDrivers().removeIf(driver -> driver.getId() == driverByEmailID);
        vehicleRepository.save(vehicle);
        return driverRepository.findByEmail(email).map(DriverDTO::new);
    }
    return Optional.empty();
}

```

And here is the functional approach:

```java
@Override
public Optional<DriverDTO> associateDriver(String email) throws ValidationException {
    return Optional.ofNullable(email)
                   .flatMap(driverRepository::findByEmail)
                   .map(driver -> {
                       Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
                       vehicle.getDrivers().add(driver);
                       vehicleRepository.save(vehicle);
                       return new DriverDTO(driver);
                   });
}

@Override
public Optional<DriverDTO> disassociateDriver(String email) throws ValidationException {
    return Optional.ofNullable(email)
                   .flatMap(driverRepository::findByEmail)
                   .map(driver -> {
                       Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
                       vehicle.getDrivers().removeIf(d -> d.getId() == driver.getId());
                       vehicleRepository.save(vehicle);
                       return new DriverDTO(driver);
                   });
}
```

# Metrics

In the realm of software development, especially for applications that manage critical functionalities like vehicle and driver management, monitoring and analyzing application metrics is indispensable. Metrics provide a quantifiable measure of various aspects of an application's performance, reliability, and efficiency. By collecting and interpreting these metrics, development and operations teams can ensure the smooth operation of the application, quickly identify and resolve issues, and make informed decisions for future improvements.

Metrics are collected by Prometheus and displayed in Graphana. All graphics show up in the section are exact moments in Graphana. The application expose the `/actuator/prometheus` endpoint, so Prometheus can consume it.

## Request Count

Counts the number of logins, logouts and register

![The green line are the login request, the yellow the logout and the blue the register](images/Untitled%203.png)

The green line are the login request, the yellow the logout and the blue the register

In the code, it has been defined in a configuration class each of the counter beans, and before each call the counter is implemented:

```java
@Configuration
public class AuthMetricsConfig { 
	//....

    @Bean
    public Counter loginCounter(MeterRegistry meterRegistry) {
        return Counter.builder("auth.login")
                .description("Number of login requests")
                .register(meterRegistry);
    }
 	//....
}
```

```java
@RestController
@RequestMapping("auth")
@AllArgsConstructor
@Validated
public class AuthController {

    //....

    private final Counter loginCounter;

    @PostMapping("/api/v1/login")
    public Object authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO){
        try {
            loginCounter.increment();
            return authService.login(authRequestDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    //....
}
```

## Error Count

This metrics count the number of failures in the driver’s creation function. This way can be track how many times the user has made a mistake in the form. In case this metric is to high, the form and the creation process must be changed.

![Untitled](images/Untitled%204.png)

In code is similar to the previous metric. The counter is defined, in this case, in a drivers metrics configuration class and the is incrementen in each of the times the function of creation throws an exception:

```java
@Configuration
public class DriversMetricsConfig {

    @Bean
    public Counter createDriverFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("driver.create.failure")
                .description("Number of failures in creating drivers")
                .register(meterRegistry);
    }
    //...
 }
```

```java
@RestController
@RequestMapping("driver")
@AllArgsConstructor
public class DriverController {
    //...
    
    private final Counter createDriverFailureCounter;
    
    @PostMapping
    public DriverDTO createDriver(@RequestBody DriverDTO driverDTO) {
        try {
            return driverService.createDriver(driverDTO)
                    .orElseThrow(() -> {
                        createDriverFailureCounter.increment();
                        return new ResponseStatusException(HttpStatus.CONFLICT, "Something went wrong");
                    });
        } catch (ValidationException e) {
            createDriverFailureCounter.increment();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    //...
}
```

## Response Time

The present metric measure the average time taken  for each get request in the `/drivers` endpoint. As this are one of the most commonly used features of the application it is crucial for them to be optimal. With the metric, it is tried to monitor this, so if users complains or we notice that the call are slower, they can be optimiced and tackle the problem.

![Untitled](images/Untitled%205.png)

In the code, time counters are defined in the configuration class and used in the `DriversController`, in each of the getters calls.  When calling a getter the `Timer` class record the time it takes to complete the function.

```java
@Configuration
public class DriversMetricsConfig {
		//....
    @Bean
    public Timer getDriverByEmailTimer(MeterRegistry meterRegistry) {
        return Timer.builder("driver.get.byEmail.time")
                .description("Time taken to get driver by email")
                .register(meterRegistry);
    }
    //....
}
```

```java
@RestController
@RequestMapping("driver")
@AllArgsConstructor
public class DriverController {
		//....
    
    private final Timer getDriverByEmailTimer;
    
    @GetMapping("/byEmail")
    public DriverDTO getDriverByEmail(@RequestParam("email") String email) {
        return getDriverByEmailTimer.record(() ->
                {
                    try {
                        return driverService.getByEmail(email)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email));
                    } catch (ValidationException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                    }
                }
        );
    }
    //....
}
```

## Concurrency

The following metric monitors the number of concurrent requests being handled in a associate and dissociate endpoints in the `/drivers` controller. The utility of this metrics is to track when user are more used to make this calls and monitor if this could be a problem so can be tackle quickly.

![Untitled](images/Untitled%206.png)

In development the  `AtomicInteger` class is defined and exported to the prometheus as a `Gauge`. When on of the call starts, the `AtomicInteger` is incremented and when finished is decremented.

```java

@Configuration
public class DriversMetricsConfig {
		//...
    @Bean
    public AtomicInteger associateDriverConcurrency() {
        return new AtomicInteger(0);
    }

    @Bean
    public Gauge associateDriverConcurrencyGauge(AtomicInteger associateDriverConcurrency, MeterRegistry meterRegistry) {
        return Gauge.builder("driver.associate.concurrency", associateDriverConcurrency, AtomicInteger::get)
                .description("Number of concurrent associateDriver requests")
                .register(meterRegistry);
    }
    //...
}
```

```java
@RestController
@RequestMapping("driver")
@AllArgsConstructor
public class DriverController {
		//...
		
		private final AtomicInteger associateDriverConcurrency;

    @PostMapping("/associate")
    public DriverDTO associateDriver(@RequestBody EmailRequestDTO email) {
        associateDriverConcurrency.incrementAndGet();
        try {
            return driverService.associateDriver(email.getEmail())
                    .orElseThrow(() -> {
                        associateDriverConcurrency.decrementAndGet();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, DRIVER_NOT_FOUND_EMAIL + email);
                    });
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } finally {
            associateDriverConcurrency.decrementAndGet();
        }
    } 
    //..
}
```

## Report Per Vehicle

This metric calculte the average number of report of each type (accepted, rejected and waiting) per vehicle. This metric can be used to make some bussiness reports and presentations. If the count is to high, can mean two things: 1) There are to many reports for a little users 2) The users are using a lot the product. Also, understanding the average reports a user makes, can be prevented the resources will need to have when this number of users increase.

![Untitled](images/Untitled%207.png)

In code is made simply. It is only implemented in the configuration class and whenever the `/actuator/prometheus` class is call. The function calls the `vehicleService.getNumOfVehicles()` and the `reportService.getNumOfReports()` functions (there is a function for each of the report’s type) to get the total number and calculate the average.

```java
@Configuration
public class ReportMetricsConfig {
		//...
    @Bean
    public Gauge averageReportsPerVehicleGauge(ReportService reportService, VehicleService vehicleService, MeterRegistry meterRegistry) {
        return Gauge.builder("reports.per.vehicle.avg", reportService, rs -> {
                    long numberOfVehicles = vehicleService.getNumOfVehicles();
                    long numberOfOpenedReports = reportService.getNumOfReports();
                    return numberOfVehicles == 0 ? 0 : (double) numberOfOpenedReports / numberOfVehicles;
                })
                .description("Average number of reports per vehicle")
                .register(meterRegistry);
    }
    //...
}
```

# Docker

Be able to dockerize an application is a crucial skill in nowadays world. For so I have containericed my application. As the project consist in both parts, backend in Spring Boot and frontend in JavaFx, I needed to try to containerice both separately which is the main goal of containeraization (have non monolotic applications). 

The Spring Boot server are an ideal candidate for Docker since are designed to run in headless enviroments. They don't require a GUI and can be easily managed using configuration files and scripts. As Docker is particularly useful for microservices architecture, where each service runs in its own container. The Spring Boot backend can be one of these services. Also deploying and managing backend services in containers is straightforward using container orchestration tools like Kubernetes.

The JavaFX frontend is more complex to containerize than the backend, as it requires a GUI, which Docker containers are not designed to handle efficiently. Running GUI applications inside containers can introduce performance overhead and latency, making the user experience suboptimal. For setting up the necessary environment for JavaFX (JDK, JavaFX libraries, display server, etc.) inside a container adds significant complexity and maintenance overhead.

## How Was the Server Containerized?

1. **Clean the Project**:
    
    ```bash
    mvn clean project
    ```
    
    This command cleans the project by removing all files generated by the previous build.
    
2. **Create the Dockerfile**:
    
    ```
    FROM openjdk:21-slim
    MAINTAINER cserranogut@gmail.com
    COPY target/*.jar app.jar
    ENTRYPOINT ["java","-jar","/app.jar"]
    ```
    
    - `FROM openjdk:21-slim`: Specifies the base image with JDK 21 on a slim version of Debian.
    - `MAINTAINER cserranogut@gmail.com`: Specifies the maintainer of the Dockerfile.
    - `COPY target/*.jar app.jar`: Copies the JAR file from the target directory into the container.
    - `ENTRYPOINT ["java","-jar","/app.jar"]`: Specifies the command to run the JAR file.
3. **Build the Docker Image**:
    
    ```bash
    docker build -t insurance-backend .
    ```
    
    This command builds the Docker image from the Dockerfile in the current directory (`.`) and tags it as `insurance-backend`.
    
4. **Run the Docker Container**:
    
    ```bash
    docker run -p 8082:8081 insurance-backend
    ```
    
    This command runs a container from the `insurance-backend` image, mapping port 8082 on the host to port 8081 in the container.
    
5. **Tag the Docker Image**:
    
    ```bash
    docker image tag insurance-backend:latest mrcharlessg/insurance-backend:latest
    
    ```
    
    This command tags the local `insurance-backend` image for pushing to Docker Hub.
    
6. **Push the Docker Image to Docker Hub**:
    
    ```bash
    docker image push mrcharlessg/insurance-backend:latest
    ```
    
    This command pushes the tagged image to the Docker Hub repository.
    

## How to Use the Image?

1. **Pull the Image**:
    
    ```bash
    docker pull mrcharlessg/insurance-backend
    ```
    
    This command pulls the `insurance-backend` image from Docker Hub.
    
2. **Run the Image**:
    
    ```bash
    docker run -p 8082:8081 mrcharlessg/insurance-backend
    ```
    
    This command runs a container from the `mrcharlessg/insurance-backend` image, mapping port 8082 on the host to port 8081 in the container.
    
3. **Run the JavaFX Frontend**:
Pull the code from [GitHub](https://github.com/MrCharlesSG/Insurance-Frontend-JavaFX) and run it. The JavaFX application will automatically connect to the Dockerized Spring Boot server if the port is 8082.