# WalkMates 🐾

[![Made for Mid Sweden University — DV033G](https://img.shields.io/badge/Mid%20Sweden%20University-DV033G-blue.svg)]()

A teaching system for **Principles & Practices in Software Testing (DV033G)**, HT26. WalkMates
matches **Seekers** (people who want time with animals — dog walking, pet/house sitting, day
visits, shelter volunteering) with **Providers** (owners and shelters) who publish **Listings**.

This codebase exists to be a good thing to **test**. It deliberately exposes clean,
boundary-friendly, mockable seams for Labs 1–3, and an AI "explain this match" feature for Lab 3.

## Quick start

```bash
java -version          # Java 21
mvn -version           # Maven 3.8+

mvn clean test         # build + run the test suite (ships green)
mvn spring-boot:run    # run the app, then open http://localhost:8080
```

New here? Start with **[FIRST_TEST_TUTORIAL.md](FIRST_TEST_TUTORIAL.md)** for a 10-minute
day-one win, then **[lab-instructions/00-GENERAL-INSTRUCTIONS.md](lab-instructions/00-GENERAL-INSTRUCTIONS.md)**.

## The labs

| Lab | Title | Focus |
|---|---|---|
| 1 | Fundamentals & Specification-based testing | EP / BVA / decision tables on `Seeker` |
| 2 | Structural testing & Test optimization | coverage + mutation (PIT) on `PricingCalculator` / `BookingService`; mocking seams |
| 3 | Research trends & Testing AI | testing `MatchExplanationService` (prompt-building, fallback, metamorphic, injection) |
| 4 | Ethics & Sustainability | paper-based (Canvas) |

## How you're assessed

**Machines grade mechanics, humans grade understanding.** When you push, GitHub Actions runs the
build, tests, JaCoCo (coverage) and PIT (mutation) and posts a summary. That CI result is the
evidence — no screenshots. You also submit one short
[`reflection`](lab-instructions/reflection-template.md) per lab, which is what the instructor
grades. Using AI is encouraged; the reflection is where you show your judgment over what it produced.

## Tech

Java 21 · Maven · Spring Boot · H2 · JUnit 5 · Mockito · AssertJ · JaCoCo · PIT · GitHub Actions.

## Project structure

```
src/main/java/com/walkmates/
  model/        Seeker, Listing, Provider, Booking + enums + state machines
  service/      PricingCalculator, BookingService, SeekerService, seam interfaces
  service/ai/   LlmClient (seam) + MatchExplanationService (the AI feature)
  repository/   repository interfaces + in-memory implementations
  web/          REST controllers + thin Thymeleaf UI + sample DataLoader
  infra/        default stub implementations of the seams (so the app runs with no keys)
src/test/java/com/walkmates/
  lab1/ lab2/ lab3/   starter tests + skeletons you extend
docs/REQUIREMENTS.md   the spec your tests are derived from
```

For educational use.

<!-- CI PR smoke-test: confirms GitHub Actions posts the coverage/mutation comment on PRs. -->
