# Hypothesis

**Assumptions**:

- The legacy app is working as expected, and we can inject logs into it.
- The legacy app is behind a API Gateway/load balancer upon which we can edit some rules.
- The target app is working as expected, and we need to migrate the data from the legacy app to the target app.
- The target app is using Quarkus.
- We need to replicate 1:1 the business logic of the legacy app.
- There are no stored procedures, triggers, or remote method invocation in the legacy app.
- The legacy cannot afford downtime.

## Migration Methodology

The selected option for migrating this application is to lambda loading at the api-gateway level.
During the assessment phase, it is possible to take an incoming request and feed it parallelly to the legacy app and the target app so we can run integration tests and check integrity of the data on each system.

Once the team is confident about the implementation of the target app, we can redirect the requests to the target app only.

In a real world scenario, there might be some Anti Corruption Layer, or "Translator" that would make sure legacy apps querying the gateway still offer the same contract as the legacy app offered.
It would be in a separate container as this is a "disposable" component that would be stopped once the legacy apps have been modernized or replaced to query directly the target app.

For this exercise I decided to create a nodejs express script that would act as load-balancer to perform the Dark Pattern, propagating the request to both the legacy and modern app

### Step 1: Setup the legacy environment

**Purpose**: Create a replicable version of the legacy app on a developer's machine.
Ideally dockerize the legacy app.
For this challenge, I opted to run the legacy app locally without docker, using the latest version of Wildfly, Java 11 and Maven 3.8.8

### Step 2: Inject logs into legacy app
**Purpose**: Understand legacy application behavior and expected input-output of a "single module"

**Tasks**:
- Inject logs to trace requests to responses
- Simulate production like traffic
- Analyze dependencies, dependencies to internal packages

**Investigation**:
- Is the expected input/output of a "single module" entirely executed from the codebase ?
- If there are discrepancies, between expected input/output, there is some business logic executed outside the codebase (stored procedures, triggers, remote method invocation)

**Actions**:
- Created a util `RequestIdGenerator` to generate a random request id for each request
- Generate tests for the target application

### Step 2.2: Audit database (not applicable for this challenge)
- Index analysis
- Triggers
- Stored procedures
- Schedulers/Cron jobs
- TTL

### Step 3: Setup boilerplate for target application

See the code in this repository

### Step 4: Generate integration tests

#### Legacy app

Based on logs, generate integration tests for legacy app (running locally)

#### Target app

Generate integration tests for the target app

### Step 5: Code module in the target app

Hypothesis (Java driver vs Spring data)

### Step 6: Code translator module (not applicable in this challenge)

In a real world scenario, there are some App/API contracts that legacy apps have when interacting with the application we are modernizing.
Respecting this contract is necessary to allow a smooth migration without requiring all the ecosystem to make too many changes.

However since this contract is purely existant because of the legacy ecosystem and doesn't have business benefits in a modernized way, it is a good practice to create this "adapter" module as a separate software. This way we would be able to dispose of the translator module and we don't have legacy related code "polluting" our modernized codebase.

### Step 7: Migrate data at runtime

**Purpose**: Make historical data available to the new system without requiring too much downtime.

**Tasks**:

- For each Member creation into legacy app, create a Member in the target app
- For each /GET/:id into the target app, if the member does not exist, query the legacy app, store the result in the target app and return the result

### Step 8: Migrate non-queried data as batches (not applicable for this challenge)
Using Relational Migrator, migrate data from legacyDB to MongoDB