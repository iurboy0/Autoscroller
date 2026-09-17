package com.vanta.instascroll;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private TextView speedValue;
    private TextView lenValue;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        speedValue = findViewById(R.id.speedValue);
        lenValue = findViewById(R.id.lenValue);
        SeekBar speedSeek = findViewById(R.id.speedSeek);
        SeekBar lenSeek = findViewById(R.id.lenSeek);
        RadioButton dirOlder = findViewById(R.id.dirOlder);
        RadioButton dirNewer = findViewById(R.id.dirNewer);

        int delay = Prefs.getDelay(this);
        int length = Prefs.getLengthPct(this);
        int dir = Prefs.getDirection(this);

        speedSeek.setProgress(Math.max(0, delay - 50));
        speedValue.setText(delay + " ms");

        lenSeek.setProgress(length);
        lenValue.setText(length + "%");

        if (dir == 0) dirOlder.setChecked(true);
        else dirNewer.setChecked(true);

        speedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) {
                int v = p + 50;
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
            statusText.setText("●  Service Online");
            statusText.setTextColor(0xFF22C55E);
        } else {
            statusText.setText("●  Service Offline");
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
