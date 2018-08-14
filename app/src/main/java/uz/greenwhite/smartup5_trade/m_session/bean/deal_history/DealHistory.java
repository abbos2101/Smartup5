package uz.greenwhite.smartup5_trade.m_session.bean.deal_history;


import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class DealHistory {

    public static final String DEAL_STATE_DRAFT = "D";
    public static final String DEAL_STATE_NEW = "N";
    public static final String DEAL_STATE_EXECUTING = "E";
    public static final String DEAL_STATE_WAITING = "W";
    public static final String DEAL_STATE_SHIPPED = "S";
    public static final String DEAL_STATE_COMPLETED = "C";
    public static final String DEAL_STATE_ARCHIVED = "A";


    public final String filialId;
    public final String roomId;
    public final String personId;
    public final String dealId;
    public final String dealState;
    public final MyArray<DealAmount> dealAmount;
    public final String dealDate;
    public final String dealDeliveryDate;
    public final MyArray<DealProduct> dealProducts;

    public DealHistory(String filialId,
                       String roomId,
                       String personId,
                       String dealId,
                       String dealState,
                       MyArray<DealAmount> dealAmount,
                       String dealDate,
                       String dealDeliveryDate,
                       MyArray<DealProduct> dealProducts) {
        this.filialId = filialId;
        this.roomId = roomId;
        this.personId = personId;
        this.dealId = dealId;
        this.dealState = dealState;
        this.dealAmount = dealAmount;
        this.dealDate = dealDate;
        this.dealDeliveryDate = dealDeliveryDate;
        this.dealProducts = dealProducts;
    }

    public static final UzumAdapter<DealHistory> UZUM_ADAPTER = new UzumAdapter<DealHistory>() {
        @Override
        public DealHistory read(UzumReader in) {
            return new DealHistory(
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readArray(DealAmount.UZUM_ADAPTER),
                    in.readString(), in.readString(),
                    in.readArray(DealProduct.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, DealHistory val) {
            out.write(val.filialId);
            out.write(val.roomId);
            out.write(val.personId);
            out.write(val.dealId);
            out.write(val.dealState);
            out.write(val.dealAmount, DealAmount.UZUM_ADAPTER);
            out.write(val.dealDate);
            out.write(val.dealDeliveryDate);
            out.write(val.dealProducts, DealProduct.UZUM_ADAPTER);
        }
    };

    public String getStateName() {
        switch (this.dealState) {
            case DEAL_STATE_DRAFT:
                return DS.getString(R.string.deal_state_draft);
            case DEAL_STATE_NEW:
                return DS.getString(R.string.deal_state_new);
            case DEAL_STATE_EXECUTING:
                return DS.getString(R.string.deal_state_executing);
            case DEAL_STATE_WAITING:
                return DS.getString(R.string.deal_state_waiting);
            case DEAL_STATE_SHIPPED:
                return DS.getString(R.string.deal_state_shipped);
            case DEAL_STATE_COMPLETED:
                return DS.getString(R.string.deal_state_completed);
            case DEAL_STATE_ARCHIVED:
                return DS.getString(R.string.deal_state_archived);
            default:
                return "";
        }
    }

    public int getIconBackground() {
        switch (this.dealState) {
            case DEAL_STATE_DRAFT:
                return R.color.deal_draft;
            case DEAL_STATE_NEW:
                return R.color.deal_new;
            case DEAL_STATE_EXECUTING:
                return R.color.deal_execute;
            case DEAL_STATE_WAITING:
                return R.color.deal_wait;
            case DEAL_STATE_SHIPPED:
                return R.color.deal_shipped;
            case DEAL_STATE_COMPLETED:
                return R.color.deal_complete;
            default:
                return R.color.deal_others;
        }
    }

    public int getCurrencyBackground() {
        switch (this.dealState) {
            case DEAL_STATE_DRAFT:
                return R.drawable.deal_state_draft;
            case DEAL_STATE_NEW:
                return R.drawable.deal_state_new;
            case DEAL_STATE_EXECUTING:
                return R.drawable.deal_state_execute;
            case DEAL_STATE_WAITING:
                return R.drawable.deal_state_wait;
            case DEAL_STATE_SHIPPED:
                return R.drawable.deal_state_shipped;
            case DEAL_STATE_COMPLETED:
                return R.drawable.deal_state_complete;
            default:
                return R.drawable.deal_state_others;
        }
    }

    public int getIconImage() {
        switch (this.dealState) {
            case DEAL_STATE_NEW:
                return R.drawable.ic_event_available_white_24dp;
            case DEAL_STATE_EXECUTING:
                return R.drawable.ic_sync_white_24dp;
            case DEAL_STATE_WAITING:
                return R.drawable.ic_timer_white_24dp;
            case DEAL_STATE_SHIPPED:
                return R.drawable.ic_unarchive_white_24dp;
            case DEAL_STATE_COMPLETED:
                return R.drawable.ic_local_shipping_white_24dp;
            case DEAL_STATE_ARCHIVED:
                return R.drawable.ic_content_paste_white_24dp;
            default:
                return R.drawable.ic_event_note_white_24dp;
        }
    }

    /*    returns the int value of deal states. It start from 0 (draft) to x (archived). It is used
       for sorting purposes.
    */
    public int getStateIntValue() {
        switch (this.dealState) {
            case DEAL_STATE_DRAFT:
                return 0;
            case DEAL_STATE_NEW:
                return 1;
            case DEAL_STATE_EXECUTING:
                return 2;
            case DEAL_STATE_WAITING:
                return 3;
            case DEAL_STATE_SHIPPED:
                return 4;
            case DEAL_STATE_COMPLETED:
                return 5;
            case DEAL_STATE_ARCHIVED:
                return 6;
            default:
                return -1;
        }
    }

}