package com.vanta.instascroll;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private TextView speedValue;
    private TextView lenValue;
    private Switch masterSwitch;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        speedValue = findViewById(R.id.speedValue);
        lenValue = findViewById(R.id.lenValue);
        masterSwitch = findViewById(R.id.masterSwitch);
        SeekBar speedSeek = findViewById(R.id.speedSeek);
        SeekBar lenSeek = findViewById(R.id.lenSeek);
        RadioButton dirOlder = findViewById(R.id.dirOlder);
        RadioButton dirNewer = findViewById(R.id.dirNewer);

        int delay = Prefs.getDelay(this);
        int length = Prefs.getLengthPct(this);
        int dir = Prefs.getDirection(this);

        speedSeek.setProgress(Math.max(0, delay - 5));
        speedValue.setText(delay + " ms");

        lenSeek.setProgress(length);
        lenValue.setText(length + "%");

        if (dir == 0) dirOlder.setChecked(true);
        else dirNewer.setChecked(true);

        masterSwitch.setChecked(Prefs.isEnabled(this));
        masterSwitch.setOnCheckedChangeListener((v, c) -> Prefs.setEnabled(MainActivity.this, c));

        speedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) {
                int v = p + 5;
                speedValue.setText(v + " ms");
                Prefs.setDelay(MainActivity.this, v);
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });

        lenSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) {
                lenValue.setText(p + "%");
                Prefs.setLengthPct(MainActivity.this, p);
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });

        dirOlder.setOnCheckedChangeListener((v, c) -> {
            if (c) Prefs.setDirection(MainActivity.this, 0);
        });
        dirNewer.setOnCheckedChangeListener((v, c) -> {
            if (c) Prefs.setDirection(MainActivity.this, 1);
        });

        findViewById(R.id.enableButton).setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isServiceOn()) {
            statusText.setText("Service Online - Scroll is ready");
            statusText.setTextColor(0xFF22C55E);
        } else {
            statusText.setText("Service Offline - Activate once below");
            statusText.setTextColor(0xFFFF3B6B);
        }
    }

    private boolean isServiceOn() {
        String enabled = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabled != null && enabled.contains(
                getPackageName() + "/" + ScrollAccessibilityService.class.getName());
    }
}
