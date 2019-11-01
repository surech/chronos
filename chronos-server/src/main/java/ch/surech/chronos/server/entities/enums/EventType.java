package ch.surech.chronos.server.entities.enums;

public enum EventType {

    /**
     * single Instance
     */
    SINGLE_INSTANCE,
    /**
     * occurrence
     */
    OCCURRENCE,
    /**
     * exception
     */
    EXCEPTION,
    /**
     * series Master
     */
    SERIES_MASTER,
    /**
     * For EventType values that were not expected from the service
     */
    UNEXPECTED_VALUE
}
