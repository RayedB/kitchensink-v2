Here's your document formatted in Markdown:

# Hypothesis
**Assumptions**:
- The legacy app is working as expected, and we can inject logs into it.
- The target app is working as expected, and we need to migrate the data from the legacy app to the target app.
- The target app is using Quarkus.
- We need to replicate 1:1 the business logic of the legacy app.
- There are no stored procedures, triggers, or remote method invocation in the legacy app.
- The legacy app can afford downtime for the migration if it is done at night or over the weekend.

## Migration Methodology

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

## Legacy app
Based on logs, generate integration tests for legacy app (running locally)

## Target app
Generate integration tests for the target app

### Step 5: Code module in the target app
Hypothesis (Java driver vs Spring data)

### Step 6: Code translator module

### Step 7: Batch migrate