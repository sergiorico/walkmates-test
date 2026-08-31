# Testing Glossary (WalkMates)

Plain-language definitions of the terms used across the labs.

- **Unit test** — tests one small piece (a method/class) in isolation. Most WalkMates tests are
  unit tests on `Seeker`, `PricingCalculator`, etc.
- **Arrange–Act–Assert (AAA)** — the three parts of a test: set up, do the thing, check the result.
- **Equivalence Partitioning (EP)** — group inputs that should behave the same and test one from
  each group, instead of every possible value.
- **Boundary Value Analysis (BVA)** — test right at the edges of a rule (just below / at / just
  above), where bugs love to hide (e.g. the 480-minute overnight boundary).
- **Decision table** — a grid of input conditions → expected outcome, used when several
  conditions combine (e.g. trust tier × booking state).
- **Statement coverage** — did each line of code run during the tests?
- **Branch coverage** — did each decision (if/else, switch arm) go both ways?
- **Coverage ≠ correctness** — a line can be "covered" by a test that never checks the value it
  produces, so the bug survives.
- **Mock** — a stand-in for a real dependency (e.g. the payment gateway or the LLM) that you
  control in a test, so you can simulate success, failure, or timeout.
- **Seam** — a place designed so you can swap in a mock (here: `PaymentService`,
  `NotificationService`, `LlmClient`).
- **Mutation testing (PIT)** — the tool deliberately introduces small bugs ("mutants"); if your
  tests still pass, they were too weak to catch that bug ("survived mutant").
- **Mutation score** — the percentage of mutants your tests killed.
- **Regression test** — a test that guards against a bug coming back after a change.
- **Metamorphic relation** — a property that must hold between related inputs/outputs when you
  have no exact oracle (e.g. "an irrelevant detail must not change the recommendation").
- **Prompt injection** — malicious text inside data trying to hijack an LLM's instructions; the
  prompt builder must treat free text as data, not instructions.
- **CI (Continuous Integration)** — GitHub Actions runs your build + tests + tooling on every
  push and reports the result.
