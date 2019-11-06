package ch.surech.chronos.server.model;

public enum Availability {
    /** No Meeting at this time */
    Available(3, true),

    /** A prefered time for a meeting */
    Prefered(4, true),

    /** User like to keep this time free from meetings */
    RatherNot(2, true),

    /** Booked by another meeting */
    Booked(1, false),

    /** Not available for meetings */
    NotAvailable(0, false);

    private final int order;

    private final boolean bookable;

    Availability(int order, boolean bookable) {
        this.order = order;
        this.bookable = bookable;
    }

    public int getOrder() {
        return order;
    }

    public boolean isBookable() {
        return bookable;
    }
}
