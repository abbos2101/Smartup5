package uz.greenwhite.smartup5_trade.m_near.util;

public interface InterceptListener<T> {
    void intercept(int key, T result);
}
