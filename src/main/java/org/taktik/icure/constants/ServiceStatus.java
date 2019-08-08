package org.taktik.icure.constants;

public enum ServiceStatus {

    NONE(0),
    INACTIVE(1),
    IRRELEVANT(2),
    ABSENT(4);

    private final int value;
    ServiceStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static int getValue(Integer value, int valueIfNull) {
        if (value == null)
            return valueIfNull;
        return value;
    }

    /**
     * Check if last bit is 0
     */
    public static boolean isActive(Integer status) {
        return isActive(status, NONE.value);
    }

    /**
     * Check if last bit is 0
     * @param valueIfNull value used if status is null (default 0)
     */
    public static boolean isActive(Integer status, int valueIfNull) {
        return (getValue(status, valueIfNull) & INACTIVE.value) == 0;
    }

    /**
     * Check if last bit is 1
     */
    public static boolean isInactive(Integer status) {
        return isInactive(status, NONE.value);
    }
    /**
     * Check if last bit is 1
     * @param valueIfNull value used if status is null (default 0)
     */
    public static boolean isInactive(Integer status, int valueIfNull) {
        return (getValue(status, valueIfNull) & INACTIVE.value) != 0;
    }

    /**
     * Check if last-but-one bit is 0
     */
    public static boolean isRelevant(Integer status) {
        return isRelevant(status, NONE.value);
    }
    /**
     * Check if last-but-one bit is 0
     * @param valueIfNull value used if status is null (default 0)
     */
    public static boolean isRelevant(Integer status, int valueIfNull) {
        return (getValue(status, valueIfNull) & IRRELEVANT.value) == 0;
    }

    /**
     * Check if last-but-one bit is 1
     */
    public static boolean isIrrelevant(Integer status) {
        return isIrrelevant(status, NONE.value);
    }
    /**
     * Check if last-but-one bit is 1
     * @param valueIfNull value used if status is null (default 0)
     */
    public static boolean isIrrelevant(Integer status, int valueIfNull) {
        return (getValue(status, valueIfNull) & IRRELEVANT.value) != 0;
    }

    /**
     * Check if last-but-two bit is 0
     */
    public static boolean isPresent(Integer status) {
        return isPresent(status, NONE.value);
    }
    /**
     * Check if last-but-two bit is 0
     * @param valueIfNull value used if status is null (default 0)
     */
    public static boolean isPresent(Integer status, int valueIfNull) {
        return (getValue(status, valueIfNull) & ABSENT.value) == 0;
    }

    /**
     * Check if last-but-two bit is 1
     */
    public static boolean isAbsent(Integer status) {
        return isAbsent(status, NONE.value);
    }
    /**
     * Check if last-but-two bit is 1
     * @param valueIfNull value used if status is null (default 0)
     */
    public static boolean isAbsent(Integer status, int valueIfNull) {
        return (getValue(status, valueIfNull) & ABSENT.value) != 0;
    }

}
