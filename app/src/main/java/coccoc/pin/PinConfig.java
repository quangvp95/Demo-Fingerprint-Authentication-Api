package coccoc.pin;

public final class PinConfig {
    public static final int MAX_PIN_DIGIT = 4;
    public static final int MAX_PIN_INPUT = 5;

    // Pin retry timeout. Default is 60 seconds.
    public static final int PIN_RETRY_TIMER = 60 * 1000;

    public static final int PIN_BACKGROUND_TIMER = 0;

    public enum Type {
        PIN_PASSCODE
    }
}
