package concurrency.locking;

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
