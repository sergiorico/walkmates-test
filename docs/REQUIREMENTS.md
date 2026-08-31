# WalkMates — Requirements Specification

> **Read this like a contract.** Every rule below is written to be **testable**: numeric
> boundaries are explicit and unambiguous so you can derive equivalence partitions, boundary
> values, and decision tables without guessing. When a lab says "from the requirements", it
> means *this file*. Cite the rule ID (e.g. `FR-1.3`) in your test design.

## 0. One-sentence domain

WalkMates matches **Seekers** (people who want time with animals — dog walking, pet/house
sitting, day visits, shelter volunteering) with **Providers** (pet owners and shelters) who
publish **Listings** (care opportunities). A Seeker books a Listing; that booking is a
**Booking** with a lifecycle and a price.

---

## 1. Seeker (the person who wants an animal experience)

### FR-1.1 Registration
- A Seeker registers with **email**, **display name**, and **phone number**.
- **Email** MUST be unique and match a valid email format (one `@`, a non-empty local part, and
  a domain containing at least one `.`). Max length **254** characters.
- **Display name** MUST be **2–40 characters** inclusive, letters/spaces/hyphens/apostrophes
  only.
- **Phone number** MUST follow Swedish format `07XXXXXXXX` (10 digits, starts `07`) or
  international `+467XXXXXXXX`.
- Terms-of-service acceptance is handled outside this teaching model and is not part of the
  Labs 1–3 test surface.

> Email uniqueness is enforced by `SeekerService`; the `Seeker` constructor validates the
> field's format and length.

### FR-1.2 Trust tiers
- Every Seeker has exactly one **trust tier**: `NEW`, `VERIFIED`, `TRUSTED`, or `PRO_SITTER`.
- A new registration starts at `NEW`.
- The trust tier sets two numeric limits — the **maximum number of concurrent active bookings**
  and the **platform service fee** applied to each booking:

| Trust tier | Max concurrent active bookings | Platform fee |
|---|---|---|
| `NEW` | 1 | 15% |
| `VERIFIED` | 3 | 12% |
| `TRUSTED` | 5 | 8% |
| `PRO_SITTER` | 10 | 5% |

> "Active" = a Booking in state `REQUESTED`, `CONFIRMED`, or `IN_PROGRESS` (see FR-4.2).

### FR-1.3 Wallet (account balance)
- A Seeker has a wallet balance in SEK, starting at **0.00**.
- **Top-up** rules:
  - Minimum top-up: **10.00 SEK** (a top-up of less than 10.00 is rejected).
  - Maximum single top-up: **5 000.00 SEK**.
  - Maximum resulting balance: **20 000.00 SEK** (a top-up that would exceed this is rejected).
- The balance MUST never go negative. A booking that costs more than the current balance is
  rejected (see FR-4.3).
- Amounts are handled to **2 decimal places**; round half-up.

### FR-1.4 Swedish identity (optional verification)
- A Seeker MAY verify identity with a Swedish **personnummer** in `YYMMDD-NNNN` format.
- The personnummer MUST pass the **Luhn checksum** on its 10 digits.
- Successful verification promotes a `NEW` Seeker to `VERIFIED` (higher tiers are granted
  manually by an operator and are out of scope for these labs).

---

## 2. Provider (pet owner or shelter publishing opportunities)

### FR-2.1 Properties
- A Provider has a unique id, a **name**, a location (**latitude** −90..90, **longitude**
  −180..180), and a **capacity** = the maximum number of concurrent active Bookings across all
  its Listings.
- Default capacity is **3**; capacity MUST be **1–20** inclusive.

### FR-2.2 Capacity rule
- A new Booking against any of a Provider's Listings is rejected if the Provider already has
  **capacity** active Bookings.

---

## 3. Listing (a single care opportunity)

### FR-3.1 Types and base rate
- A Listing has exactly one **type**, each with a fixed **base rate per hour** (SEK):

| Listing type | Base rate (SEK/hour) |
|---|---|
| `DOG_WALK` | 80 |
| `DAY_VISIT` | 100 |
| `PET_SITTING` | 120 |
| `HOUSE_SITTING` | 150 |
| `SHELTER_VOLUNTEER` | 0 |

- `SHELTER_VOLUNTEER` listings are **free** (rate 0); they still create Bookings and still count
  against trust-tier and Provider capacity limits.

### FR-3.2 State machine
- A Listing is in exactly one **status**: `AVAILABLE`, `BOOKED`, `IN_PROGRESS`,
  `COMPLETED`, or `CANCELLED`.
- Legal transitions only:
  - `AVAILABLE → BOOKED` (a Seeker books it)
  - `BOOKED → IN_PROGRESS` (the experience starts)
  - `BOOKED → CANCELLED` (cancelled before it starts)
  - `IN_PROGRESS → COMPLETED` (the experience ends normally)
  - `COMPLETED → AVAILABLE` and `CANCELLED → AVAILABLE` (Provider re-opens the slot)
- **Any other transition MUST be rejected** (e.g. `CANCELLED → IN_PROGRESS`, `AVAILABLE →
  COMPLETED`, `COMPLETED → IN_PROGRESS`).

---

## 4. Booking (a Seeker's reservation of a Listing)

### FR-4.1 Duration
- A Booking has a planned **duration in minutes**.
- Minimum duration: **30 minutes**. Maximum duration: **1 440 minutes** (24 hours).
- A duration outside `[30, 1440]` is rejected.

### FR-4.2 Lifecycle
- Booking statuses: `REQUESTED`, `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
- `REQUESTED`, `CONFIRMED`, and `IN_PROGRESS` are **active** (count against limits).
- Legal transitions: `REQUESTED → CONFIRMED → IN_PROGRESS → COMPLETED`, and `REQUESTED →
  CANCELLED` or `CONFIRMED → CANCELLED`. Any other transition is rejected.

### FR-4.3 Pricing
The **price of a Booking** is computed at confirmation as:

```
hours          = durationMinutes / 60
baseCost       = hours × listing.baseRatePerHour
overnightExtra = (durationMinutes > 480) ? baseCost × 0.20 : 0      // > 8 hours
subtotal       = baseCost + overnightExtra
fee            = subtotal × seeker.trustTier.platformFee
total          = round2(subtotal + fee)
```

- The **overnight surcharge** of **20%** applies only when duration is **strictly greater than
  480 minutes** (8 hours). Exactly 480 minutes does **not** incur it.
- `SHELTER_VOLUNTEER` Bookings always cost **0.00** (base rate 0 → subtotal 0 → fee 0).
- A Booking is **rejected** if `total` exceeds the Seeker's wallet balance (FR-1.3).

### FR-4.4 Booking creation — combined rule (decision-table source)
A request to create a Booking is **accepted** only if **all** hold; otherwise it is **rejected**
with the first failing reason:

1. The Listing is `AVAILABLE`.
2. The Seeker's active Bookings `<` the trust-tier max (FR-1.2).
3. The Provider's active Bookings `<` the Provider capacity (FR-2.2).
4. Duration is within `[30, 1440]` (FR-4.1).
5. `total` price `≤` the Seeker's wallet balance (FR-4.3).

---

## 5. AI feature — "Explain this match" (Lab 3)

### FR-5.1 Match explanation
- Given a Seeker and a candidate Listing, `MatchExplanationService` returns a short,
  human-readable explanation of why the Listing suits the Seeker.
- The service builds a **prompt** from the Seeker and Listing (a **pure function** —
  deterministic, no network) and passes it to an **LLM client** behind an interface.

### FR-5.2 Robustness and fallback
- If the LLM client **fails or times out**, the service MUST return a **deterministic fallback**
  explanation built from the same data (no exception leaks to the caller).
- The prompt builder MUST place the provider-supplied Listing description inside explicit
  untrusted-data delimiters while keeping the standing instruction outside and unchanged. This
  is a testable prompt-construction mitigation; it does not guarantee that every live model will
  resist every prompt-injection attempt.

### FR-5.3 Metamorphic expectations (test oracles for Lab 3)
- **MR-1 (irrelevant detail):** adding an irrelevant sentence to a Listing description MUST NOT
  change which Listing is recommended as the best match.
- **MR-2 (order invariance):** the chosen best match MUST NOT depend on the order in which
  candidate Listings are supplied.

---

## 6. Non-functional notes (lightweight, for context)

- **Platform:** Java 21, Maven, Spring Boot. H2 for any persistence; in-memory repositories by
  default.
- **Currency:** SEK, 2 decimal places, round half-up.
- **Testing:** JUnit 5, Mockito, AssertJ, JaCoCo (coverage), PIT (mutation), GitHub Actions (CI).

---

**Document version:** 1.0 (HT26) · **Classification:** Educational use.
