package Saborear.com.br;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Random;

import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Utils.Utils;

public class Redefinir extends AppCompatActivity {

    private Database db;

    private String email, code;
    private EditText numero, codigo, senha;
    private ImageButton salvar, solicitar;
    private Button tologin, tocadastro;
    private ProgressBar carregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redefinir);
        initialize();

        solicitar.setOnClickListener(v -> {
            String telefone = numero.getText().toString();
            block(true);

            if(telefone.length() == 0) {
                Toast.makeText(getApplicationContext(), "Preencha o campo telefone corretamente", Toast.LENGTH_SHORT).show();
                block(false);
                return;
            } else if(!Utils.isNumber(telefone)) {
                block(false);
                numero.setError("Digite um número com ddd válido!");
                return;
            }

            String sql = "select * from usuario where telefone='@num'".replace("@num", ""+telefone);
            db.requestScript(sql, data -> {
                for (Map<String, String> i : data) {
                    email = i.get("email");

                    Random random = new Random();
                    code = String.valueOf(random.nextInt(900000) + 100000);

                    String s = "insert into redefinicao(telefone, codigo) values('@telefone', '@cod');";
                    s = s.replace("@telefone", telefone);
                    s = s.replace("@cod", code);
                    Log.i("SaborearDatabase", s);
                    db.requestScript(s, d -> {
                        Toast.makeText(getApplicationContext(), "Código enviado", Toast.LENGTH_SHORT).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(telefone, null, "Saborear: seu código de redefinição é "+code, null, null);
                        block(false);
                    });
                }
            });
        });

        salvar.setOnClickListener(v -> {
            block(true);

            String c = codigo.getText().toString();
            String s = senha.getText().toString();
            String n = numero.getText().toString();

            if(code == null) {
                Toast.makeText(getApplicationContext(), "Solicite um código", Toast.LENGTH_SHORT).show();
                block(false);
                return;
            }

            if(c.length() == 0 || s.length() == 0 || n.length() == 0) {
                Toast.makeText(getApplicationContext(), "Preencha os campos", Toast.LENGTH_SHORT).show();
                block(false);
                return;
            }

            if(s.length() > 30) {
                senha.setError("Máximo de 30 caracteres");
                block(false);
                return;
            }

            String sql = "select * from redefinicao where telefone='@telefone' and codigo='@cod';".replace("@cod", c);
            sql = sql.replace("@telefone", n);
            db.requestScript(sql, data -> {
                if(data.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Código inválido", Toast.LENGTH_SHORT).show();
                    block(false);
                    return;
                } else {
                    String newsql = "delete from redefinicao where telefone='@telefone';update usuario set senha='@senha' where telefone='@telefone';".replace("@telefone", n);
                    newsql = newsql.replace("@senha", s);
                    Log.i("SaborearDatabase", newsql);
                    db.requestScript(newsql, d -> {
                        Toast.makeText(getApplicationContext(), "Senha alterada", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(getApplicationContext(), Login.class);
                        startActivity(myIntent);
                        finish();
                    });
                }
            });

        });

        tologin.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Login.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        tocadastro.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Cadastro.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

    }

    void block(Boolean block) {
        View[] views = new View[]{numero, codigo, senha, tologin, tocadastro, salvar, solicitar};
        for (View view : views) {
            view.setEnabled(!block);

        }
        carregar.setIndeterminate(block);
        carregar.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }


    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);

        numero = findViewById(R.id.redefinir_insert_telefone);
        codigo = findViewById(R.id.redefinir_insert_codigo);
        senha = findViewById(R.id.redefinir_insert_senha);
        tologin = findViewById(R.id.redefinir_btn_entrar);
        tocadastro = findViewById(R.id.redefinir_btn_cadastro);
        salvar = findViewById(R.id.redefinir_btn_salvar);
        solicitar = findViewById(R.id.redefinir_btn_solicitar);
        carregar = findViewById(R.id.redefinir_carregar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTracker.delActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}