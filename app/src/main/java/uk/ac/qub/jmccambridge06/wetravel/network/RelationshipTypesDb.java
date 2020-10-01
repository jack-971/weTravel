package uk.ac.qub.jmccambridge06.wetravel.network;

/**
 * Holds the various relationship types as held in the database
 */
public class RelationshipTypesDb {


        public static final String FRIENDS = "friends";
        public static final String PENDING_FIRST = "sent_by_first";
        public static final String PENDING_SECOND = "sent_by_second";

        public static int getFRIENDS() {
                return 3;
        }

        public static int getPendingFirst() {
                return 1;
        }

        public static int getPendingSecond() {
                return 2;
        }
}
