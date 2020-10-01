package uk.ac.qub.jmccambridge06.wetravel.utilities;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents the different types of profile that may be loaded to the screen.
 */
public abstract class ProfileTypes {

    @IntDef({PROFILE_ADMIN, PROFILE_FRIEND, PROFILE_USER, PROFILE_REQUEST_SENT, PROFILE_REQUEST_RECEIVED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ProfileType {}
        // Declare the constants
        public static final int PROFILE_ADMIN = 0;
        public static final int PROFILE_FRIEND = 1;
        public static final int PROFILE_USER = 2;
        public static final int PROFILE_REQUEST_SENT = 3;
        public static final int PROFILE_REQUEST_RECEIVED = 4;
        public static final int PROFILE_UNDEFINED = 5;

    @ProfileType
    private int type;


}
