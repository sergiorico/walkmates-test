# WalkMates — Redesign Rationale (HT26)

Why this repo exists and how it is shaped. (Instructor-facing; safe to keep in the student repo —
it explains the design, not the answers. Seeded-fault details live in the separate instructor key.)

## Why a new system

WalkMates replaces the long-running **BikeShare** teaching system. Four problems drove the change:

1. **Domain complexity got in the way of teaching testing.** BikeShare's billing/peak-hour/
   membership rules took real effort to understand before you could test them. WalkMates is
   explainable in one sentence — *people matched with animal-care opportunities* — and keeps all
   rules numeric and boundary-friendly.
2. **No day-one win.** WalkMates builds green and ships `FIRST_TEST_TUTORIAL.md` + a passing
   starter test so students write and run a test in the first session.
3. **AI trivialises naive lab completion.** AI is now *encouraged*; value moves to reflection and
   judgment. CI auto-checks the mechanics (build/coverage/mutation) so instructor effort goes to
   reading the per-lab reflection.
4. **Backend-only scope.** WalkMates adds a thin Spring Boot REST layer + a mockable **LLM seam**
   (`MatchExplanationService`), so the labs reach interface testing and testing-AI topics.

BikeShare is **harvested, not retired**: the `PaymentService`/`NotificationService` seam pattern,
the `REQUIREMENTS.md` shape, and the Luhn/personnummer validator port over directly.

## Course shape

Four pair labs (down from six), module-aligned, released together in wk36; CI grades mechanics,
the instructor grades reflections. See [`lab-instructions/00-GENERAL-INSTRUCTIONS.md`](lab-instructions/00-GENERAL-INSTRUCTIONS.md).

## The design spine: a natural test-level progression

The four labs are organised by technique, but the content walks up the test-level ladder:

| Rung | Lab | What students do |
|---|---|---|
| Run the system / first test | 1A | `mvn test` green, launch app, first unit test |
| Spec-based unit tests | 1B | EP / BVA / decision tables on `Seeker` |
| Structural unit testing | 2A | branch coverage + a covered-but-buggy path on `PricingCalculator` |
| Component isolation + integration | 2B | mock the seams, kill mutants, regression selection on `BookingService` |
| Feature / interface testing | 3 | the AI feature: prompt-building, fallback, metamorphic, injection, `MockMvc` |
| Societal reflection | 4 | SUsAF + ethics (paper) |

## Domain → testable seams

| Entity | Role | Lab use |
|---|---|---|
| `Seeker` | person wanting an animal experience; trust tiers + wallet | Lab 1 spec-based target (kept correct) |
| `Provider` | owner/shelter; location + capacity | booking decision rule (FR-4.4) |
| `Listing` | a care opportunity; type + state machine | Lab 2 structural (state transitions) |
| `Booking` | a reservation; lifecycle + price | Lab 2 component isolation |
| `PricingCalculator` | FR-4.3 pricing | Lab 2 coverage + mutation target |
| `PaymentService` / `NotificationService` | external seams | Lab 2 mocking (success/decline/timeout) |
| `LlmClient` / `MatchExplanationService` | the AI feature behind an interface | Lab 3 |

The system is built to **expose testable surfaces**, optimising clarity/testability over domain
realism. A few subtle faults are seeded so the shipped suite is green but real work remains for
Lab 2 — documented in the instructor key (kept outside this repo).

## Stack

Java 21 · Maven · Spring Boot · H2 · JUnit 5 · Mockito · AssertJ · JaCoCo · PIT · GitHub Actions.
CI is a first-class deliverable: see [`.github/workflows/ci.yml`](.github/workflows/ci.yml), which
posts coverage % and mutation score to the job summary and PR comments.
