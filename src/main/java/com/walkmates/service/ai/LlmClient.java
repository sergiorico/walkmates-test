package com.walkmates.service.ai;

/**
 * Thin abstraction over a Large Language Model text-completion call — the <strong>mockable LLM
 * seam</strong> for Lab 3 (and reused from Lab 2's mocking work).
 *
 * <p>Keeping the real provider (Anthropic/OpenAI/…) behind this one-method interface is what
 * makes the AI feature testable without a live API: students inject a Mockito mock that returns
 * a canned answer, throws, or times out.</p>
 */
public interface LlmClient {

    /**
     * Sends a prompt to the model and returns its text completion.
     *
     * @param prompt the fully-built prompt
     * @return the model's text response
     * @throws LlmException        if the model call fails
     * @throws LlmTimeoutException if the call does not return in time
     */
    String complete(String prompt) throws LlmException;

    /** Thrown when the model call fails (bad request, provider error, …). */
    class LlmException extends Exception {
        public LlmException(String message) {
            super(message);
        }
    }

    /** Thrown when the model call exceeds its timeout. */
    class LlmTimeoutException extends RuntimeException {
        public LlmTimeoutException(String message) {
            super(message);
        }
    }
}
