package uz.greenwhite.smartup5_trade.m_deal.variable;// 30.06.2016

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_deal.bean.DealHeader;
import uz.greenwhite.smartup5_trade.m_session.bean.RoundModel;

public class VDealHeader extends VariableLike {

    public Date begunOn;
    public Date endedOn;
    public int spendTime;
    public String locLatLng;
    public RoundModel roundModel;
    public final ValueString deliveryDate;

    @Nullable
    public final ValueSpinner contractNumber;
    @Nullable
    public final ValueSpinner expeditor;
    @Nullable
    public final ValueSpinner agents;


    public VDealHeader(DealHeader h,
                       @Nullable ValueSpinner contractNumber,
                       @Nullable ValueSpinner expeditor,
                       @Nullable ValueSpinner agents) {
        this.begunOn = DateUtil.parse(h.begunOn);
        this.endedOn = DateUtil.parse(h.endedOn);
        this.spendTime = h.spendTime;
        this.locLatLng = h.locLatLng;
        this.contractNumber = contractNumber;
        this.deliveryDate = new ValueString(10, h.deliveryDate);
        this.expeditor = expeditor;
        this.agents = agents;
        this.roundModel = h.roundModel;
    }

    public DealHeader toDealHeader() {
        SimpleDateFormat format = DateUtil.FORMAT_AS_DATETIME.get();
        return new DealHeader(
                format.format(begunOn),
                format.format(endedOn),
                spendTime,
                locLatLng,
                deliveryDate.getValue(),
                roundModel,
                getContractNumber(),
                getExpeditor(),
                getAgentId());
    }

    private String getContractNumber() {
        if (contractNumber != null) {
            return contractNumber.getValue().code;
        }
        return null;
    }

    private String getExpeditor() {
        if (expeditor != null) {
            return expeditor.getValue().code;
        }
        return null;
    }

    private String getAgentId() {
        if (agents != null) {
            return agents.getValue().code;
        }
        return null;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.<Variable>from(deliveryDate);
    }

    @Override
    public ErrorResult getError() {
        if (deliveryDate.nonEmpty()) {
            String today = DateUtil.format(new Date(), DateUtil.FORMAT_AS_NUMBER);
            String delivery = DateUtil.convert(deliveryDate.getValue(), DateUtil.FORMAT_AS_NUMBER);
            if (Integer.parseInt(today) > Integer.parseInt(delivery)) {
                return ErrorResult.make(DS.getString(R.string.deal_delivery_date_incorrect));
            }
        }
        return super.getError();
    }
}
