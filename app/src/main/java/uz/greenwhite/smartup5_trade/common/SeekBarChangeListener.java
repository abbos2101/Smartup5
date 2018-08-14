package uz.greenwhite.smartup5_trade.common;

import android.widget.SeekBar;


public abstract class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    public abstract void onProgressChanged(SeekBar seekBar, int progress);

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        onProgressChanged(seekBar, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}