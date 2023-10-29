package Saborear.com.br;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Saborear.com.br.Classes.classePergunta;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Classes.classeTempero;
import Saborear.com.br.Classes.classeUtilizacao;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.FireDatabase;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.MarketDatabase;
import Saborear.com.br.Database.NutricionalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Manage.ManageDatabase;
import Saborear.com.br.Utils.Timer;
import Saborear.com.br.Utils.Utils;

public class Loading extends AppCompatActivity {

    private Context context;
    private Database db;
    private boolean autenticado = false, dbload = false, dbtempero = false;
    private int process = 0, pos = 0, perm = 0;
    public static int steps = 0, end = 0;

    public ImageView loading;

    public String sql;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.loading);

        context = this;
        db = new Database(this);
        ManageDatabase.start(this);
        InternalDatabase.start(this);
        ImageDatabase.start(this);
        FireDatabase.start();

        StorageDatabase.scale = getApplicationContext().getResources().getDisplayMetrics().density;

        Timer timer = new Timer();

        String email = InternalDatabase.getEmail();
        String senha = InternalDatabase.getSenha();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference updateref = database.getReference().child("update");
        updateref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!dbload) {
                    dbload = true;
                    return;
                }
                String[] splits = snapshot.getValue(String.class).replace("\"", "").split("//");

                String id = splits[0];
                String acao = splits[1];

                db.requestScript(StorageDatabase.getScriptAll()+"where id_receita='@id';".replace("@id", id), data -> {
                    try {
                        classeReceita find = Manage.findReceita(id);
                        classeReceita receita;

                        Log.i("SaborearFirebase", "Firebase: " + acao + ":"+ id);
                        switch(acao) {
                            case "atualizar":
                                receita = new classeReceita(data.get(0));
                                if(find != null) find.update(receita);
                                break;
                            case "deletar":
                                if(find != null) Manage.deletarReceita(id);
                                break;
                            case "criar":
                                receita = new classeReceita(data.get(0));
                                if(find == null) StorageDatabase.receitas.add(receita);
                                break;
                        }
                    } catch (Exception e) {
                        Log.e("SaborearFirebase", "Erro ao atualizar receita: "+e.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        DatabaseReference updatetemperoref = database.getReference().child("update-temperos");
        updatetemperoref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!dbtempero) {
                    dbtempero = true;
                    return;
                }

                String[] splits = snapshot.getValue(String.class).replace("\"", "").split("//");

                String id = splits[0];
                String acao = splits[1];

                db.requestScript("select t.id_tempero, t.nome, t.descricao, string_agg(concat(u.id_utilizacao, '/', u.nome), ',,') as utilizacoes from tempero t join tempero_utilizacao tu on t.id_tempero = tu.id_tempero join utilizacao u on tu.id_utilizacao = u.id_utilizacao where t.id_tempero='@id' group by t.id_tempero;".replace("@id", id), data -> {
                    try {
                        classeTempero find = Manage.findTempero(id);
                        classeTempero tempero;

                        Log.i("SaborearFirebase", "Firebase: " + acao + ":"+ id);
                        switch(acao) {
                            case "atualizar":
                                tempero = new classeTempero(getApplicationContext(), data.get(0));
                                if(find != null) find.update(tempero);
                                break;
                            case "deletar":
                                if(find != null) Manage.deletarTempero(id);
                                break;
                            case "criar":
                                tempero = new classeTempero(getApplicationContext(), data.get(0));
                                if(find == null) StorageDatabase.temperos.add(tempero);
                                break;
                        }
                    } catch (Exception e) {
                        Log.e("SaborearFirebase", "Erro ao atualizar tempero: "+e.getMessage());
                    }

                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if (email == null || senha == null) { // Checar se o usuário já logou anteriormente
            Log.i("SaborearDatabase", "Usuário não autenticado: sem registro " + timer.stop());
            steps++; // 1- Login
        } else {
            sql = "select * from usuario where email = '" + email + "' and senha = '" + senha + "';";
            db.requestScript(sql, data -> {
                if (data.size() == 0)  // Senha do usuário inválida
                    Log.i("SaborearDatabase", "Usuário não autenticado: senha inválida " + timer.stop());
                else {
                    Map<String, String> user = data.get(0);
                    ImageDatabase.setPerfil(ImageDatabase.decode(user.get("imagem")));
                    if (Objects.requireNonNull(user.get("admin")).equals("t"))
                        StorageDatabase.isAdmin = true; // Checar se é administrador
                    autenticado = true;
                }
                steps++; // 1-Login
                Log.i("SaborearDatabase", "Usuário autenticado " + timer.stop());
            });
        }

        db.requestScript("select * from utilizacao", data -> {
            Map<String, String> util = new HashMap<>();
            for (Map<String, String> i : data)  util.put(i.get("nome"), i.get("id_utilizacao"));
            StorageDatabase.util = util;
            Log.i("SaborearDatabase", "Utilizações carregadas " + timer.stop());
            steps++;
        });

        db.requestScript("select t.id_tempero, t.nome, t.descricao, string_agg(concat(u.id_utilizacao, '/', u.nome), ',,') as utilizacoes from tempero t join tempero_utilizacao tu on t.id_tempero = tu.id_tempero join utilizacao u on tu.id_utilizacao = u.id_utilizacao group by t.id_tempero;", finish -> {
            int[] ids = new int[]{R.drawable.saborear_utilizacao_00, R.drawable.saborear_utilizacao_01, R.drawable.saborear_utilizacao_02, R.drawable.saborear_utilizacao_03, R.drawable.saborear_utilizacao_04, R.drawable.saborear_utilizacao_05, R.drawable.saborear_utilizacao_06, R.drawable.saborear_utilizacao_07, R.drawable.saborear_utilizacao_08, R.drawable.saborear_utilizacao_09};

            StorageDatabase.temperos = new ArrayList<>();
            for (Map<String, String> i : finish) {
                ArrayList<classeUtilizacao> utilizacoes = new ArrayList<>();
                try {
                    ArrayList<String> ar = new ArrayList<>(Arrays.asList(i.get("utilizacoes").split(",,")));
                    for (String s : ar) {
                        String[] split = s.split("/");
                        utilizacoes.add(new classeUtilizacao(split[0], split[1], Utils.drawableToBitmap(this, ids[Integer.parseInt(split[0])])));
                    }

                    StorageDatabase.temperos.add(new classeTempero(i.get("id_tempero"), i.get("nome"), i.get("descricao"), null, utilizacoes));
                } catch (Exception e) {
                    Log.e("SaborearDatabase", "ERRO #171:"+e.getMessage());
                }
            }

            Comparator<classeTempero> nomeComparator = Comparator.comparing(classeTempero::getNome);
            Collections.sort(StorageDatabase.temperos, nomeComparator);

            Log.i("SaborearDatabase", "Temperos carregados " + timer.stop());
            steps++;
        });

        db.requestScript("select * from ingrediente;", data -> {
            StorageDatabase.nomes = new ArrayList<>();
            StorageDatabase.idingrediente = new HashMap<>();

            for (Map<String, String> i : data) {
                StorageDatabase.nomes.add(i.get("nome"));
                StorageDatabase.idingrediente.put(i.get("nome"), i.get("id_ingrediente"));
            }
            Collections.sort(StorageDatabase.nomes);

            StorageDatabase.categ = new HashMap<>();
            db.requestScript("select * from categ_receita;", onFinish -> {
                StorageDatabase.nomescategoria = new ArrayList<>();
                for (Map<String, String> i : onFinish) {
                    StorageDatabase.nomescategoria.add(i.get("nome"));
                    StorageDatabase.categ.put(i.get("nome"), i.get("id_cat_receita"));
                }

                Collections.sort(StorageDatabase.nomescategoria);
                StorageDatabase.nomescategoria.add(0, "Categoria");
                steps++;
            });
            db.requestScript("select c.nome as categoria, i.* from ingrediente as i join categ_ingrediente as c on i.id_cat_ingrediente = c.id_cat_ingrediente;", onFinish -> {
                StorageDatabase.nomesingred = new HashMap<>();
                StorageDatabase.nomescategingred = new ArrayList<>();
                for (Map<String, String> i : onFinish) {
                    StorageDatabase.nomesingred.put(i.get("nome"), i.get("categoria"));
                    if(!StorageDatabase.nomescategingred.contains(i.get("categoria"))) StorageDatabase.nomescategingred.add(i.get("categoria"));
                }


                MarketDatabase marketTask = new MarketDatabase(() -> steps++);
                marketTask.execute("http://200.145.153.91/carlospinto/PHP/TCC/market.html");

                NutricionalDatabase nutricionalTask = new NutricionalDatabase(() -> steps++);
                nutricionalTask.execute("http://200.145.153.91/carlospinto/PHP/TCC/nutricional.html");
                steps++;
            });
        });

        db.requestScript("select count(*) as tamanho from receita;", finish -> {
            ArrayList<classeReceita> receitas = new ArrayList<>();

            int size = Integer.parseInt(finish.get(0).get("tamanho")) / 10 + 1;
            for (int i = 0; i < size; i++) {
                db.requestScript(StorageDatabase.getScript() + "limit 10 offset " + (10 * i), data -> {
                    for (Map<String, String> j : data)
                        receitas.add(new classeReceita(j));

                    if (++end == size) {
                        StorageDatabase.receitas = receitas;
                        Log.i("SaborearDatabase", "Receitas carregadas " + timer.stop());
                        steps++;

//                        db.requestScript("select id_receita, imagem from receita;", end -> {
//                            for (Map<String, String> j : end)  {
//                                classeReceita receita = Manage.findReceita(j.get("id_receita").toString());
//                                if(receita != null) receita.setImagem(ImageDatabase.decode(j.get("imagem")));
//                            }
//                            Log.i("SaborearDatabase", "Todas as imagens das receitas carregadas " + timer.stop());
//                        });
                    }
                });
            }
        });

        db.requestScript("SELECT p.*, (SELECT STRING_AGG(pa.id_pergunta || ',,' || pa.alternativa || ',,' || pa.id_acao || ',,' || pa.prioridade, '//') FROM pergunta_alternativa pa WHERE pa.id_pergunta = p.id_pergunta) AS alternativas FROM pergunta p;", data -> {
            ArrayList<classePergunta> perguntas = new ArrayList<>();
            ArrayList<classePergunta> perguntasdefinicao = new ArrayList<>();

            classePergunta pergunta;
            for (Map<String, String> i : data) {
                pergunta = new classePergunta(i);
                perguntas.add(pergunta);

                if(!pergunta.getAcao().contains("Subcategoria")) continue;

                pergunta = new classePergunta(i);
                pergunta.setPergunta(i.get("definicao"));
                pergunta.setMultiplos(i.get("multiplos_definicao").equals("t") ? true : false);
                perguntasdefinicao.add(pergunta);
            }

            StorageDatabase.perguntas = perguntas;
            StorageDatabase.perguntasdefinicao = perguntasdefinicao;
            Log.i("SaborearDatabase", "Perguntas carregadas " + timer.stop());
            steps++;
        });

        loading = findViewById(R.id.loading_icon);
        loading.setTranslationY(250f);

        loading.animate().translationY(-50f).setDuration(1000).start();

        requestPermissions();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (steps < (StorageDatabase.permissoes.length + 9) || process < 3) {
                    switch (pos) {
                        case 0:
                            loading.setImageResource(R.drawable.saborear_icon_melancia_01);
                            break;
                        case 1:
                            loading.setImageResource(R.drawable.saborear_icon_melancia_02);
                            break;
                        case 2:
                            loading.setImageResource(R.drawable.saborear_icon_melancia_03);
                            break;
                        case 3:
                            loading.setImageResource(R.drawable.saborear_icon_melancia_04);
                            break;
                        case 4:
                            loading.setImageResource(R.drawable.saborear_icon_melancia_05);
                            break;
                        case 5:
                            loading.setImageDrawable(null);
                            break;
                    }
                    loading.invalidate();
                    pos += 1;
                    process += 1;

                    if (pos % 6 == 0)
                        pos = 0;

                    handler.postDelayed(this, 500);
                } else {
                    Log.i("SaborearDatabase", "Carregamento finalizado " + timer.stop());
                    sql = "select * from apk";
                    db.requestScript(sql, i -> {
                        Map<String, String> data = i.get(0);
                        if (!data.get("versao").toString().equals(StorageDatabase.versao)) { // Checar se a versão do usuário está ativa
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Há uma nova versão disponível")
                                    .setTitle("Saborear")
                                    .setNegativeButton("Sair", (dialog, id) -> { finishAffinity(); })
                                    .setPositiveButton("Atualizar", (dialog, id) -> {
                                        String url = data.get("download");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Adicione esta linha para abrir em uma nova tarefa
                                        intent.setPackage("com.android.chrome"); // Opcional: especifica o navegador a ser usado (neste exemplo, Chrome)
                                        try {
                                            startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(getApplicationContext(), "Nenhum navegador disponível", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            builder.create().show();
                        } else {
                            Runnable changeScreen = () -> {
                                Intent myIntent;
                                if (InternalDatabase.firstTime) {
                                    myIntent = new Intent(getApplicationContext(), InfoApp1.class);
                                } else {
                                    myIntent = new Intent(getApplicationContext(), autenticado ? Main.class : Acessar.class);
                                }
                                startActivity(myIntent);
                                finish();
                            };
                            handler.postDelayed(changeScreen, 1000);
                            loading.animate().translationY(250f).setDuration(1000).start();
                        }
                    });


                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, StorageDatabase.permissoes[perm]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{StorageDatabase.permissoes[perm]}, 1);
        } else {
            Log.i("SaborearDatabase", "Permissão concedida [" + (perm + 1) + "/" + StorageDatabase.permissoes.length + "]");
            steps++; // 1-Permissão (ok)
            perm++;
            if (perm < StorageDatabase.permissoes.length)
                requestPermissions();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                steps++; // Permissão (ok)
                perm++;
                if(perm < StorageDatabase.permissoes.length)
                    requestPermissions();
            } else {
                Toast.makeText(this, "Você recusou as permissões", Toast.LENGTH_SHORT).show();
            }
        }
    }
}