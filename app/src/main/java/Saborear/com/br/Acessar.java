package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Saborear.com.br.Utils.Utils;

public class Acessar extends AppCompatActivity {
    private ImageButton tologin, tocadastro;
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.acessar);
        initialize();

        tologin.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Login.class);
            startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        tocadastro.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Cadastro.class);
            startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

    }

    public void initialize() {
        tologin = findViewById(R.id.acessar_btn_entrar);
        tocadastro = findViewById(R.id.acessar_btn_cadastrar);
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - time < 3000) {
            finishAffinity();
        } else {
            Toast.makeText(getApplicationContext(), "Clique novamente para sair", Toast.LENGTH_SHORT).show();
            time = System.currentTimeMillis();
        }
    }
}
