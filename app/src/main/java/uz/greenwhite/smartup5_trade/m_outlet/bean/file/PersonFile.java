package uz.greenwhite.smartup5_trade.m_outlet.bean.file;

import uz.greenwhite.lib.collection.MyArray;
import uz.greenwhite.lib.uzum.UzumAdapter;
import uz.greenwhite.lib.uzum.UzumReader;
import uz.greenwhite.lib.uzum.UzumWriter;

public class PersonFile {

    public final String personId;
    public final MyArray<PersonFileDetail> fileDetails;

    public PersonFile(String personId, MyArray<PersonFileDetail> fileDetails) {
        this.personId = personId;
        this.fileDetails = fileDetails;
    }

    public static final UzumAdapter<PersonFile> UZUM_ADAPTER = new UzumAdapter<PersonFile>() {
        @Override
        public PersonFile read(UzumReader in) {
            return new PersonFile(in.readString(), in.readArray(PersonFileDetail.UZUM_ADAPTER));
        }

        @Override
        public void write(UzumWriter out, PersonFile val) {
            out.write(val.personId);
            out.write(val.fileDetails, PersonFileDetail.UZUM_ADAPTER);
        }
    };
}
