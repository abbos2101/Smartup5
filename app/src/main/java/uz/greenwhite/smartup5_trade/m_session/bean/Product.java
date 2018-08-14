package uz.greenwhite.smartup5_trade.m_session.bean;// 29.06.2016

import java.math.BigDecimal;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class Product {

    public static final String INPUT_BOX = "B";
    public static final String INPUT_QUANT = "Q";
    public static final String INPUT_ALL = "A";

    public final String id;
    public final String name;
    public final String measureName;
    public final int measureScale;
    public final String boxName;
    public final BigDecimal boxQuant;
    public final String saleKind;
    public final int orderNo;
    public final MyArray<ProductGroupValue> groups;
    public final BigDecimal weightNetto;
    public final BigDecimal litr;
    public final String code;
    public final String producerId;

    public Product(String id,
                   String name,
                   String measureName,
                   int measureScale,
                   String boxName,
                   BigDecimal boxQuant,
                   String saleKind,
                   Integer orderNo,
                   MyArray<ProductGroupValue> groups,
                   BigDecimal weightNetto,
                   BigDecimal litr,
                   String code,
                   String producerId) {
        this.id = id;
        this.name = name;
        this.measureName = measureName;
        this.measureScale = measureScale;
        this.boxName = boxName;
        this.boxQuant = Util.nvl(boxQuant, BigDecimal.ZERO);//TODO
        this.saleKind = saleKind;
        this.orderNo = Util.nvl(orderNo, 9999);
        this.groups = groups;
        this.weightNetto = Util.nvl(weightNetto, BigDecimal.ZERO);
        this.litr = Util.nvl(litr, BigDecimal.ZERO);
        this.code = Util.nvl(code);
        this.producerId = Util.nvl(producerId);
    }

    public Product() {
        this("", "", "", 0,
                "", BigDecimal.ZERO, "",
                0, MyArray.<ProductGroupValue>emptyArray(),
                BigDecimal.ZERO, BigDecimal.ZERO, "", "");
    }


    public boolean isInputBox() {
        return (INPUT_BOX.equals(saleKind) || INPUT_ALL.equals(saleKind)) &&
                boxQuant.compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean isInputQuant() {
        return INPUT_QUANT.equals(saleKind) || INPUT_ALL.equals(saleKind);
    }

    public BigDecimal getBoxPart(BigDecimal val) {
        if (val == null) {
            return null;
        }
        return val.divide(boxQuant, 0, BigDecimal.ROUND_FLOOR);
    }

    public BigDecimal getQuantPart(BigDecimal val) {
        if (val == null) {
            return null;
        }
        return val.remainder(boxQuant);
    }

    public BigDecimal getBoxQuant(BigDecimal boxPart, BigDecimal quantPart) {
        return boxPart.multiply(boxQuant).add(quantPart);
    }

    public BigDecimal getWeight() {
        return this.weightNetto.compareTo(BigDecimal.ZERO) != 0 ? this.weightNetto : litr;
    }

    public static final MyMapper<Product, String> KEY_ADAPTER = new MyMapper<Product, String>() {
        @Override
        public String apply(Product product) {
            return product.id;
        }
    };

    public static final UzumAdapter<Product> UZUM_ADAPTER = new UzumAdapter<Product>() {
        @Override
        public Product read(UzumReader in) {
            return new Product(in.readString(), in.readString(),
                    in.readString(), in.readInt(), in.readString(),
                    in.readBigDecimal(), in.readString(), in.readInteger(),
                    in.readArray(ProductGroupValue.UZUM_ADAPTER),
                    in.readBigDecimal(), in.readBigDecimal(),
                    in.readString(),in.readString());
        }

        @Override
        public void write(UzumWriter out, Product val) {
            out.write(val.id);
            out.write(val.name);
            out.write(val.measureName);
            out.write(val.measureScale);
            out.write(val.boxName);
            out.write(val.boxQuant);
            out.write(val.saleKind);
            out.write(val.orderNo);
            out.write(val.groups, ProductGroupValue.UZUM_ADAPTER);
            out.write(val.weightNetto);
            out.write(val.litr);
            out.write(val.code);
            out.write(val.producerId);
        }
    };

    @Override
    public String toString() {
        return name;
    }
}
