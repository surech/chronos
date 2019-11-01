package ch.surech.chronos.server.entities.enums;

public enum LocationType {
    /**
     * default
     */
    DEFAULT,
    /**
     * conference Room
     */
    CONFERENCE_ROOM,
    /**
     * home Address
     */
    HOME_ADDRESS,
    /**
     * business Address
     */
    BUSINESS_ADDRESS,
    /**
     * geo Coordinates
     */
    GEO_COORDINATES,
    /**
     * street Address
     */
    STREET_ADDRESS,
    /**
     * hotel
     */
    HOTEL,
    /**
     * restaurant
     */
    RESTAURANT,
    /**
     * local Business
     */
    LOCAL_BUSINESS,
    /**
     * postal Address
     */
    POSTAL_ADDRESS,
    /**
     * For LocationType values that were not expected from the service
     */
    UNEXPECTED_VALUE
}
