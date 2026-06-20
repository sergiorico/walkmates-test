# Lab 1 — Fundamentals & Specification-based Testing

> Modules **M1 + M2**. *In the WalkMates repo.* Due **wk 38**. Done in pairs.

## Learning objectives (DV033G)

Explain QA fundamentals (quality attributes, V&V) and testing fundamentals (faults, failures,
levels); name/describe specification-based techniques; design meaningful test artifacts.

---

## Part A — Fundamentals (M1)

### Activity 1.1 — Quality-attribute analysis (ISO/IEC 25010)
1. Pick **three** WalkMates features from [`docs/REQUIREMENTS.md`](../docs/REQUIREMENTS.md) — for
   example wallet top-ups (FR-1.3), the booking decision rule (FR-4.4), and the AI match
   explanation (FR-5).
2. For each, choose the **two** ISO/IEC 25010 quality characteristics most at stake (e.g.
   *Functional correctness*, *Security*, *Reliability*) and justify the choice in one sentence.
3. For one of them, write a **testable** quality requirement (something you could check).

### Activity 1.2 — Bug analysis (error → fault → failure)
You are given this **failure report**:

> *"A NEW seeker who already has one active booking was able to create a second one. The system
> let it through; the seeker now has two active bookings, which should be impossible for their
> tier."*

1. Trace the chain: what was the likely **human error**, the **fault** in the code, and the
   **failure** the user saw?
2. Find the responsible code (hint: the FR-4.4 rule-2 check). Describe the fault precisely.
3. Which **test level** (unit / integration / system) should have caught this, and what kind of
   test (which technique)? You do **not** need to fix it in this lab — Lab 2 is where you hunt it.

> There are a couple more seeded faults in the system. You are not asked to find them all here;
> Activity 1.2 is about reasoning, not bug-hunting.

### Activity 1.3 — Your first test (day-one win)
Complete the [`FIRST_TEST_TUTORIAL.md`](../FIRST_TEST_TUTORIAL.md) if you haven't. Confirm
`mvn test -Dtest=BeginnerFirstTest` is green.

---

## Part B — Specification-based design (M2)

**Target class:** [`Seeker`](../src/main/java/com/walkmates/model/Seeker.java).
**Implement in:** [`src/test/java/com/walkmates/lab1/SeekerSpecBasedTest.java`](../src/test/java/com/walkmates/lab1/SeekerSpecBasedTest.java).

### Activity 2.1 — Equivalence Partitioning
From FR-1.1 and FR-1.3, define valid/invalid classes for **email**, **display name**, **phone
number**, and **wallet top-up amount**. Pick one representative per class. Document as a table
(*Input → Expected outcome*), then implement.

### Activity 2.2 — Boundary Value Analysis
From FR-1.3, test the wallet boundaries: the **10.00 minimum** (just below / at / just above),
the **5 000.00 single-transaction maximum**, and the **20 000.00 maximum balance**. Document the
boundary table, then implement.

### Activity 2.3 — Decision table (trust tier → limits)
From FR-1.2, build a decision table mapping each **trust tier** (NEW/VERIFIED/TRUSTED/PRO_SITTER)
to its **max concurrent bookings** and **platform fee**. Implement a compact covering set
(`getMaxConcurrentBookings()` and `getTrustTier().getPlatformFee()`).

---

## CI evidence (automatic)

Build passes and your new tests run green. Bar for Lab 1: a populated `SeekerSpecBasedTest` with
EP, BVA and decision-table tests present and passing. (Coverage is reported but not gated here.)

## Submission

Repo link/commit + completed [`reflection-template.md`](reflection-template.md). No long report.
