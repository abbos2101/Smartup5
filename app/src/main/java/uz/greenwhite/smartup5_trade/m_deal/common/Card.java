package uz.greenwhite.smartup5_trade.m_deal.common;

public class Card {

    public static Card EMPTY = new Card("");
    public static Card ANY = new Card(null);

    public static Card make(String code) {
        return (code == null ? ANY : (code.length() == 0 ? EMPTY : new Card(code)));
    }

    public final String code;

    private Card(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Card that = (Card) obj;
        return this.code == null ? that.code == null : this.code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code == null ? 0 : this.code.hashCode();
    }
}
