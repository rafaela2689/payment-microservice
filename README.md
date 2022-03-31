# Payment processing microservice 

Microservice responsible for processing payments which can be online and offline.

## Prerequisites

* [IntelliJ](https://www.jetbrains.com/idea/) (Optional)
* [Java](https://www.java.com/) 11+
* [Gradle](https://gradle.org/install/) 6.9.1+

## Run

### Gradle

In order to run locally you must run these commands via terminal on its root:

* Build the project:

  ```console
  $ ./gradlew build
  ```

* Run Application:

  ```console
  $ ./gradlew bootRun
  ```

## Testing

* Run Tests:

  ```console
  $ ./gradlew test
  ```

## Improvements

- Save error asynchronous, through a message system like SQS, 
instead of have to do this http request while processing the payment.
- Separate the core and worker in different modules, to be more organized the domains.
- Do integrations test for kafka consumer.

## Assumptions

- I have created a validation for a payment based on the constraints the payment table has.
- I have also, added a validation before sending the request to save log error, not allowing if the `paymentId` and `errorType` are `null`.