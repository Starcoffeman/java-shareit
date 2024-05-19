package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED,
    UNDEFINED;

    public static boolean isValid(String state) {
        for (BookingStatus bookingState : BookingStatus.values()) {
            if (bookingState.name().equalsIgnoreCase(state)) {
                return true;
            }
        }
        return false;
    }
}
