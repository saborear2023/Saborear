package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Saborear.com.br.Adapter.AlternativaIngredientesView;
import Saborear.com.br.Adapter.PerguntaIngredientesView;
import Saborear.com.br.Classes.classeIngrediente;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Utils.Utils;

public class Chatingredientes extends AppCompatActivity {
    private Activity activity;
    private Database db;
    private ImageButton back, enviar, voltar, home, notificacao, pesquisar, perfil, visualizar;
    private float alternativax;
    private ViewPager alternativas;
    private Boolean digitando, unidade;

    public static classeReceita receita;
    public static ArrayList<classeIngrediente> ingredientes;
    public static String acao;
    public static ArrayList<String> perguntas, mensagem, acoes, nome, id_ingrediente, quantidade, medida;

    public static ArrayList<Boolean> admin;

    public static Map<Integer, Bitmap> imagem;
    public static Map<Integer, String> target;

    public static TextView insert;
    public static ListView chat;

    public static int finish;
    public static boolean blocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot);
        initialize();

        enviar.setOnClickListener(v -> {
            if(insert.length() == 0) return;

            AlternativaIngredientesView adapter = (AlternativaIngredientesView) alternativas.getAdapter();

            String aux = insert.getText().toString(), storage = adapter.getStorage().size() > 0 ? adapter.getStorage().get(0) : "";
            insert.setText("");

            addMensagem(false, aux);
            switch(storage) {
                case "del":
                    if(nome.size() > 0) {
                        id_ingrediente.remove(id_ingrediente.size() - 1);
                        medida.remove(medida.size() - 1);
                        quantidade.remove(quantidade.size() - 1);
                        nome.remove(nome.size() - 1);

                        CadastrarReceita.delIngrediente();

                        addMensagem(true, "Lista de ingredientes atual: " + getIngredientes());
                    } else
                        addMensagem(true, "Não há ingredientes para remover");
                    start();
                    break;
                case "salvar":
                    String fim = "delete from receita_ingred where id_receita='@id';".replace("@id", receita.getIdreceita());
                    for (int i = 0; i < nome.size(); i++) {
                        String cmd = "insert into receita_ingred(id_ingrediente, id_receita, quantidade) values('@id', '@receita', '@quantidade');";
                        cmd = cmd.replace("@id", ""+id_ingrediente.get(i));
                        cmd = cmd.replace("@receita", "" + receita.getIdreceita());
                        cmd = cmd.replace("@quantidade", quantidade.get(i));
                        fim += cmd;
                    }
                    Log.i("SaborearDatabase", fim);
                    db.requestScript(fim, onFinish -> {
                        block(true, false);
                        Handler handler = new Handler();
                        Runnable runnable = () -> {
                            Toast.makeText(getApplicationContext(), "Ingredientes atualizados!", Toast.LENGTH_SHORT).show();
                            activity.finish();
                        };
                        handler.postDelayed(runnable, 1500);
                    });
                    break;
                case "back":
                    block(true, false);
                    Handler handler = new Handler();
                    Runnable runnable = () -> activity.finish();
                    handler.postDelayed(runnable, 1500);
                    break;
                default:
                    ArrayList<String[]> arr = new ArrayList<>();
                    boolean erro = false;
                    for (String s : aux.split(",")) {
                        Pair<Boolean, String[]> result = extract(s);

                        if(!result.first) {
                            erro = true;
                            break;
                        }

                        arr.add(result.second);
                    }

                    if (!erro) {
                        if(arr.size() > 1) addMensagem(true, "Ok... vou encontrar os ingredientes mais semelhantes");
                        else addMensagem(true, "Ok... vou encontrar o ingrediente mais semelhante");
                        block(true, true);

                        finish = 0;
                        for (String[] strings : arr) {
                            db.requestScript("select *, similarity(nome::text, '@find'::text) as similaridade from ingrediente where similarity(nome::text, '@find'::text) > 0.1 order by similaridade desc limit 10;".replace("@find", strings[0]), onFinish -> {
                                if(onFinish.size() == 0) {
                                    addMensagem(true, "Não encontrei algum ingrediente com o nome '"+strings[0]+"'");

                                    if(++finish == arr.size()) {
                                        block(false, true);
                                        addMensagem(true, "Lista de ingredientes atual: " + getIngredientes());
                                    }
                                    return;
                                }

                                String n = onFinish.get(0).get("nome");
                                String id = onFinish.get(0).get("id_ingrediente");

                                nome.add(n);
                                id_ingrediente.add(id);
                                quantidade.add(strings[1] + " " + strings[2]);
                                medida.add(strings[2]);

                                classeIngrediente ingrediente = new classeIngrediente(id, null, n, strings[1] + " " + strings[2]);

                                CadastrarReceita.addIngrediente(ingrediente);
                                CadastrarReceita.atualizar();

                                if(++finish == arr.size()) {
                                    block(false, true);
                                    addMensagem(true, "Lista de ingredientes atual: " + getIngredientes());
                                }
                            });
                        }

                    } else {
                        addMensagem(true, "O ingrediente está fora do padrão");
                    }
            }
        });

        back.setOnClickListener(v -> this.finish());

        voltar.setOnClickListener(v -> this.finish());

        perfil.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Perfil.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        pesquisar.setOnClickListener(v -> {
            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Pesquisar.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
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

    private void block(boolean block, boolean digitando) {
        if(digitando) blocked = block;
        if(block) {
            alternativas.animate().xBy(getResources().getDisplayMetrics().widthPixels).setDuration(1500).start();
        } else {
            alternativas.setX(-500-100*alternativas.getChildCount());
            alternativas.animate().x(alternativax+Utils.intToDp(28)).setDuration(1500).start();
        }
    }

    public void initialize() {
        activity = this;
        db = new Database(this);
        alternativas = findViewById(R.id.chatbot_alternativas);
        enviar = findViewById(R.id.chatbot_btn_enviar);
        chat = findViewById(R.id.chatbot_chat);
        insert = findViewById(R.id.chatbot_insert);
        home = findViewById(R.id.chatbot_btn_home);
        notificacao = findViewById(R.id.chatbot_btn_notificacao);
        pesquisar = findViewById(R.id.chatbot_btn_pesquisar);
        perfil = findViewById(R.id.chatbot_btn_perfil);
        voltar = findViewById(R.id.chatbot_btn_voltar);
        visualizar = findViewById(R.id.chatbot_btn_visualizar);
        back = findViewById(R.id.chatbot_btn_back);

        admin = new ArrayList<>();
        mensagem = new ArrayList<>();
        perguntas = new ArrayList<>();
        nome = new ArrayList<>();
        id_ingrediente = new ArrayList<>();
        quantidade = new ArrayList<>();
        medida = new ArrayList<>();
        acoes = new ArrayList<>();
        imagem = new HashMap<>();
        target = new HashMap<>();

        insert.setEnabled(true);

        digitando = false;
        unidade = false;

        block(false, false);

        for (classeIngrediente ingrediente : ingredientes) {
            nome.add(ingrediente.getNome());
            id_ingrediente.add(ingrediente.getIndingrediente());
            quantidade.add(ingrediente.getQuantidadeString()+" "+ingrediente.getMedida());
            medida.add(ingrediente.getMedida());
        }

        start();
        addMensagem(true, "Lista de ingredientes atual: "+getIngredientes());
        addMensagem(true, "Digite no seguinte padrão para adicionar ingredientes: ");
        addMensagem(true, "(nome) (quantidade) (medida),\n(nome) (quantidade) (medida)");

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(insert); ignore.add(enviar); ignore.add(findViewById(R.id.chatbot_icon));
        inverted.add(chat);
        Utils.blackMode(findViewById(R.id.chatbot_layout), ignore, inverted);
    }

    public Pair<Boolean, String[]> extract(String entrada) {
        String[] arr = {"", "", ""};

        Pattern pattern = Pattern.compile("^(.*?)\\s+([0-9]+(?:\\.[0-9]+)?)\\s+(.*)$");
        Matcher matcher = pattern.matcher(entrada.replace(",", ".").replace("\n", ""));

        if (matcher.matches()) {
            try {
                arr[0] = matcher.group(1);
                arr[1] = matcher.group(2);
                arr[2] = matcher.group(3);
                new Pair<>(true, arr);
            } catch (Exception e) { return new Pair<>(false, arr); }
        } else return new Pair<>(false, arr);
        return new Pair<>(true, arr);
    }

    private void start() {
        ArrayList<String> nomes = new ArrayList<>();
        ArrayList<String> acoes = new ArrayList<>();

        nomes.add("Voltar"); acoes.add("back");
        nomes.add("Remover"); acoes.add("del");

        AlternativaIngredientesView adapterView = new AlternativaIngredientesView(Chatingredientes.this, nomes, acoes, true);
        alternativas.setAdapter(adapterView);
        alternativas.setOffscreenPageLimit(15);
    }

    private String getIngredientes() {
        String ingredientes = "\n";
        for (int i = 0; i < nome.size(); i++) {
            ingredientes += "\n("+quantidade.get(i)+") "+nome.get(i);
        }
        return ingredientes;
    }
    private void addMensagem(Boolean isadmin, String msg) {
        admin.add(isadmin); mensagem.add(msg);


        PerguntaIngredientesView adapterView = new PerguntaIngredientesView(Chatingredientes.this, admin, imagem, target, mensagem);
        chat.setAdapter(adapterView);
        chat.setSelection(mensagem.size()-1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}