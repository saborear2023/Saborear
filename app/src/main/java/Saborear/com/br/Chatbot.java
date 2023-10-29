package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;

import Saborear.com.br.Adapter.AlternativaView;
import Saborear.com.br.Adapter.PerguntaView;
import Saborear.com.br.Callback.ChatCallback;
import Saborear.com.br.Classes.classeAlternativa;
import Saborear.com.br.Classes.classeMensagem;
import Saborear.com.br.Classes.classePergunta;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Manage.ManageReceita;
import Saborear.com.br.Utils.Utils;

public class Chatbot extends AppCompatActivity {

    public static ListView chat;

    public static TextView insert;

    public static classeReceita receita;

    private ManageReceita mg;
    private Activity activity;
    private Database db;
    private ImageButton back, enviar, voltar, home, notificacao, pesquisar, perfil, visualizar;
    private ViewPager listview;
    private int position;
    private String acompanhamento;
    private float x;

    public static ArrayList<classePergunta> perguntas;
    private ArrayList<classeMensagem> mensagens;

    private ArrayList<classeReceita> receitas;

    private ProgressBar loading;

    public static ArrayList<String> subcategorias;

    public static Boolean block = false, cadastrando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.chatbot);
        initialize();

        enviar.setOnClickListener(v -> {
            try {
                if (insert.length() == 0) return;


                String value = insert.getText().toString();
                insert.setText("");

                addMensagem(value, false);
                addMensagem("Digitando", true);
                block(true, () -> {
                    AlternativaView adapter = (AlternativaView) listview.getAdapter();
                    ArrayList<classeAlternativa> selecionados = adapter.getSelecionados();

                    if(cadastrando) {
                        for (classeAlternativa alternativa : selecionados) {
                            if(alternativa.getIdacao().equals("Voltar")) {
                                delMensagem();
                                addMensagem("Voltando!", true);

                                Handler handler = new Handler();
                                Runnable runnable = () -> activity.finish();
                                handler.postDelayed(runnable, 1500);
                            }
                            return;
                        }
                    }

                    delMensagem();

                    classePergunta pergunta = perguntas.get(position);

                    mg = new ManageReceita(receitas);

                    for (classeAlternativa alternativa : selecionados) {
                        if (alternativa.getIdacao().equals("-1")) continue;
                        notify("(#346) Filtrando por: "+pergunta.getAcao());
                        switch (pergunta.getAcao()) {
                            case "Subcategoria":
                                mg.getBySubcategoria(alternativa.getIdacao());
                                break;
                            case "nSubcategoria":
                                mg.getByExceptSubcategoria(alternativa.getIdacao());
                                break;
                            case "Tempo":
                                mg.getByTempo(0, Integer.parseInt(alternativa.getIdacao()));
                                break;
                            case "Acompanhamento":
                                acompanhamento = alternativa.getIdacao();
                                break;
                            case "Info-recomeçar":
                                if (alternativa.getIdacao().equals("1")) {
                                    start();
                                    addPerguntas();
                                    make();
                                } else {
                                    Handler nhandler = new Handler();
                                    Runnable nrunnable = () -> activity.finish();
                                    nhandler.postDelayed(nrunnable, 1500);
                                }
                                return;
                        }
                    }

                    ArrayList<classeReceita> receitas = mg.randomizar().getReceitas();
                    notify("(#024) Filtrando receitas: "+receitas.size()+" restante(s)");

                    if (receitas.size() == 0)
                        addMensagem("Não encontrei nenhuma receita com essas alternativas, vou desconsiderá-las", true);
                    else this.receitas = receitas;

                    if (receitas.size() == 1 || position+1 == perguntas.size()) end();
                    else make();
                });
            } catch (Exception e) {
                Log.e("SaborearDatabase", e.getMessage());
            }
        });

        back.setOnClickListener(v -> this.finish());

        visualizar.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Receita.class);
            myIntent.putExtra("visualizacao", true);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            Toast.makeText(getApplicationContext(), "Visualizando...", Toast.LENGTH_SHORT).show();
        });

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

    private void block(boolean block, ChatCallback callback) {
        int time;
        if(block) {
            time = 1000;
            listview.animate().xBy(getResources().getDisplayMetrics().widthPixels).setDuration(time).start();
        } else {
            time = 1500;
            listview.setX(-500-100*listview.getChildCount());
            listview.animate().x(x+Utils.intToDp(28)).setDuration(time).start();
        }

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> callback.onFinish();
        handler.postDelayed(runnable, time);
    }

    private void blockLoading(boolean block) {
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    public void initialize() {
        activity = this;
        db = new Database(this);
        listview = findViewById(R.id.chatbot_alternativas);
        enviar = findViewById(R.id.chatbot_btn_enviar);
        chat = findViewById(R.id.chatbot_chat);
        insert = findViewById(R.id.chatbot_insert);
        home = findViewById(R.id.chatbot_btn_home);
        notificacao = findViewById(R.id.chatbot_btn_notificacao);
        pesquisar = findViewById(R.id.chatbot_btn_pesquisar);
        perfil = findViewById(R.id.chatbot_btn_perfil);
        voltar = findViewById(R.id.chatbot_btn_voltar);
        visualizar = findViewById(R.id.chatbot_btn_visualizar);
        loading = findViewById(R.id.chatbot_carregar);
        back = findViewById(R.id.chatbot_btn_back);

        x = listview.getX();

        subcategorias = new ArrayList<>();
        mensagens = new ArrayList<>();
        acompanhamento = "-1";

        start();

        addMensagem("Olá, seja bem-vindo ao chatbot da Saborear", true);
        if(cadastrando) {
            block(false, () -> {});
            addMensagem("Selecione as alternativas que correspondam com a sua receita", true);

            ArrayList<String> subcategorias;
            if(receita.getSubcategoria() == null) subcategorias = new ArrayList<>();
            else subcategorias = receita.getSubcategoria().getSubcategorias();

            for (classePergunta pergunta : StorageDatabase.perguntasdefinicao) {
                if(pergunta.getAcao().contains("Info")) continue;

                classePergunta npergunta = new classePergunta(pergunta);

                ArrayList<classeAlternativa> alternativas = new ArrayList<>();
                for (classeAlternativa alternativa : npergunta.getAlternativas()) {
                    classeAlternativa nalternativa = new classeAlternativa(alternativa);

                    if(subcategorias.contains(alternativa.getAlternativa()) || subcategorias.contains(alternativa.getIdacao())) {
                        this.subcategorias.add(nalternativa.getIdacao());
                        nalternativa.setSelect(true);
                    }
                    alternativas.add(nalternativa);
                }

                npergunta.setAlternativas(alternativas);
                addMensagem(npergunta);
                perguntas.add(npergunta);
            }

            addMensagem("Lembre-se de salvar ao voltar", true);

            ArrayList<classeAlternativa> alternativas = new ArrayList<>();
            alternativas.add(new classeAlternativa("0", "Voltar", "Voltar", 0));
            AlternativaView adapterView = new AlternativaView(Chatbot.this, alternativas, false);
            listview.setAdapter(adapterView);

        } else {
            block(true, () -> {});
            addMensagem("Com base nas suas escolhas, recomendaremos a receita ideal!", true);
            addPerguntas();
            make();
        }

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(insert); ignore.add(enviar); ignore.add(findViewById(R.id.chatbot_icon));
        inverted.add(chat);
        Utils.blackMode(findViewById(R.id.chatbot_layout), ignore, inverted);
    }

    private void end() {
        if(receitas.size() == 0) return;

        notify("(#047) Recomendando receita");

        classeReceita receita = receitas.get(0);
        addMensagem("Com base nas suas opções, nossa receita de "+receita.getNome().toLowerCase()+" será perfeita, clique para abrir o passo a passo!", true, false, receita);
        addMensagem("", true, true, receita);

        if(!acompanhamento.equals("-1")) {
            notify("(#467) Recomendando acompanhamento");

            mg = new ManageReceita(StorageDatabase.receitas);
            classeReceita nreceita = mg.getByIDCATEG(acompanhamento).getReceitas().get(0);
            addMensagem("Já para beber, "+nreceita.getNome().toLowerCase()+" será ideal!", true, false, nreceita);
            addMensagem("", true, true, nreceita);
        };

        block(true, () -> {
            notify("(#000) Reiniciando perguntas");

            try {
                start();
                perguntas.add(StorageDatabase.findPergunta("42"));
                make();
            } catch (Exception e) {
                Log.e("SaborearChat", e.getMessage());
            }

        });
    }

    private void start() {
        position = -1;
        perguntas = new ArrayList<>();
        receitas = StorageDatabase.receitas;
    }

    private void addPerguntas() {
        for (classePergunta pergunta : StorageDatabase.perguntas) {
            if(pergunta.getAcao().contains("Info")) continue;
            perguntas.add(pergunta);
        }
        Collections.shuffle(perguntas);
    }
    private void make() {
        try {
            if (++position == perguntas.size()) return;

            classePergunta pergunta = perguntas.get(position);
            ArrayList<classeAlternativa> alternativas = pergunta.getAlternativas();

            notify("(#477) Iniciando pergunta: "+pergunta.getPergunta());

            int countblock = 0;
            for (int i = 0; i < alternativas.size(); i++) {
                classeAlternativa alternativa = alternativas.get(i);
                mg = new ManageReceita(receitas);

                switch (pergunta.getAcao()) {
                    case "Subcategoria":
                        if (mg.getBySubcategoria(alternativa.getIdacao()).getReceitas().size() == 0) {
                            notify("(#196) Alternativa bloqueada: "+alternativa.getAlternativa());
                            alternativa.setBlock(true);
                            countblock++;
                        }
                        break;
                    case "nSubcategoria":
                        if (mg.getByExceptSubcategoria(alternativa.getIdacao()).getReceitas().size() == 0) {
                            notify("(#292) Alternativa bloqueada: "+alternativa.getAlternativa());
                            alternativa.setBlock(true);
                            countblock++;
                        }
                        break;
                    case "Tempo":
                        if (mg.getByTempo(0, Integer.parseInt(alternativa.getIdacao())).getReceitas().size() == 0) {
                            notify("(#478) Alternativa bloqueada: "+alternativa.getAlternativa());
                            alternativa.setBlock(true);
                            countblock++;
                        }
                        break;
                }
            }

            if ((alternativas.size() == 1 && countblock == 1) || (alternativas.size() > 1 && !(alternativas.size() - countblock > 1))) {
                if (position+1 == perguntas.size()) {
                    notify("(#171) Fim das perguntas");
                    end();
                } else {
                    notify("(#777) Pulando pergunta");
                    make();
                }

                return;
            }

            notify("(#989) Adicionando mensagem");
            addMensagem(pergunta.getPergunta(), true);

            if(!pergunta.getAcao().contains("Info")) {
                notify("(#144) Adicionando alternativa pular");
                alternativas.add(0, new classeAlternativa("-1", "Pular", "-1", 0));
            }

            notify("(#878) Atualizando visualizador de alternativas");
            AlternativaView adapterView = new AlternativaView(Chatbot.this, alternativas, pergunta.isMultiplos());
            listview.setAdapter(adapterView);
            listview.setOffscreenPageLimit(15);

            block(false, () -> { });
        } catch (Exception e) {
            Log.e("SaborearChat", e.getMessage());
        }
    }

    public void delMensagem() {
        mensagens.remove(mensagens.size()-1);
        atualizar();
    }

    public void addMensagem(String mensagem, Boolean isadmin) {
        mensagens.add(new classeMensagem(mensagem, isadmin, false, null));
        atualizar();
    }

    public void addMensagem(String mensagem, Boolean isadmin, Boolean hasimagem, classeReceita target) {
        mensagens.add(new classeMensagem(mensagem, isadmin, hasimagem, target));
        atualizar();
    }

    public void addMensagem(classePergunta pergunta) {
        classeMensagem mensagem = new classeMensagem(pergunta.getPergunta(), true, false, null);
        mensagem.setPergunta(pergunta);

        mensagens.add(mensagem);
        atualizar();
    }

    public void atualizar() {
        PerguntaView adapterView = new PerguntaView(Chatbot.this, mensagens);
        chat.setAdapter(adapterView);
        chat.setSelection(mensagens.size()-1);
    }

    public void notify(String mensagem) {
        Log.i("SaborearChat", mensagem);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizar();
    }
}