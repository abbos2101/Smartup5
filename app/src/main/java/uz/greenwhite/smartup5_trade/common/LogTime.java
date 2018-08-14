package uz.greenwhite.smartup5_trade.common;

public class LogTime {

    private long start;
    private long end;

    public void start() {
        start = System.currentTimeMillis();
    }

    public void end(String message) {
        end = System.currentTimeMillis();
        System.out.println("end(" + (end - start)+" ms): " + message);
    }
}
