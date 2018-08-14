package uz.greenwhite.smartup5_trade.datasource;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import uz.greenwhite.smartup.anor.ErrorUtil;

public class TapeFile {

    private final Context context;

    public TapeFile(Context context) {
        this.context = context;
    }

    public HashMap<String, String> load(String name) throws IOException {
        HashMap<String, String> buffer = new HashMap<>();

        if (TextUtils.isEmpty(name)) return buffer;

        FileInputStream in = null;
        try {
            try {
                in = context.openFileInput(name);
            } catch (FileNotFoundException e) {
                ErrorUtil.saveThrowable(e);
                return buffer;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"), 8129);
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.put(line.substring(0, line.indexOf("\t")), line);
            }
            return buffer;
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void print(HashMap<String, String> buffer, String name) throws IOException {
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "utf-8")));
            for (String val : buffer.values()) writer.println(val);
            writer.flush();
        } finally {
            if (out != null) out.close();
        }
    }
}
