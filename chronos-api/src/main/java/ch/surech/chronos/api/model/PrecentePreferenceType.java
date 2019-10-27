package ch.surech.chronos.api.model;

public enum PrecentePreferenceType {
    /** Not at work, so not meeting possible */
    NoWork,

    /** Basically available, but better no meeting */
    RatherNot,

    /** Available for meeting. This is the Default if nothing is set */
    Available,

    /** Preferred time for meetings */
    Preferred
}
