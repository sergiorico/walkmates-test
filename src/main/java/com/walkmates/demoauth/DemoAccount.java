package com.walkmates.demoauth;

/** A fixed, openly documented account used only to exercise role-specific demo flows. */
public record DemoAccount(String username, String password, String displayName,
                          DemoRole role, String providerName) {
}
