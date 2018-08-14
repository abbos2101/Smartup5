package uz.greenwhite.smartup5_trade.common;// 20.12.2016

public interface MyCommand<E> {
    void apply(E val);
}
