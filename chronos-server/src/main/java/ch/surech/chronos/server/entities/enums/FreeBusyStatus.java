package ch.surech.chronos.server.entities.enums;

public enum FreeBusyStatus {
    /**
     * free
     */
    FREE,
    /**
     * tentative
     */
    TENTATIVE,
    /**
     * busy
     */
    BUSY,
    /**
     * oof
     */
    OOF,
    /**
     * working Elsewhere
     */
    WORKING_ELSEWHERE,
    /**
     * unknown
     */
    UNKNOWN,
    /**
     * For FreeBusyStatus values that were not expected from the service
     */
    UNEXPECTED_VALUE
}
