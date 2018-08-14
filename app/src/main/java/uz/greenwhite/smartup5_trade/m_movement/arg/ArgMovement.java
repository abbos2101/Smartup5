package uz.greenwhite.smartup5_trade.m_movement.arg;

import android.text.TextUtils;

import java.util.Date;

import uz.greenwhite.lib.util.DateUtil;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;
import uz.greenwhite.smartup.anor.datasource.persist.EntryState;
import uz.greenwhite.smartup.anor.m_admin.AdminApi;
import uz.greenwhite.smartup5_trade.datasource.Scope;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncoming;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingHolder;
import uz.greenwhite.smartup5_trade.m_movement.bean.MovementIncomingPost;
import uz.greenwhite.smartup5_trade.m_session.arg.ArgSession;

public class ArgMovement extends ArgSession {

    public static final int K_OPEN = 1;
    public static final int K_ACCEPT = 2;

    public final int openSate;
    public final String movementId;

    public ArgMovement(ArgSession arg, int openSate, String movementId) {
        super(arg.accountId, arg.filialId);
        this.openSate = openSate;
        this.movementId = movementId;
    }

    public ArgMovement(UzumReader in) {
        super(in);
        this.openSate = in.readInt();
        this.movementId = in.readString();
    }

    @Override
    public void write(UzumWriter w) {
        super.write(w);
        w.write(openSate);
        w.write(movementId);
    }

    public MovementIncomingHolder getHolder() {
        Scope scope = getScope();
        MovementIncomingHolder find = scope.entry.getAllMovementIncoming().find(movementId, MovementIncomingHolder.KEY_ADAPTER);
        MovementIncoming movementIncoming = scope.ref.getMovementIncomings().find(movementId, MovementIncoming.KEY_ADAPTER);

        if (find == null) {
            String entryId = String.valueOf(AdminApi.nextSequence());
            String today = TextUtils.isEmpty(movementIncoming.date) ?
                    DateUtil.format(new Date(), DateUtil.FORMAT_AS_DATE) : movementIncoming.date;
            MovementIncomingPost movement = new MovementIncomingPost(entryId,
                    movementId, today, movementIncoming.warehouseId);
            return new MovementIncomingHolder(movement, EntryState.NOT_SAVED_ENTRY);

        } else {
            return find;
        }
    }

    public static final UzumAdapter<ArgMovement> UZUM_ADAPTER = new UzumAdapter<ArgMovement>() {
        @Override
        public ArgMovement read(UzumReader in) {
            return new ArgMovement(in);
        }

        @Override
        public void write(UzumWriter out, ArgMovement val) {
            val.write(out);
        }
    };
}
