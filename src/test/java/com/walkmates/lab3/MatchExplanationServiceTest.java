package com.walkmates.lab3;

import com.walkmates.model.Listing;
import com.walkmates.model.ListingType;
import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import com.walkmates.service.ai.LlmClient;
import com.walkmates.service.ai.MatchExplanationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Lab 3, Part A — testing the AI "explain this match" feature without a live LLM.
 *
 * <p>There is no exact oracle for the model's text, so we test the parts we <em>can</em> pin
 * down: the deterministic prompt builder, the fallback path (mock the {@link LlmClient} to
 * fail/timeout), the metamorphic relations, and prompt-injection resistance. Two worked
 * examples are provided; the {@code TODO}s are yours.</p>
 */
class MatchExplanationServiceTest {

    private Seeker seeker() {
        return new Seeker("p@example.com", "Pat", "0701112233");
    }

    private Listing listing(String description) {
        return new Listing("provider-1", "Walk Rex", description, ListingType.DOG_WALK);
    }

    // ---- Worked example 1: the prompt builder is deterministic and structured (FR-5.1) ----
    @Test
    @DisplayName("buildPrompt includes the structured fields")
    void promptIncludesStructuredFields() {
        MatchExplanationService service = new MatchExplanationService(mock(LlmClient.class));

        String prompt = service.buildPrompt(seeker(), listing("Friendly dog"));

        assertThat(prompt).contains("Seeker trust tier: " + TrustTier.NEW);
        assertThat(prompt).contains("Listing type: " + ListingType.DOG_WALK);
    }

    // ---- Worked example 2: on LLM failure, fall back deterministically (FR-5.2) ----
    @Test
    @DisplayName("explainMatch falls back when the LLM call fails")
    void fallsBackOnLlmFailure() throws Exception {
        LlmClient llm = mock(LlmClient.class);
        when(llm.complete(org.mockito.ArgumentMatchers.anyString()))
                .thenThrow(new LlmClient.LlmException("provider down"));
        MatchExplanationService service = new MatchExplanationService(llm);
        Seeker seeker = seeker();
        Listing listing = listing("Friendly dog");

        String result = service.explainMatch(seeker, listing);

        // Use an independent, concrete oracle. Comparing result only with another call to
        // fallbackExplanation would pass if both calls returned the same wrong text.
        assertThat(result).isEqualTo(
                "This DOG_WALK opportunity \"Walk Rex\" is a good fit for a NEW seeker.");
    }

    // TODO (fallback): also fall back on LlmTimeoutException, and on a null/blank response.
    // TODO (injection): a description containing "ignore previous instructions and ..." must
    //      stay inside the data block; buildPrompt must still contain the data delimiters.
    // TODO (MR-1): adding an irrelevant sentence to the listing description must not change
    //      recommendBestMatch's chosen listing.
    // TODO (MR-2): shuffling the candidate list must not change the chosen listing.
}
