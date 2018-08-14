package uz.greenwhite.smartup5_trade;


import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class AppVersionNameLoader extends AsyncTask<Void, String, String> {

    private static final String PLAY_MARKET_URL = "https://play.google.com/store/apps/details?id=";
    private static final String APP_URL = "uz.greenwhite.smartup5_trade&hl=en";
    private static final String OWN_TEXT = "Current Version";
    private static final String REFERRER = "https://www.google.com";

    private String newVersion = "";

    @Override
    protected String doInBackground(Void... voids) {
        if (NotificationUtil.isNetworkAvailable(SmartupApp.getContext())) {
            try {
                Document document = Jsoup.connect(PLAY_MARKET_URL + APP_URL)
                        .timeout(5000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer(REFERRER)
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText(OWN_TEXT);
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newVersion;
    }


    @Override
    protected void onPostExecute(String onlineVersion) {
        super.onPostExecute(onlineVersion);
        String currentVersion = "";
        try {
            currentVersion = BuildConfig.VERSION_NAME;
            if (!TextUtils.isEmpty(onlineVersion)) {
                if (BuildConfig.DEBUG) {
                    currentVersion = currentVersion.substring(0, currentVersion.indexOf("-"));
                }
                if (!currentVersion.equals(onlineVersion)) {
                    NotificationUtil.notifyUpdate(SmartupApp.getContext(), APP_URL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("update", "Current version " + currentVersion + " playstore version " + onlineVersion);
    }

}
