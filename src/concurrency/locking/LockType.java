package concurrency.locking;
/**
 * Defines the type of lock and provides easy printing
 * for integration into logging functions. 
 */
public enum LockType {
    WRITE {
        @Override
        public String toString() {
            return "WRITE";
        }
    },
    READ {
        @Override
        public String toString() {
            return "READ";
        }
    }

}
