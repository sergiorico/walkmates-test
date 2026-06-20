package com.walkmates.web;

import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.repository.ListingRepository;
import com.walkmates.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST surface for listings and bookings (used by the demo UI and available for testing). */
@RestController
public class BookingController {

    private final ListingRepository listings;
    private final BookingService bookingService;

    public BookingController(ListingRepository listings, BookingService bookingService) {
        this.listings = listings;
        this.bookingService = bookingService;
    }

    @GetMapping("/api/listings")
    public List<ListingView> listings() {
        return listings.findAll().stream()
                .map(l -> new ListingView(l.getId(), l.getTitle(), l.getType().name(),
                        l.getBaseRatePerHour(), l.getStatus().name()))
                .toList();
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<BookingView> create(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(
                request.seekerId(), request.listingId(), request.durationMinutes());
        BookingView view = new BookingView(booking.getId(), booking.getStatus().name(), booking.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @ExceptionHandler(BookingService.BookingRejectedException.class)
    public ResponseEntity<ErrorView> onRejected(BookingService.BookingRejectedException e) {
        return ResponseEntity.badRequest().body(new ErrorView(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorView> onInvalid(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorView(e.getMessage()));
    }

    public record ListingView(String id, String title, String type, double rate, String status) {
    }

    public record CreateBookingRequest(String seekerId, String listingId, int durationMinutes) {
    }

    public record BookingView(String id, String status, double price) {
    }

    public record ErrorView(String error) {
    }
}
