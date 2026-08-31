# Lab Material Version and Freeze Policy

**Course release:** HT26 v1.0  
**Frozen:** 2026-08-26

The student-facing specification for Labs 1–4 is frozen for the HT26 course run. This includes
the activities, required deliverables, assessment bars, requirements used as test oracles, and
starter-test TODOs.

After the freeze, the following changes are allowed without creating a new lab version:

- typo, broken-link, accessibility, and formatting fixes;
- clarifications that do not add work or change an expected result;
- CI and dependency maintenance that preserves the documented student workflow; and
- application or UI improvements that preserve the requirements and the intended lab
  observations, including the deliberately seeded faults.

Any change to lab scope, required evidence, grading criteria, or a behavior students are asked
to discover belongs in a later course iteration and must use a new version/tag.

## Release baseline

- The published instructions, requirements, starter tests, reflection template, and assessment
  bars together define HT26 v1.0.
- The supported toolchain is Java 21 and Maven 3.8 or newer. GitHub Actions runs the build, test
  suite, JaCoCo coverage, and PIT mutation analysis.
- The supplied starter suite is green. Deliberately seeded faults and surviving mutants are part
  of the learning material unless a lab explicitly asks students to correct them.
- The demo interface and its persistent pet/service catalog are application features outside the
  frozen lab domain. Its fixed testing roles, runtime-only booking views, and controls use the
  existing behavior without changing the work required from students. The openly documented demo
  credentials are role-switching fixtures, not a production security feature.

## Published corrections

None for HT26 v1.0. Add future corrections here with the date, affected files, and a short
statement confirming whether student work or expected results change.
