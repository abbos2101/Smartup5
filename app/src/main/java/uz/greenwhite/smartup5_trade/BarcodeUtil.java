package uz.greenwhite.smartup5_trade;// 29.08.2016

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

import uz.greenwhite.lib.Command;
import uz.greenwhite.lib.util.SysUtil;
import uz.greenwhite.lib.view_setup.UI;
import uz.greenwhite.smartup5_trade.datasource.DS;

public class BarcodeUtil {

    public static final int BARCODE_REQUEST = 3;

    public static Intent barcodeIntent(Context ctx) {
        Intent intent = new Intent(ctx, CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");
        return intent;
    }

    public static void showBarcodeDialog(final Fragment fragment) {
        //TODO
        if (true) {
            if (SysUtil.checkSelfPermissionGranted(fragment.getActivity(), Manifest.permission.CAMERA)) {
                fragment.startActivityForResult(barcodeIntent(fragment.getActivity()), BARCODE_REQUEST);
            } else {
                fragment.requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
            return;
        }
        UI.bottomSheet()
                .title(R.string.barcode)
                .option(R.string.scan, new Command() {
                    @Override
                    public void apply() {
                        fragment.startActivityForResult(barcodeIntent(fragment.getActivity()), BARCODE_REQUEST);
                    }
                })
                .option(R.string.input, new Command() {
                    @Override
                    public void apply() {
                        //TODO OutletBarcodeDialog.show(fragment.getActivity());
                        Toast.makeText(fragment.getContext(),
                                R.string.during_the_development_phase, Toast.LENGTH_SHORT).show();
                    }
                })
                .show(fragment.getActivity());
    }

    public static String getBarcodeInActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == BARCODE_REQUEST) {
            final String barcode = data.getStringExtra("SCAN_RESULT");
            if (TextUtils.isEmpty(barcode)) {
                UI.alertError(activity, DS.getString(R.string.generating_an_error_try_again));
                return null;
            }
            return barcode;
        }
        return null;
    }
}
