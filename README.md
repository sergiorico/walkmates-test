# WalkMates 🐾

![Made for Mid Sweden University — DV033G](https://img.shields.io/badge/Mid%20Sweden%20University-DV033G-blue.svg)

A teaching system for **Principles & Practices in Software Testing (DV033G)**, HT26. WalkMates
matches **Seekers** (people who want time with animals — dog walking, pet/house sitting, day
visits, shelter volunteering) with **Providers** (owners and shelters) who publish **Listings**.

This codebase exists to be a good thing to **test**. It deliberately exposes clean,
boundary-friendly, mockable seams for Labs 1–3, and an AI "explain this match" feature for Lab 3.

## Release status

The **HT26 lab specification was frozen on 2026-08-26**. After that date, lab tasks,
deliverables, and assessment bars change only for minor corrections or clarifications. The
application code and UI may continue to evolve as long as the frozen behavior under test is
preserved. See [`LAB_MATERIAL_VERSION.md`](LAB_MATERIAL_VERSION.md) for the policy and release
notes.

## Quick start

```bash
java -version          # must show Java 21
mvn -version           # Maven 3.8+; its "Java version" must also be 21

mvn clean test         # build + run the test suite (ships green)
mvn spring-boot:run    # run the app, then open http://localhost:8080
```

The home page includes a seeded demo profile. You can ask why each opportunity is a match and
make in-memory bookings directly from the listing cards, then follow their status in **My
bookings**. Open
[`http://localhost:8080/manage`](http://localhost:8080/manage) to add, edit, or remove the demo
providers' pets and service listings around Östersund. A service can optionally be assigned to a
specific pet; linked pets are protected from accidental deletion. Providers can also start and
complete bookings and reopen completed services. Catalog changes persist in a local H2 database
and appear on the home page immediately; bookings and wallet changes reset on restart.

### Demo accounts

The role switcher is deliberately **not production authentication**. Its fixed credentials are
shown on the home page so students can quickly exercise separate application perspectives:

| Role | Username | Password | Perspective |
|---|---|---|---|
| Seeker | `seeker` | `seeker123` | Browse, book, and follow booking history |
| Pet holder | `holder` | `holder123` | Manage Rex, services, and provider bookings |
| Shelter | `shelter` | `shelter123` | Manage the Östersund shelter catalog and bookings |
| Administrator | `admin` | `admin123` | Switch between and manage both providers |

These accounts are stored in source and remembered only in the browser session. They provide
realistic role separation for testing, not security controls.

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
[`reflection`](lab-instructions/reflection-template.md) per lab, saved as
`reflections/labN-reflection.md`, which is what the instructor grades. Using AI is encouraged;
the reflection is where you show your judgment over what it produced.

## Tech

Java 21 · Maven · Spring Boot · H2 · JUnit 5 · Mockito · AssertJ · JaCoCo · PIT · GitHub Actions.

The local catalog database is written under `data/` when the application runs. That directory is
ignored by Git and can be removed whenever you want to restore a completely fresh catalog.

## Project structure

```
src/main/java/com/walkmates/
  model/        Seeker, Listing, Provider, Booking + enums + state machines
  service/      PricingCalculator, BookingService, SeekerService, seam interfaces
  service/ai/   LlmClient (seam) + MatchExplanationService (the AI feature)
  catalog/      persistent Pet/service catalog bridge, isolated from the frozen lab domain
  repository/   repository interfaces + in-memory implementations
  web/          REST controllers + Thymeleaf home/management UI + sample DataLoader
  infra/        default stub implementations of the seams (so the app runs with no keys)
src/test/java/com/walkmates/
  lab1/ lab2/ lab3/   starter tests + skeletons you extend
docs/REQUIREMENTS.md   the spec your tests are derived from
reflections/           your four completed lab reflections
```

For educational use.
