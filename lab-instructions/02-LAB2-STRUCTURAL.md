# Lab 2 — Structural Testing & Test Optimization

> Modules **M3 + M4**. *In the WalkMates repo.* Due **wk 40**. Done in pairs.

## Learning objectives (DV033G)

Apply structural techniques (statement/branch/path coverage); assess test quality using
artifacts (coverage, mutation, logs); apply test optimization (mutation, regression
selection/prioritization, mocking, CI); implement tools/frameworks.

---

## Part A — Structural & coverage (M3)

**Primary assessed target:**
[`PricingCalculator`](../src/main/java/com/walkmates/service/PricingCalculator.java) (FR-4.3).
The status state machines in
[`ListingStatus`](../src/main/java/com/walkmates/model/ListingStatus.java) /
[`BookingStatus`](../src/main/java/com/walkmates/model/BookingStatus.java) are optional additional
structural-testing targets; they are not part of the Lab 2 pass bar.
**Implement in:** [`src/test/java/com/walkmates/lab2/PricingCalculatorStructuralTest.java`](../src/test/java/com/walkmates/lab2/PricingCalculatorStructuralTest.java).

### Activity 3.1 — Measure baseline coverage
```bash
mvn clean test jacoco:report
# open target/site/jacoco/index.html
```
Record the line and branch coverage for `PricingCalculator`. Identify the **uncovered branches**
(free-listing path? overnight-surcharge path?).

### Activity 3.2 — Raise branch coverage
Add tests to cover every branch: a free `SHELTER_VOLUNTEER` listing, a non-overnight booking, and
a clearly-overnight booking (e.g. 600 min).

### Activity 3.3 — Coverage ≠ correctness
Now write a test for a booking of **exactly 480 minutes**. FR-4.3 says the 20% overnight
surcharge applies only when duration is **strictly greater than** 480. What does your test show?
Explain in your reflection how a test could "cover" the surcharge line yet **miss** this bug.

Run this test class locally and record the expected boundary-test failure in your reflection:

```bash
mvn test -Dtest=PricingCalculatorStructuralTest
```

After observing the failure, correct the production fault (`>=` versus `>`), run the complete
suite, and push only a **green** final commit. A red suite prevents both the CI coverage report
and PIT mutation analysis from being produced; the locally observed red test is learning
evidence, not the final repository state.

---

## Part B — Test optimization (M4)

### Activity 4.1 — Mutation testing (PIT)
```bash
mvn clean test org.pitest:pitest-maven:mutationCoverage
# open target/pit-reports/index.html
```
Find **surviving mutants** on `PricingCalculator` and the booking limit logic. Add tests that
**kill** them. Note especially boundary mutants (`>` ↔ `>=`) — these point straight at real
faults in the code.

The booking-limit fault analysed in Lab 1 is resolved here: add the equality-boundary regression
test, observe it fail against the supplied comparison, then correct the production comparison.
Keep the regression test and the correction in the green final repository, just as in Activity
3.3.

> **PIT requires a green suite.** It stops before mutation analysis if any test fails. Always
> run `mvn clean test` successfully before running PIT. In the HTML report, select the target
> package and class to see mutants by source line; `target/pit-reports/mutations.xml` contains
> the same details in machine-readable form.

### Activity 4.2 — Component isolation with mocking
**Targets:** [`BookingService`](../src/main/java/com/walkmates/service/BookingService.java) and
[`SeekerService`](../src/main/java/com/walkmates/service/SeekerService.java).
Using Mockito, mock the repositories and the **seams** —
[`PaymentService`](../src/main/java/com/walkmates/service/PaymentService.java),
[`NotificationService`](../src/main/java/com/walkmates/service/NotificationService.java) — to test
a service in isolation. Cover **success, failure (decline), and timeout** for the payment seam
(`SeekerService.topUp`): assert the wallet is **not** credited when the charge fails. Verify the
confirmation notification is sent on a successful booking.
**Implement in:** `src/test/java/com/walkmates/lab2/`. Create `BookingServiceTest.java` and
either put the `SeekerService.topUp` cases there in a clearly named nested class or create a
separate `SeekerServiceTest.java`; both layouts are acceptable.

### Activity 4.3 — Regression selection
A change is proposed on the branch/diff `feature/weekend-surcharge` (see the instructor's change
scenario, or simulate one by editing `PricingCalculator`). Given the change, **select and
prioritize** which existing tests must re-run and justify the order. Which tests are
change-relevant, and which are wasteful to run every time?

Write the analysis in `lab2-regression-selection.md` at the repository root and summarize or
link to it from your Lab 2 reflection.

---

## CI evidence (automatic)

Build passes; CI posts **coverage %** and **mutation score**. Bar for Lab 2: branch coverage on
`PricingCalculator` clearly improved over baseline, and the boundary mutant(s) killed (your
480-minute test should initially fail against the supplied buggy code—that is the point).
Document that observation locally, correct the fault, and push the passing regression test so
CI can generate the required evidence.

## Submission

Repo link/commit + `lab2-regression-selection.md` + completed
`reflections/lab2-reflection.md`, created from the
[`reflection template`](reflection-template.md).
