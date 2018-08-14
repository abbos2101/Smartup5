package uz.greenwhite.smartup5_trade.common.roles;


import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

/*
    the list of role pref codes
 */

public class TradeRoleKeys {

    public final String operator;
    public final String manager;
    public final String supervisor;
    public final String agent;
    public final String expeditor;
    public final String merchandiser;
    public final String vanseller;
    public final String billCollector;
    public final String agentMerchandiser;
    public final String seller;

    public TradeRoleKeys(String operator,
                         String manager,
                         String supervisor,
                         String agent,
                         String expeditor,
                         String merchandiser,
                         String vanseller,
                         String billCollector,
                         String agentMerchandiser,
                         String seller) {
        this.operator = operator;
        this.manager = manager;
        this.supervisor = supervisor;
        this.agent = agent;
        this.expeditor = expeditor;
        this.merchandiser = merchandiser;
        this.vanseller = vanseller;
        this.billCollector = billCollector;
        this.agentMerchandiser = agentMerchandiser;
        this.seller = seller;
    }

    public static final TradeRoleKeys DEFAULT = new TradeRoleKeys(
            "trade1", "trade2",
            "trade3", "trade4",
            "trade5", "trade6",
            "trade7", "trade8",
            "trade9", "trade13");

    public static final UzumAdapter<TradeRoleKeys> UZUM_ADAPTER = new UzumAdapter<TradeRoleKeys>() {
        @Override
        public TradeRoleKeys read(UzumReader in) {
            return new TradeRoleKeys(
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString(),
                    in.readString(), in.readString()
            );
        }

        @Override
        public void write(UzumWriter out, TradeRoleKeys val) {
            out.write(val.operator);
            out.write(val.manager);
            out.write(val.supervisor);
            out.write(val.agent);
            out.write(val.expeditor);
            out.write(val.merchandiser);
            out.write(val.vanseller);
            out.write(val.billCollector);
            out.write(val.agentMerchandiser);
            out.write(val.seller);
        }
    };
}
