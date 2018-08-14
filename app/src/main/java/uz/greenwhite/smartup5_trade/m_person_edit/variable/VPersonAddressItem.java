package uz.greenwhite.smartup5_trade.m_person_edit.variable;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.ErrorResult;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAddress;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class VPersonAddressItem extends VariableLike {

    public final ValueSpinner typeAddress;
    public final ValueString address;
    public final ValueString postCode;
    private ValueSpinner region;

    public VPersonAddressItem(PersonAddress address) {
        this.typeAddress = makeTypeAddress(address.typeAddress);
        this.address = new ValueString(200, address.address);
        this.postCode = new ValueString(20, address.postCode);
        this.region = new ValueSpinner(makeRegion(address.region));
    }

    private ValueSpinner makeTypeAddress(String typeAddress) {
        MyArray<SpinnerOption> options = MyArray.from(
                new SpinnerOption(PersonAddress.ADDRESS_TYPE_HOME, DS.getString(R.string.person_address_home)),
                new SpinnerOption(PersonAddress.ADDRESS_TYPE_DELIVERY, DS.getString(R.string.delivery)),
                new SpinnerOption(PersonAddress.ADDRESS_TYPE_WORK, DS.getString(R.string.work)),
                new SpinnerOption(PersonAddress.ADDRESS_TYPE_JURIDIC, DS.getString(R.string.person_address_juridic)),
                new SpinnerOption(PersonAddress.ADDRESS_TYPE_FACTUAL, DS.getString(R.string.person_address_factial)),
                new SpinnerOption(PersonAddress.ADDRESS_TYPE_OTHERS, DS.getString(R.string.others))
        );

        SpinnerOption option = options.get(0);
        if (!TextUtils.isEmpty(typeAddress)) {
            option = options.find(typeAddress, SpinnerOption.KEY_ADAPTER);
        }
        return new ValueSpinner(options, option);
    }

    private MyArray<SpinnerOption> makeRegion(Region region) {
        MyArray<SpinnerOption> regions = MyArray.from(new SpinnerOption("", DS.getString(R.string.not_selected)));
        if (!TextUtils.isEmpty(region.regionId)) {
            regions = regions.append(new SpinnerOption(region.regionId, region.name, region));
        }
        return regions;
    }

    public ValueSpinner getRegion() {
        return region;
    }

    public void setRegion(MyArray<Region> regions) {
        MyArray<SpinnerOption> options = regions.map(new MyMapper<Region, SpinnerOption>() {
            @Override
            public SpinnerOption apply(Region region) {
                return new SpinnerOption(region.regionId, region.name, region);
            }
        });
        options = options.prepend(new SpinnerOption("", DS.getString(R.string.not_selected)));

        SpinnerOption value = region.getValue();
        SpinnerOption find;
        if ((find = options.find(value.code, SpinnerOption.KEY_ADAPTER)) == null) {
            options = options.append(value);
        }
        this.region = new ValueSpinner(options, Util.nvl(find, value));
    }

    //----------------------------------------------------------------------------------------------

    public PersonAddress toValue() {
        SpinnerOption typeAddress = this.typeAddress.getValue();
        SpinnerOption regionValue = this.region.getValue();
        Region region = regionValue == null ? Region.DEFAULT : (Region) regionValue.tag;

        return new PersonAddress(typeAddress.code,
                this.address.getText(),
                this.postCode.getText(),
                region);
    }

    @Override
    public ErrorResult getError() {
        if (this.address.isEmpty()) {
            return ErrorResult.make(DS.getString(R.string.fill_in_required_fields));
        }
        return ErrorResult.NONE;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(typeAddress, address, postCode, region).toSuper();
    }
}
