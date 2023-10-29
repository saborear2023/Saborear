package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Saborear.com.br.Adapter.ComentarioView;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.Utils;

public class Comentario extends AppCompatActivity {

    private static Activity context;
    private static Database db;

    private ImageButton voltar;
    private ImageButton home;
    private ImageButton pesquisar;
    private ImageButton perfil;
    private ImageButton notificacao;
    private ImageButton back;
    private static ImageView sem;
    private static ImageButton enviar;
    private static ListView listview;
    private static ProgressBar loading;
    private static EditText insert;
    private TextView size;
    private String comentario;
    public static boolean editando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.comentario);
        initialize();

        Utils.setupChat(this, findViewById(R.id.comentario_layout));

        enviar.setOnClickListener(v -> {
            block(true);
            if(insert.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(), "Preencha o campo", Toast.LENGTH_SHORT).show();
                block(false);
                return;
            }
            Database db = new Database(this);
            String sql = "insert into comentario(id_receita, email, descricao) values('@receita', '@email', '@desc'); select *, (select nome from receita where id_receita = '@receita') as nome_da_receita from usuario where email = '@email';";
            sql = InternalDatabase.convert(sql);
            sql = sql.replace("@receita", Receita.receita.getIdreceita());
            sql = sql.replace("@desc", insert.getText().toString());
            db.requestScript(sql, data -> {
                int id = new Random().nextInt(99901)+500;
                String cmd = "insert into notificacao(id_notificacao, id_receita, titulo, mensagem) values('@id', '@receita', '@titulo', '@mensagem'); select * from receita where id_receita='@receita';";
                cmd = cmd.replace("@id", ""+id);
                cmd = cmd.replace("@receita", Receita.receita.getIdreceita());
                cmd = cmd.replace("@titulo", "Um novo comentário!");

                String msg = "@nome comentou em sua receita @receitanome";
                msg = msg.replace("@nome", data.get(0).get("nome"));
                msg = msg.replace("@receitanome", data.get(0).get("nome_da_receita"));

                cmd = cmd.replace("@mensagem", msg);
                db.requestScript(cmd, onFinish -> {
                    if(!onFinish.get(0).get("email").equals(InternalDatabase.convert("@email")))
                        for (Map<String, String> ignored : onFinish) {
                            String command = "insert into usuario_notificacao(id_notificacao, email) values('@id', '@email')";
                            command = command.replace("@email", onFinish.get(0).get("email"));
                            command = command.replace("@id", ""+id);
                            db.sendScript(command);
                        }
                });
                Manage.findReceita(Receita.receita).setComentarios(Receita.receita.getComentarios()+1);
                insert.setText("");
                size.setText("0/200)");
                atualizar();
            });
        });

        insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String texto = charSequence.toString();
                if(texto.length() > 0);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String txt = editable.toString();
                size.setText("("+txt.length()+"/200)");

                if(txt.length() <= 200 && insert.getText().toString().split("\\r?\\n").length <= 10)
                    comentario = txt;
                else if(insert.getText().toString().split("\\r?\\n").length > 10){
                    insert.setText(comentario);
                    Toast.makeText(getApplicationContext(), "Limite de 10 linhas atingido", Toast.LENGTH_SHORT).show();
                } else {
                    insert.setText(comentario);
                    Toast.makeText(getApplicationContext(), "Limite de caracteres atingido", Toast.LENGTH_SHORT).show();
                }

            }
        });

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> finish());

        pesquisar.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Pesquisar.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            finish();
        });

        perfil.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Perfil.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            finish();
        });

        home.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            finish();
        });

        notificacao.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Notificacao.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });
    }

    public static void block(Boolean block) {
        View[] views = new View[]{insert, enviar};
        for (View view : views) {
            view.setEnabled(!block);
        }
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
        listview.setVisibility(block ? View.INVISIBLE : View.VISIBLE);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        context = this;
        db = new Database(this);

        home = findViewById(R.id.comentario_btn_home);
        pesquisar = findViewById(R.id.comentario_btn_pesquisar);
        perfil = findViewById(R.id.comentario_btn_perfil);
        notificacao = findViewById(R.id.comentario_btn_notificacao);
        voltar = findViewById(R.id.comentario_btn_voltar);
        listview = findViewById(R.id.receita_listview_nutricional);
        loading = findViewById(R.id.comentario_carregar);
        insert = findViewById(R.id.comentario_insert);
        size = findViewById(R.id.comentario_size);
        enviar = findViewById(R.id.comentario_btn_enviar);
        back = findViewById(R.id.comentario_btn_back);
        sem = findViewById(R.id.comentario_sem);
        atualizar();

        editando = false;

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(listview);
        Utils.blackMode(findViewById(R.id.comentario_layout), ignore, inverted);

        if(InternalDatabase.isDarkmode())
            insert.setHintTextColor(Color.parseColor("#FFFFFF"));
    }

    public static void atualizar() {
        block(true);
        editando = false;
        String sql = "select c.*, u.nome, u.imagem from comentario as c join usuario as u on c.email = u.email where c.id_receita = '@id' order by id_avaliacao;".replace("@id", Receita.receita.getIdreceita());
        db.requestScript(sql, data -> {
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> e = new ArrayList<>();
            HashMap<String, String> nome = new HashMap<>();
            HashMap<String, String> mensagem = new HashMap<>();
            HashMap<String, String> horario = new HashMap<>();
            HashMap<String, String> email = new HashMap<>();
            HashMap<String, Bitmap> imagens = new HashMap<>();
            for (Map<String, String> i : data) {
                id.add(i.get("id_avaliacao"));
                e.add(i.get("email"));
                nome.put(i.get("id_avaliacao"), i.get("nome"));
                mensagem.put(i.get("id_avaliacao"), i.get("descricao"));
                horario.put(i.get("id_avaliacao"), i.get("horario"));
                email.put(i.get("id_avaliacao"), i.get("email"));
                imagens.put(i.get("id_avaliacao"), ImageDatabase.decode(i.get("imagem")));
            }

            if(id.size() == 0) sem.setVisibility(View.VISIBLE);
            else sem.setVisibility(View.INVISIBLE);

            ComentarioView adapterView = new ComentarioView(context, id, email, nome, mensagem, horario, imagens);
            listview.setAdapter(adapterView);
            block(false);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTracker.delActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}