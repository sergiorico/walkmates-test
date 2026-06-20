package com.walkmates.infra;

import com.walkmates.service.ai.LlmClient;
import org.springframework.stereotype.Component;

/**
 * Default {@link LlmClient} so the app runs with no API key. It returns a canned, deterministic
 * completion — enough to demo the feature end-to-end.
 *
 * <p>In Lab 3 you do <em>not</em> test against this stub; you inject a Mockito mock of
 * {@link LlmClient} to drive success / failure / timeout paths. A real provider-backed client
 * (e.g. Anthropic) would replace this class without touching {@code MatchExplanationService}.</p>
 */
@Component
public class StubLlmClient implements LlmClient {

    @Override
    public String complete(String prompt) {
        return "Based on your profile, this looks like a friendly, well-matched animal-care "
                + "opportunity for you.";
    }
}
