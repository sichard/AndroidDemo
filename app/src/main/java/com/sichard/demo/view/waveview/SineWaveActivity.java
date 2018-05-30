package com.sichard.demo.view.waveview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述:基于正弦波的波浪实现
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-30</b>
 */
public class SineWaveActivity extends BaseActivity {

    private SineWaveView mSineWaveView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sine_wave);
        mSineWaveView = findViewById(R.id.wave_view);
        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setMax(100);
        seekBar.setProgress(80);
        mSineWaveView.setProgress(80);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSineWaveView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
