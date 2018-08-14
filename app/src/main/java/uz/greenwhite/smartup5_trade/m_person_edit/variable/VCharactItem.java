package uz.greenwhite.smartup5_trade.m_person_edit.variable;

import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.GroupType;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditGroup;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonEditGroupType;

public class VCharactItem extends VariableLike {

    public final GroupType val;
    public final PersonEditGroup group;
    public final ValueSpinner spinner;

    public VCharactItem(PersonEditGroup group, GroupType val) {
        this.group = group;
        this.val = val;
        this.spinner = makeSpinner();
    }

    VCharactItem(PersonEditGroup group, GroupType val, ValueSpinner spinner) {
        this.val = val;
        this.group = group;
        this.spinner = spinner;
    }

    private ValueSpinner makeSpinner() {
        MyArray<SpinnerOption> options = group.types.map(new MyMapper<PersonEditGroupType, SpinnerOption>() {
            @Override
            public SpinnerOption apply(PersonEditGroupType val) {
                return new SpinnerOption(val.typeId, val.name, val);
            }
        }).prepend(NOT_SELECT);

        SpinnerOption selected = options.get(0);
        if (!TextUtils.isEmpty(val.typeId)) {
            selected = options.find(val.typeId, SpinnerOption.KEY_ADAPTER);
        }
        return new ValueSpinner(options, selected);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(spinner).toSuper();
    }

    public static final MyMapper<VCharactItem, String> KEY_ADAPTER = new MyMapper<VCharactItem, String>() {
        @Override
        public String apply(VCharactItem val) {
            return val.group.groupId;
        }
    };

    private static final SpinnerOption NOT_SELECT = new SpinnerOption("", DS.getString(R.string.not_selected));
}
