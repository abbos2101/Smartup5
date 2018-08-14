package uz.greenwhite.smartup5_trade.m_order_info.bean;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.mold.NavigationItem;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class OrderForm {

    public static final int INFO = 0;
    public static final int ACCOUNT = 1;
    public static final int GIFT = 2;
    public static final int ORDER = 3;
    public static final int STOCK = 4;
    public static final int PHOTO = 5;


    public final MyArray<OrderCurAccount> curAccount;
    public final MyArray<OrderCurGift> curGift;
    public final MyArray<OrderCurOrder> curOrder;
    public final MyArray<OrderCurStock> curStocks;
    public final MyArray<OrderCurPhoto> curPhotos;

    public OrderForm(MyArray<OrderCurAccount> curAccount,
                     MyArray<OrderCurGift> curGift,
                     MyArray<OrderCurOrder> curOrder,
                     MyArray<OrderCurStock> curStocks,
                     MyArray<OrderCurPhoto> curPhotos) {
        this.curAccount = MyArray.nvl(curAccount);
        this.curGift = MyArray.nvl(curGift);
        this.curOrder = MyArray.nvl(curOrder);
        this.curStocks = MyArray.nvl(curStocks);
        this.curPhotos = MyArray.nvl(curPhotos);
    }


    public MyArray<NavigationItem> getForms() {
        List<NavigationItem> list = new ArrayList<>();
        list.add(new NavigationItem(INFO, DS.getString(R.string.info), R.drawable.ic_info_black_24dp));

        if (curStocks.nonEmpty()) {
            list.add(new NavigationItem(STOCK, DS.getString(R.string.order_stock), R.drawable.ic_visibility_black_24dp));
        }
        if (curGift.nonEmpty()) {
            list.add(new NavigationItem(GIFT, DS.getString(R.string.order_gift), R.drawable.ic_card_giftcard_black_24dp));
        }
        if (curOrder.nonEmpty()) {
            list.add(new NavigationItem(ORDER, DS.getString(R.string.order_order), R.drawable.ic_shopping_cart_black_24dp));
        }
        if (curAccount.nonEmpty()) {
            list.add(new NavigationItem(ACCOUNT, DS.getString(R.string.order_account), R.drawable.ic_local_atm_black_24dp));
        }

        if (curPhotos.nonEmpty()) {
            list.add(new NavigationItem(PHOTO, DS.getString(R.string.photo), R.drawable.ic_photo_black_24dp));
        }
        return MyArray.from(list);
    }

    public static final UzumAdapter<OrderForm> UZUM_ADAPTER = new UzumAdapter<OrderForm>() {
        @Override
        public OrderForm read(UzumReader in) {
            return new OrderForm(in.readArray(OrderCurAccount.UZUM_ADAPTER),
                    in.readArray(OrderCurGift.UZUM_ADAPTER),
                    in.readArray(OrderCurOrder.UZUM_ADAPTER),
                    in.readArray(OrderCurStock.UZUM_ADAPTER),
                    in.readArray(OrderCurPhoto.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, OrderForm val) {
            out.write(val.curAccount, OrderCurAccount.UZUM_ADAPTER);
            out.write(val.curGift, OrderCurGift.UZUM_ADAPTER);
            out.write(val.curOrder, OrderCurOrder.UZUM_ADAPTER);
            out.write(val.curStocks, OrderCurStock.UZUM_ADAPTER);
            out.write(val.curPhotos, OrderCurPhoto.UZUM_ADAPTER);
        }
    };
}
