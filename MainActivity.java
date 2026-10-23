package com.example.vivitekremote;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ConsumerIrManager irManager;
    private static final int FREQUENCY = 38000; // 38kHz carrier frequency

    @Override
    protected void Bundle) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Infrared Manager
        irManager = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);

        if (irManager == null || !irManager.hasIrEmitter()) {
            Toast.makeText(this, "আপনার ফোনে বিল্ট-ইন IR Blaster পাওয়া যায়নি!", Toast.LENGTH_LONG).show();
        }

        // Mapping buttons and Hex values
        setupIrButton(R.id.btn_power, "1FE807F");
        setupIrButton(R.id.btn_input, "1FE08F7");
        setupIrButton(R.id.btn_up, "1FEC03F");
        setupIrButton(R.id.btn_down, "1FE906F");
        setupIrButton(R.id.btn_left, "1FEE01F");
        setupIrButton(R.id.btn_right, "1FE708F");
        setupIrButton(R.id.btn_ok, "1FE30CF");
        setupIrButton(R.id.btn_vol_up, "1FE8877");
        setupIrButton(R.id.btn_vol_down, "1FE3AC5");
        setupIrButton(R.id.btn_menu, "1FEF00F");
        setupIrButton(R.id.btn_exit, "1FE58A7");
    }

    private void setupIrButton(int buttonId, final String hexCode) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transmitHexCode(hexCode);
                }
            });
        }
    }

    private void transmitHexCode(String hex) {
        if (irManager != null && irManager.hasIrEmitter()) {
            // Converts the Hex string back to native Android microsecond timings
            int[] pattern = hexToLayoutPattern(hex);
            irManager.transmit(FREQUENCY, pattern);
        }
    }

    private int[] hexToLayoutPattern(String hex) {
        // Generates compliant NEC protocol timings based on your specific hex values
        long raw = Long.parseLong(hex, 16);
        int[] pattern = new int[67];
        pattern[0] = 9000; // Leading pulse
        pattern[1] = 4500; // Space
        
        int idx = 2;
        for (int i = 31; i >= 0; i--) {
            pattern[idx++] = 560; // Burst
            if (((raw >> i) & 1) == 1) {
                pattern[idx++] = 1690; // Logic 1
            } else {
                pattern[idx++] = 560;  // Logic 0
            }
        }
        pattern[66] = 560; // Stop bit
        return pattern;
    }
}
