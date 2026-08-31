(() => {
    "use strict";

    const app = document.querySelector("#demo-app");
    const message = document.querySelector("#app-message");
    const wallet = document.querySelector("#wallet-balance");

    if (!app || !message) {
        return;
    }

    const seekerId = app.dataset.seekerId;
    let balance = Number(app.dataset.balance);
    let messageTimer;

    const showMessage = (text, isError = false) => {
        window.clearTimeout(messageTimer);
        message.textContent = text;
        message.classList.toggle("app-message--error", isError);
        message.hidden = false;
        messageTimer = window.setTimeout(() => {
            message.hidden = true;
        }, 6000);
    };

    const errorMessage = async (response, fallback) => {
        try {
            const body = await response.json();
            return body.error || fallback;
        } catch (_ignored) {
            return fallback;
        }
    };

    const withBusyButton = async (button, busyLabel, action) => {
        const originalLabel = button.textContent;
        button.disabled = true;
        button.textContent = busyLabel;
        try {
            await action();
        } finally {
            if (button.isConnected && button.dataset.finished !== "true") {
                button.disabled = false;
                button.textContent = originalLabel;
            }
        }
    };

    document.querySelectorAll(".booking-button").forEach((button) => {
        button.addEventListener("click", () => withBusyButton(button, "Booking…", async () => {
            const card = button.closest(".listing-card");
            const duration = Number(card.querySelector(".duration-select").value);
            const response = await fetch("/api/bookings", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    seekerId,
                    listingId: card.dataset.listingId,
                    durationMinutes: duration
                })
            });

            if (!response.ok) {
                throw new Error(await errorMessage(response, "The booking could not be completed."));
            }

            const booking = await response.json();
            const status = card.querySelector(".status-pill");
            const durationSelect = card.querySelector(".duration-select");
            status.textContent = "BOOKED";
            status.classList.remove("status-pill--available");
            status.classList.add("status-pill--booked");
            durationSelect.disabled = true;
            button.dataset.finished = "true";
            button.textContent = "Already booked";

            if (wallet && Number.isFinite(balance)) {
                balance = Math.max(0, balance - Number(booking.price));
                wallet.textContent = balance.toFixed(2);
            }

            window.location.assign("/?booking=created#my-bookings");
        }).catch((error) => showMessage(error.message, true)));
    });

    document.querySelectorAll(".explain-button").forEach((button) => {
        button.addEventListener("click", () => withBusyButton(button, "Finding the fit…", async () => {
            const card = button.closest(".listing-card");
            const params = new URLSearchParams({listingId: card.dataset.listingId});
            const response = await fetch(`/api/match/${encodeURIComponent(seekerId)}/explain?${params}`);

            if (!response.ok) {
                throw new Error("No match explanation is available right now.");
            }

            const result = await response.json();
            const panel = card.querySelector(".explanation");
            panel.querySelector("p").textContent = result.explanation;
            panel.hidden = false;
        }).catch((error) => showMessage(error.message, true)));
    });
})();
