package uz.greenwhite.smartup5_trade.m_movement.variable;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.collection.MyMapper;
import uz.greenwhite.lib.variable.SpinnerOption;
import uz.greenwhite.lib.variable.ValueSpinner;
import uz.greenwhite.lib.variable.ValueString;
import uz.greenwhite.lib.variable.Variable;
import uz.greenwhite.lib.variable.VariableLike;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncoming;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingHolder;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingPost;
import uz.greenwhite.smartup5_trade.m_session.bean.Filial;
import uz.greenwhite.smartup5_trade.m_session.bean.Warehouse;

public class VMovementIncoming extends VariableLike {

    public final MovementIncoming incoming;
    public final MovementIncomingHolder holder;

    public final ValueString vDate;
    public final ValueSpinner vWarehouser;

    public VMovementIncoming(MovementIncoming incoming,
                             MovementIncomingHolder holder,
                             ValueString vDate,
                             ValueSpinner vWarehouser) {
        this.incoming = incoming;
        this.holder = holder;
        this.vDate = vDate;
        this.vWarehouser = vWarehouser;
    }

    @Override
    protected MyArray<Variable> gatherVariables() {
        return MyArray.from(vDate, vWarehouser).toSuper();
    }

    public static VMovementIncoming build(final Scope scope, MovementIncomingHolder holder) {
        MovementIncoming movementIncoming = scope.ref.getMovementIncomings().find(holder.incoming.movementId, MovementIncoming.KEY_ADAPTER);
        Filial filial = scope.ref.getFilial(scope.filialId);

        MyArray<SpinnerOption> warehouses = filial.warehouseIds.map(new MyMapper<String, SpinnerOption>() {
            @Override
            public SpinnerOption apply(String warehouseId) {
                Warehouse warehouse = scope.ref.getWarehouse(warehouseId);
                if (warehouse == null) return null;
                return new SpinnerOption(warehouseId, warehouse.name, warehouse);
            }
        }).filterNotNull();

        ValueSpinner vWarehouse = new ValueSpinner(warehouses, warehouses.find(holder.incoming.warehouseId, SpinnerOption.KEY_ADAPTER));
        ValueString vDate = new ValueString(20);
        vDate.setText(holder.incoming.date);
        return new VMovementIncoming(movementIncoming, holder, vDate, vWarehouse);
    }

    public static MovementIncomingHolder toValue(VMovementIncoming vMovementIncoming) {
        MovementIncomingPost movementIncomingPost = new MovementIncomingPost(vMovementIncoming.holder.incoming.entryId,
                vMovementIncoming.incoming.movementId, vMovementIncoming.vDate.getText(),
                vMovementIncoming.vWarehouser.getValue().code);
        return new MovementIncomingHolder(movementIncomingPost, vMovementIncoming.holder.state);
    }
}
