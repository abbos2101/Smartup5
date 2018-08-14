package uz.greenwhite.smartup5_trade.m_file_manager.bean;


import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class User {
    public final String userId;
    public final String userName;

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public static final UzumAdapter<User> UZUM_ADAPTER = new UzumAdapter<User>() {
        @Override
        public User read(UzumReader in) {
            return new User(in.readString(), in.readString());
        }

        @Override
        public void write(UzumWriter out, User user) {
            out.write(user.userId);
            out.write(user.userName);
        }
    };
}
