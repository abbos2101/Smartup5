package uz.greenwhite.smartup5_trade.m_person_edit.variable;


import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.collection.MyPredicate;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonProps;
import uz.greenwhite.smartup5_trade.m_session.bean.Outlet;

public class VOutletProps extends VariableLike {

    public final ValueString typeorg;
    public final ValueString department;
    public final ValueString typeown;
    public final ValueString orgform;
    public final ValueString laddress;
    public final ValueString inn;
    public final ValueString kopf;
    public final ValueString kfc;
    public final ValueString okonx;
    public final ValueString okpo;
    public final ValueString okud;
    public final ValueString coato;
    public final ValueString coogu;
    public final ValueString oked;
    public final ValueString svift;
    public final ValueSpinner parent;

    public VOutletProps(PersonInfo info, Scope scope) {
        this.typeorg = new ValueString(200, info.props.typeorg);
        this.department = new ValueString(300, info.props.department);
        this.typeown = new ValueString(100, info.props.typeown);
        this.orgform = new ValueString(100, info.props.orgform);
        this.laddress = new ValueString(100, info.props.laddress);
        this.inn = new ValueString(18, info.props.inn);
        this.kopf = new ValueString(30, info.props.kopf);
        this.kfc = new ValueString(30, info.props.kfc);
        this.okonx = new ValueString(10, info.props.okonx);
        this.okpo = new ValueString(30, info.props.okpo);
        this.okud = new ValueString(30, info.props.okud);
        this.coato = new ValueString(30, info.props.coato);
        this.coogu = new ValueString(30, info.props.coogu);
        this.oked = new ValueString(30, info.props.oked);
        this.svift = new ValueString(40, info.props.svift);
        this.parent = makeSpinner(scope, info);
    }

    private ValueSpinner makeSpinner(Scope scope, final PersonInfo info) {
        MyArray<SpinnerOption> options = MyArray.from(new SpinnerOption("", ""));
        MyArray<Outlet> filialOutlets = DSUtil.getFilialOutlets(scope);
        //parent cannot be a child! so, the list is filtered eliminating the parentId equals to
        // current personId
        MyArray<Outlet> filteredOutlets = filialOutlets.filter(new MyPredicate<Outlet>() {
            @Override
            public boolean apply(Outlet val) {
                return TextUtils.isEmpty(info.legalPerson.personId) ||
                        !val.parentId.equalsIgnoreCase(info.legalPerson.personId) &&
                                !val.id.equalsIgnoreCase(info.legalPerson.personId);
            }
        });

        if (!filteredOutlets.isEmpty()) {
            options = options.append(filteredOutlets.map(new MyMapper<Outlet, SpinnerOption>() {
                @Override
                public SpinnerOption apply(Outlet val) {
                    return new SpinnerOption(val.id, val.name, val);
                }
            }));
        }
        SpinnerOption option = options.get(0);
        if (!TextUtils.isEmpty(info.props.parentId)) {
            option = options.findFirst(new MyPredicate<SpinnerOption>() {
                @Override
                public boolean apply(SpinnerOption spinnerOption) {
                    return spinnerOption.code.equalsIgnoreCase(info.props.parentId);
                }
            });
        }
        return new ValueSpinner(options, option);
    }

    public PersonProps toValue() {
        return new PersonProps(this.typeorg.getText(), this.department.getText(),
                typeown.getText(), orgform.getText(), laddress.getText(),
                inn.getText(), kopf.getText(), kfc.getText(), okonx.getText(),
                okpo.getText(), okud.getText(), coato.getText(),
                coogu.getText(), oked.getText(), svift.getText(), parent.getValue().code);
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(typeorg, department, typeown, orgform,
                laddress, inn, kopf, kfc, okonx, okpo,
                okud, coato, coogu, oked, svift, parent).toSuper();
    }
}
