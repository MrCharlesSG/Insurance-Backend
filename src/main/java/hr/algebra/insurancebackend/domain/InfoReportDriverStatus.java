package hr.algebra.insurancebackend.domain;

public enum InfoReportDriverStatus {
    WAITING(0), ACCEPTED(1), REJECTED(2);

    private final int value;

    InfoReportDriverStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static InfoReportDriverStatus fromValue(int value) {
        switch (value) {
            case 0: return WAITING;
            case 1: return ACCEPTED;
            case 2: return REJECTED;
            default: throw new IllegalArgumentException("Unknown status value: " + value);
        }
    }
}

