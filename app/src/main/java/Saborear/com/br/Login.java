package Saborear.com.br;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Objects;

import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;

public class Login extends AppCompatActivity {

    private Database db;

    private EditText email, senha;
    private ImageButton entrar;
    private Button toCadastro, esqueci;
    private ProgressBar loading;

    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initialize();

        entrar.setOnClickListener(v -> {
            block(true);
            EditText[] info = new EditText[]{email, senha};
            String[] text = new String[2];
            String sql;

            for(int i = 0; i<2; i++) {
                if(info[i].length() > 0) {
                    text[i] = info[i].getText().toString();
                    continue;
                }
                block(false);
                Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            sql = "select * from usuario where email = '@email';".replace("@email", text[0]);
            db.requestScript(sql, data -> {
                if(data.size() == 0) {
                    block(false);
                    Toast.makeText(getApplicationContext(), "Usuário não cadastrado", Toast.LENGTH_SHORT).show();
                } else
                    for (Map<String, String> item : data) {
                        if(Objects.requireNonNull(item.get("admin")).equals("t")) StorageDatabase.isAdmin = true;
                        else StorageDatabase.isAdmin = false;

                        if(Objects.requireNonNull(item.get("senha")).equals(text[1])) {
                            ImageDatabase.setPerfil(ImageDatabase.decode(item.get("imagem")));
                            InternalDatabase.saveUser(getApplicationContext(), item.get("nome"), item.get("telefone"), text[0], text[1]);
                            Toast.makeText(getApplicationContext(), "Autenticado", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(getApplicationContext(), Main.class);
                            startActivity(myIntent);
                            finish();
                        } else {
                            block(false);
                            Toast.makeText(getApplicationContext(), "Senha inválida", Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        });

        toCadastro.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Saborear.com.br.Cadastro.class);
            startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        esqueci.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Saborear.com.br.Redefinir.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });
    }

    void block(Boolean block) {
        View[] views = new View[]{email, senha, entrar, toCadastro, esqueci};
        for (View view : views) {
            view.setEnabled(!block);

        }
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    void initialize() {
        ActivityTracker.addActivity(this);

        db = new Database(this);
        email = findViewById(R.id.login_insert_email);
        senha = findViewById(R.id.login_insert_senha);
        entrar = findViewById(R.id.login_btn_entrar);
        toCadastro = findViewById(R.id.login_btn_cadastro);
        esqueci = findViewById(R.id.login_btn_esqueci);
        loading = findViewById(R.id.login_carregar);

        toCadastro.setPaintFlags(toCadastro.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        esqueci.setPaintFlags(esqueci.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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
