# Your First WalkMates Test (10 minutes)

This is your **day-one win**. By the end you will have run a passing test and written one of
your own — no deep setup, no reading the whole codebase first.

## 1. Check your tools (2 min)

```bash
java -version    # should show 21
mvn -version     # should show Maven 3.8+ AND Java 21
```

Both commands must use Java 21; having a different JDK installed is not enough if Maven reports
another Java version. If Java 21 is missing, download a JDK 21 build such as
[Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21), then configure your
`JAVA_HOME` and `PATH` to point to that installation. Restart the terminal and run both checks
again. On a university-managed computer where you cannot install software, use the course lab
environment or contact course support.

The build stops early with a clear message when Maven is using an incompatible Java or Maven
version. Once both checks pass, clone the pair repository provided for the course and you are
ready. The GitHub Student Developer Pack is optional.

## 2. Build and run the whole test suite (2 min)

```bash
mvn clean test
```

You should see `BUILD SUCCESS`. The project ships with passing tests so you know your
environment is healthy before you change anything.

## 3. Run just the starter test (1 min)

```bash
mvn test -Dtest=BeginnerFirstTest
```

Open it: [`src/test/java/com/walkmates/lab1/BeginnerFirstTest.java`](src/test/java/com/walkmates/lab1/BeginnerFirstTest.java).
Every test has three parts:

- **Arrange** — set up the objects you need.
- **Act** — call the method you want to test.
- **Assert** — check the result is what you expected.

## 4. See the system run (optional, 2 min)

```bash
mvn spring-boot:run
# then open http://localhost:8080
```

You'll see the seeded animal-care opportunities. The REST API is at `/api/listings`,
`/api/bookings`, and `/api/match/{seekerId}/explain?listingId=...`.

## 5. Write your own test (3 min)

Open [`SeekerSpecBasedTest.java`](src/test/java/com/walkmates/lab1/SeekerSpecBasedTest.java) and
add a test. Try this one (the rule is `FR-1.3` in [`docs/REQUIREMENTS.md`](docs/REQUIREMENTS.md)):

```java
@Test
@DisplayName("Adding 250 SEK to a new seeker gives a 250.00 balance")
void addingFundsWorks() {
    Seeker seeker = new Seeker("you@example.com", "You", "0701234567");  // Arrange
    seeker.addFunds(250.00);                                              // Act
    assertThat(seeker.getBalance()).isEqualTo(250.00);                   // Assert
}
```

Run it:

```bash
mvn test -Dtest=SeekerSpecBasedTest
```

Green? You just wrote and ran a real specification-based test. That's Lab 1 Part B — now go
design the full equivalence-partition and boundary-value set from the requirements.

When you reach the phone-number partitions, `+4671234567` is an example of the valid
international format in FR-1.1.

## Where to next

- **Lab 1** — [`lab-instructions/01-LAB1-FUNDAMENTALS.md`](lab-instructions/01-LAB1-FUNDAMENTALS.md)
- New to testing terms? — [`TESTING_GLOSSARY.md`](TESTING_GLOSSARY.md)
