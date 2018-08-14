package uz.greenwhite.smartup5_trade.m_person_edit.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonAddress;
import uz.greenwhite.smartup5_trade.m_session.bean.Region;

public class VPersonAddress extends VariableLike {

    public ValueArray<VPersonAddressItem> personItem;


    public VPersonAddress(MyArray<PersonAddress> address) {
        this.personItem = new ValueArray<>(makePersonItem(address));
    }

    private MyArray<VPersonAddressItem> makePersonItem(MyArray<PersonAddress> address) {
        return address.map(new MyMapper<PersonAddress, VPersonAddressItem>() {
            @Override
            public VPersonAddressItem apply(PersonAddress val) {
                return new VPersonAddressItem(val);
            }
        });
    }

    public void makeRegion(MyArray<Region> regions) {
        if (regions.isEmpty()) return;
        for (VPersonAddressItem item : personItem.getItems()) {
            item.setRegion(regions);
        }
    }

    public void removeAddress(VPersonAddressItem val) {
        personItem.delete(val);
    }

    public void addNewAddress() {
        personItem.append(new VPersonAddressItem(PersonAddress.DEFAULT));
    }

    public MyArray<PersonAddress> toValue() {
        return personItem.getItems().map(new MyMapper<VPersonAddressItem, PersonAddress>() {
            @Override
            public PersonAddress apply(VPersonAddressItem item) {
                return item.toValue();
            }
        });
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(personItem).toSuper();
    }
}
