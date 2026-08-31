package com.walkmates.web;

import com.walkmates.model.Listing;
import com.walkmates.model.Seeker;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.ai.MatchExplanationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST surface for the AI "explain this match" feature — the <strong>Lab 3 interface-testing
 * target</strong>. Students drive this with Spring {@code MockMvc} / {@code @WebMvcTest},
 * mocking the {@link MatchExplanationService} (or its {@link com.walkmates.service.ai.LlmClient})
 * to test the HTTP contract independently of a live model.
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final SeekerRepository seekers;
    private final ListingRepository listings;
    private final MatchExplanationService matchExplanation;

    public MatchController(SeekerRepository seekers,
                           ListingRepository listings,
                           MatchExplanationService matchExplanation) {
        this.seekers = seekers;
        this.listings = listings;
        this.matchExplanation = matchExplanation;
    }

    /** Explains why a specific listing suits a seeker. */
    @GetMapping("/{seekerId}/explain")
    public ResponseEntity<ExplanationResponse> explain(@PathVariable String seekerId,
                                                       @RequestParam String listingId) {
        Seeker seeker = seekers.findById(seekerId).orElse(null);
        Listing listing = listings.findById(listingId).orElse(null);
        if (seeker == null || listing == null) {
            return ResponseEntity.notFound().build();
        }
        String explanation = matchExplanation.explainMatch(seeker, listing);
        return ResponseEntity.ok(new ExplanationResponse(seekerId, listingId, explanation));
    }

    /** Recommends the best available listing for a seeker and explains it. */
    @GetMapping("/{seekerId}/best")
    public ResponseEntity<ExplanationResponse> best(@PathVariable String seekerId) {
        Seeker seeker = seekers.findById(seekerId).orElse(null);
        if (seeker == null) {
            return ResponseEntity.notFound().build();
        }
        List<Listing> available = listings.findAll().stream().filter(Listing::isAvailable).toList();
        Listing best = matchExplanation.recommendBestMatch(seeker, available);
        if (best == null) {
            return ResponseEntity.noContent().build();
        }
        String explanation = matchExplanation.explainMatch(seeker, best);
        return ResponseEntity.ok(new ExplanationResponse(seekerId, best.getId(), explanation));
    }

    /** JSON response for an explanation. */
    public record ExplanationResponse(String seekerId, String listingId, String explanation) {
    }
}
