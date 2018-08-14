package uz.greenwhite.smartup5_trade.common;

public enum SensorState {
    NOT_SUPPORTED,
    NOT_BLOCKED, // если устройство не защищено пином, рисунком или паролем
    NO_FINGERPRINTS, // если на устройстве нет отпечатков
    READY
}
