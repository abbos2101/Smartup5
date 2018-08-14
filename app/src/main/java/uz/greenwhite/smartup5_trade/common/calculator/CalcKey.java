package uz.greenwhite.smartup5_trade.common.calculator;

public enum CalcKey {
    KEY_1("1"),
    KEY_2("2"),
    KEY_3("3"),
    KEY_4("4"),
    KEY_5("5"),
    KEY_6("6"),
    KEY_7("7"),
    KEY_8("8"),
    KEY_9("9"),
    KEY_0("0"),
    KEY_00("00"),

    KEY_DOT("."),
    KEY_REMOVE("remove"),
    KEY_PLUS_ONE("+1"),
    KEY_SUBTRACT_ONE("-1"),
    KEY_OK("ok");

    public final String param;

    CalcKey(String param) {
        this.param = param;
    }

    public boolean isZero() {
        return "0".equals(this.param) && "00".equals(this.param);
    }

    public boolean isDot() {
        return ".".equals(this.param);
    }

    public boolean isRemove() {
        return "remove".equals(this.param);
    }

    public boolean isSubtractOne() {
        return "-1".equals(this.param);
    }

    public boolean isOk() {
        return "ok".equals(this.param);
    }
}
