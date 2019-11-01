package ch.surech.chronos.server.model;

public enum Availability {
    /** No Meeting at this time */
    Available,

    /** A prefered time for a meeting */
    Prefered,

    /** User like to keep this time free from meetings */
    RatherNot,

    /** Booked by another meeting */
    Booked,

    /** Not available for meetings */
    NotAvailable
}
