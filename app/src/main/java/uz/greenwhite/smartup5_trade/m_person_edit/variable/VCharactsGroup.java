package uz.greenwhite.smartup5_trade.m_person_edit.variable;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.util.Util;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueArray;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.GroupType;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditGroup;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditGroupType;


public class VCharactsGroup extends VariableLike {

    private ValueArray<VCharactItem> groups;
    private final MyArray<GroupType> characts;
    private final boolean outletEdit;

    public VCharactsGroup(final MyArray<PersonEditGroup> personEditGroups, final MyArray<GroupType> characts, final boolean outletEdit) {
        this.characts = characts;
        this.outletEdit = outletEdit;
        if (personEditGroups.isEmpty()) {
            this.groups = new ValueArray<>(characts.map(new MyMapper<GroupType, VCharactItem>() {
                @Override
                public VCharactItem apply(GroupType groupType) {
                    return new VCharactItem(groupType.toPersonEditGroup(),
                            Util.nvl(groupType, GroupType.DEFAULT));
                }
            }));
        } else {
            this.groups = new ValueArray<>(personEditGroups.map(new MyMapper<PersonEditGroup, VCharactItem>() {
                @Override
                public VCharactItem apply(PersonEditGroup chGroup) {
                    GroupType found = characts.find(chGroup.groupId, GroupType.KEY_ADAPTER);
                    if (outletEdit && characts.nonEmpty() && found == null) {
                        return null;
                    }
                    return new VCharactItem(chGroup, Util.nvl(found, GroupType.DEFAULT));
                }
            }).filterNotNull());
        }
    }


    public void makeGroups(final MyArray<PersonEditGroup> groups) {
        this.groups = new ValueArray<>(groups.map(new MyMapper<PersonEditGroup, VCharactItem>() {
            @Override
            public VCharactItem apply(PersonEditGroup chGroup) {
                GroupType groupType = characts.find(chGroup.groupId, GroupType.KEY_ADAPTER);
                VCharactItem found = VCharactsGroup.this.groups.getItems()
                        .find(chGroup.groupId, VCharactItem.KEY_ADAPTER);
                if (found != null) {
                    return new VCharactItem(chGroup, found.val);
                }
                return new VCharactItem(chGroup, Util.nvl(groupType, GroupType.DEFAULT));
            }
        }).filterNotNull());
    }

    public ValueArray<VCharactItem> getGroups() {
        return groups;
    }

    public MyArray<GroupType> toValue() {
        List<GroupType> r = new ArrayList<>();
        for (VCharactItem group : this.groups.getItems()) {
            ValueSpinner spinner = group.spinner;
            SpinnerOption value = spinner.getValue();
            if (!TextUtils.isEmpty(value.code) && value.tag != null) {
                PersonEditGroupType type = (PersonEditGroupType) value.tag;
                r.add(new GroupType(group.group.groupId, group.group.name, type.typeId, type.name));
            }
        }
        return MyArray.from(r);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return groups.getItems().toSuper();
    }

}