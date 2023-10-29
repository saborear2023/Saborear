package Saborear.com.br;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;


public class Cadastro extends AppCompatActivity {

    private Database db;
    private EditText nome, telefone, email, senha;
    private ImageButton cadastrar;
    private Button tologin;
    private ProgressBar loading;

    private String sql;

    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);
        initialize();

        cadastrar.setOnClickListener(v -> {
            block(true);
            EditText[] info = new EditText[]{ nome, telefone, email, senha};
            String[] text = new String[4];

            for(int i = 0; i<4; i++) {
                if(info[i].length() > 0) {
                    text[i] = info[i].getText().toString();
                    if(text[i].length() > 30 && info[i] != telefone) {
                        info[i].setError("Máximo de 30 caracteres");
                        block(false);
                        return;
                    }
                    continue;
                }
                block(false);
                Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!Utils.isNumber(text[1])) {
                block(false);
                telefone.setError("Digite um número com ddd válido!");
                return;
            }

            if(!Utils.isEmail(text[2])) {
                block(false);
                email.setError("Digite um email válido!");
                return;
            }

            sql = "select (case when exists (select 1 from usuario where email = '@email') then 'true' else 'false' end) as email, (case when exists (select 1 from usuario where telefone = '@telefone') then 'true' else 'false' end) as telefone;".replace("@email", text[2]);
            sql = sql.replace("@telefone", text[1]);
            db.requestScript(sql, data -> {
                for (Map<String, String> i : data) {
                    if(i.get("email").equals("true")) {
                        block(false);
                        Toast.makeText(getApplicationContext(), "Email já cadastrado", Toast.LENGTH_SHORT).show();
                        return;
                    } else if(i.get("telefone").equals("true")) {
                        block(false);
                        Toast.makeText(getApplicationContext(), "Telefone já cadastrado", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                sql = "insert into usuario(nome, telefone, email, senha) values ('@nome', @telefone, '@email', '@senha');";
                sql = sql.replace("@nome", text[0]);
                sql = sql.replace("@telefone", text[1]);
                sql = sql.replace("@email", text[2]);
                sql = sql.replace("@senha", text[3]);

                db.requestScript(sql, value -> {
                    InternalDatabase.saveUser(getApplicationContext(), text[0], text[1], text[2], text[3]);
                    Toast.makeText(getApplicationContext(), "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(getApplicationContext(), Main.class);
                    startActivity(myIntent);
                    this.finish();
                });
            });
        });

        tologin.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Saborear.com.br.Login.class);
            startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            this.finish();
        });
    }

    private void block(boolean block) {
        ViewGroup layout = findViewById(R.id.cadastro_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            view.setEnabled(!block);
        }
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);

        nome = findViewById(R.id.cadastro_insert_nome);
        telefone = findViewById(R.id.cadastro_insert_telefone);
        email = findViewById(R.id.cadastro_insert_email);
        senha = findViewById(R.id.cadastro_insert_senha);
        cadastrar = findViewById(R.id.cadastro_btn_criar);
        tologin = findViewById(R.id.cadastro_btn_entrar);
        loading = findViewById(R.id.cadastro_carregar);

        tologin.setPaintFlags(tologin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTracker.delActivity(this);
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
