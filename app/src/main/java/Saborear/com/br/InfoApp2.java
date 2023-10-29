package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class InfoApp2 extends AppCompatActivity {

    private Button prox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.info_app2);
        initialize();

        prox.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), InfoApp3.class);
            startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    public void initialize() {
        ActivityTracker.addActivity(this);

        prox = findViewById(R.id.appinfo2_btn_prox);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTracker.delActivity(this);
    }
}