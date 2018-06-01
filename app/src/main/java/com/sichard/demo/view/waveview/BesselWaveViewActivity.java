package com.sichard.demo.view.waveview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述:贝塞尔曲线方法实现的WaveView的界面
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-31</b>
 */
public class BesselWaveViewActivity extends BaseActivity {
    private BesselWaveView mSineWaveView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bessel_wave_view);
        SeekBar seekBar = findViewById(R.id.bessel_seekBar);
        seekBar.setMax(100);
        seekBar.setProgress(80);
        mSineWaveView = findViewById(R.id.bessel_wave_view);
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
}
