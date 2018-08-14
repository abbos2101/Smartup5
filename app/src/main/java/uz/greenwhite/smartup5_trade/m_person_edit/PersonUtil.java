package uz.greenwhite.smartup5_trade.m_person_edit;


import android.text.TextUtils;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.uzum.Uzum;
import uz.greenwhite.smartup5_trade.Utils;
import uz.greenwhite.smartup5_trade.common.roles.TradeRoleKeys;
import uz.greenwhite.smartup5_trade.datasource.DSUtil;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.NaturalPersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.bean.PersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.NaturalPersonData;
import uz.greenwhite.smartup5_trade.m_person_edit.ui.PersonData;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VCharactsGroup;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VLegalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VNaturalPerson;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VNaturalPersonAdditionally;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VNaturalPersonInfo;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VOutletProps;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonAccount;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonAddress;
import uz.greenwhite.smartup5_trade.m_person_edit.variable.VPersonInfo;
import uz.greenwhite.smartup5_trade.m_session.bean.role.Role;
import uz.greenwhite.smartup5_trade.m_session.bean.role.RoleSetting;

public class PersonUtil {

    public static String stringify(VPersonInfo vPersonInfo) {
        return Uzum.toJson(vPersonInfo.toValue(), PersonInfo.UZUM_ADAPTER);
    }

    public static String stringifyNaturalPerson(VNaturalPersonInfo vPersonInfo) {
        return Uzum.toJson(vPersonInfo.toValue(), NaturalPersonInfo.UZUM_ADAPTER);
    }

    public static VPersonInfo make(PersonData data, PersonInfo info) {
        VOutletProps vProps = new VOutletProps(info, data.getScope());
        VLegalPerson vPerson = new VLegalPerson(info.legalPerson);
        VCharactsGroup vPersonCharacts = new VCharactsGroup(data.getPersonGroups(),
                info.personCharacts, !TextUtils.isEmpty(info.legalPerson.personId));
        VPersonAddress vPersonAddress = new VPersonAddress(info.personAddresses);
        VPersonAccount vPersonAccount = new VPersonAccount(info.personAccounts);

        vPersonAccount.makeCurrency(data.getAccount().currencies);

        return new VPersonInfo(info, vPerson, vPersonCharacts, vPersonAddress, vPersonAccount, vProps, info.personKind);
    }

    public static VNaturalPersonInfo makeNaturalPerson(NaturalPersonData data, NaturalPersonInfo info) {
        VNaturalPerson vPerson = new VNaturalPerson(info.naturalPerson);
        VNaturalPersonAdditionally vNaturalPersonAdditionally = new VNaturalPersonAdditionally(info.naturalPersonAdditionally);
        VCharactsGroup vPersonCharacts = new VCharactsGroup(data.getPersonGroups(),
                info.characts, !TextUtils.isEmpty(info.naturalPerson.personId));

        return new VNaturalPersonInfo(info, vPerson, vNaturalPersonAdditionally, vPersonCharacts);
    }

    public static boolean checkRolePersonEditOrVisible(Scope scope) {
        TradeRoleKeys tradeRoleKeys = scope.ref.getRoleKeys();
        return Utils.isRole(scope, tradeRoleKeys.agent, tradeRoleKeys.agentMerchandiser, tradeRoleKeys.supervisor) ||
                Utils.isRole(scope, tradeRoleKeys.merchandiser, tradeRoleKeys.expeditor, tradeRoleKeys.vanseller) ||
                Utils.isRole(scope, Role.PHARMACY, Role.DOCTOR, Role.PHARMCY_PLUS_DOCTOR);
    }

    public static boolean hasEdit(Scope scope, String code) {
        assert scope.ref != null;

        if (!checkRolePersonEditOrVisible(scope)) {
            return false;
        }

        MyArray<Role> roles = DSUtil.getFilialRoles(scope);
        for (Role r : roles) {
            RoleSetting setting = scope.ref.findRoleSetting(r.roleId);
            if (setting != null) {
                if (setting.outletModuleEditings.contains(code, MyMapper.<String>identity())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasVisible(Scope scope, String code) {
        assert scope.ref != null;

        if (!checkRolePersonEditOrVisible(scope)) {
            return false;
        }

        MyArray<Role> roles = DSUtil.getFilialRoles(scope);
        for (Role r : roles) {
            RoleSetting setting = scope.ref.findRoleSetting(r.roleId);
            if (setting != null) {
                if (setting.outletModuleVisibles.contains(code, MyMapper.<String>identity())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isRequired(Scope scope, String code) {
        assert scope.ref != null;
        MyArray<Role> filialRoles = DSUtil.getFilialRoles(scope);
        for (Role r : filialRoles) {
            RoleSetting setting = scope.ref.findRoleSetting(r.roleId);
            if (setting != null) {
                if (setting.outletModuleRequireds.contains(code, MyMapper.<String>identity())) {
                    return true;
                }
            }
        }
        return false;
    }
}
