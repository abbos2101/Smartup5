package uz.greenwhite.smartup5_trade.m_display;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.error.AppError;
import uz.greenwhite.smartup.anor.datasource.EntryValue;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.R;
import uz.greenwhite.smartup5_trade.datasource.DS;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayBarcode;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayHolder;
import uz.greenwhite.smartup5_trade.m_display.bean.DisplayRequest;
import uz.greenwhite.smartup5_trade.m_display.ui.DisplayData;
import uz.greenwhite.smartup5_trade.m_outlet.arg.ArgOutlet;

public class DisplayApi {

    public static DisplayHolder getDisplayHolder(Scope scope, String outletId) {
        assert scope.entry != null;
        EntryValue<DisplayBarcode> entryDisplay = scope.entry.getOutletDisplayBarcode(outletId);
        DisplayBarcode display;
        EntryState entryState = EntryState.NOT_SAVED_ENTRY;
        if (entryDisplay != null) {
            display = entryDisplay.value;
            entryState = entryDisplay.getEntryState();
        } else {
            String entryId = String.valueOf(AdminApi.nextSequence());
            display = new DisplayBarcode(entryId, scope.filialId,
                    outletId, MyArray.<DisplayRequest>emptyArray());
        }
        return new DisplayHolder(display, entryState);
    }

    public static void displayDelete(ArgOutlet arg, DisplayData data) {
        Scope scope = arg.getScope();
        String entryId = data.vDisplay.holder.display.entryId;
        scope.ds.db.entryDelete(entryId);
        scope.ds.db.photoUpdateStateByEntry(entryId, EntryState.SAVED);
        scope.ds.db.photoDeleteByState(EntryState.SAVED);
    }

    public static void displayMakeEdit(ArgOutlet arg, DisplayData data) {
        Scope scope = arg.getScope();
        String entryId = data.vDisplay.holder.display.entryId;
        scope.ds.db.tryMakeStateSaved(entryId);
        scope.ds.db.photoUpdateStateByEntry(entryId, EntryState.SAVED);
    }

    @SuppressWarnings("ConstantConditions")
    public static void saveDisplay(ArgOutlet arg, DisplayData data, boolean ready) {
        Scope scope = arg.getScope();

        DisplayBarcode value = data.vDisplay.toValue();
        scope.entry.saveOutletDisplayBarcode(value);
        for (DisplayRequest request : value.displayBarcode) {
            scope.ds.db.photoUpdateStateBySha(request.photoSha, EntryState.SAVED);
        }
        scope.ds.db.photoDeleteByState(EntryState.NOT_SAVED);

        if (ready) {
            scope.ds.db.tryMakeStateReady(value.entryId);
            scope.ds.db.photoUpdateStateByEntry(value.entryId, EntryState.READY);
            if (scope.ds.db.entryLoadState(value.entryId) != EntryState.READY) {
                throw new AppError(DS.getString(R.string.deal_error_in_ready_deal));
            }
        }
    }
}
