package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;

import Saborear.com.br.Adapter.NutrientesView;
import Saborear.com.br.Classes.classeNutricional;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class Receita extends AppCompatActivity {

    public static Activity activity;

    public static classeReceita receita;

    public static int pos, position;

    private Database db;

    private TextView nome, tempo, nota, ingredientes, preparo, autor;

    private ImageButton voltar;
    private ImageButton ingred;
    private ImageButton prep;
    private ImageButton btnnutrientes;
    private ImageButton back;
    private ImageView imagem;

    private ProgressBar loading;
    private Bitmap foto;
    private ListView listview;

    private boolean visualizacao = false;
    private boolean prepup = false;
    private boolean ingredup = false;
    private boolean nutrientesup = false;

    private static ImageButton home;
    private static ImageButton pesquisar;
    private static ImageButton perfil;
    private static ImageButton notificacao;
    private static ImageButton comentario;
    private static ImageButton curtir;
    private static ImageButton receitaEdit;
    private static ImageButton mercado;
    private static ImageButton compartilhar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.receita);
        initialize();
        admin();

        compartilhar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");

            Uri imageUri = Utils.getImageUri(this, receita.getImagem());
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.putExtra(Intent.EXTRA_TEXT, "Aprenda a cozinhar: " + receita.getNome() + "\n\n" + "Ingredientes:\n" + receita.listarIngredientes() + "\n\n" + "Modo de preparo:\n" + receita.getDescricao() + "\n\nQuer conhecer mais receitas como essa? Conheça o Saborear em " + StorageDatabase.URL);

            startActivity(Intent.createChooser(intent, "Compartilhar via"));
        });

        btnnutrientes.setVisibility(View.VISIBLE);
        btnnutrientes.setOnClickListener(v -> {
            if(nutrientesup) {
                btnnutrientes.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_nutrientesdown));
                listview.setVisibility(View.VISIBLE);
            } else {
                btnnutrientes.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_nutrientesup));
                listview.setVisibility(View.GONE);
            }

            nutrientesup = !nutrientesup;
        });

        preparo.setVisibility(View.VISIBLE);
        prep.setOnClickListener(v -> {
            if(prepup) {
                prep.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_preparodown));
                preparo.setVisibility(View.VISIBLE);
            } else {
                prep.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_preparoup));
                preparo.setVisibility(View.GONE);
            }

            prepup = !prepup;
        });

        ingredientes.setVisibility(View.VISIBLE);
        ingred.setOnClickListener(v -> {

            if(ingredup) {
                ingred.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_ingreddown));
                ingredientes.setVisibility(View.VISIBLE);
            } else {
                ingred.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_ingredup));
                ingredientes.setVisibility(View.GONE);
            }


            ingredup = !ingredup;
        });

        curtir.setOnClickListener(v -> {
            String sql;
            if(receita.getReceitausuario().getCurtida()) {
                sql = "delete from salva where id_receita='@id' and email='@email';";
                curtir.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_favorito_vazio));
            } else {
                sql = "insert into salva(email, id_receita) values('@email', '@id');";
                curtir.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_favorito));
            }
            receita.getReceitausuario().setCurtida(!receita.getReceitausuario().getCurtida());
            sql = sql.replace("@id", receita.getIdreceita());
            sql = InternalDatabase.convert(sql);

            block(true);
            db.requestScript(sql, data -> {
                Manage.findReceita(receita).update(new classeReceita(receita));
                block(false);
            });
        });

        mercado.setOnClickListener(v -> {
            Market.receita = new classeReceita(receita);
            Intent myIntent = new Intent(getApplicationContext(), Market.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
        });

        comentario.setOnClickListener(v -> {
            Intent myIntent = new Intent(getApplicationContext(), Comentario.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
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

    private void admin() {
        if(visualizacao) return;

        if(!StorageDatabase.isAdmin && !receita.getCriador().getEmail().equals(InternalDatabase.getEmail())) return;
        receitaEdit.setVisibility(View.VISIBLE); receitaEdit.setEnabled(true);
        receitaEdit.setOnClickListener(v -> {
            CadastrarReceita.preload = true;
            CadastrarReceita.idload = receita.getIdreceita();
            CadastrarReceita.receita = new classeReceita(receita);
            Intent myIntent = new Intent(getApplicationContext(), CadastrarReceita.class);
            startActivityForResult(myIntent, 1);
            this.overridePendingTransition(0, 0);
        });
    }
    private void block(boolean block) {
        View[] view = new View[]{findViewById(R.id.receita_press_01), findViewById(R.id.receita_press_02), findViewById(R.id.receita_press_03), findViewById(R.id.receita_press_04), findViewById(R.id.receita_press_05), curtir};
        for (View v : view)
            v.setEnabled(!block);
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);

        activity = this;
        db = new Database(this);

        voltar = findViewById(R.id.receita_btn_voltar);
        home = findViewById(R.id.receita_btn_home);
        pesquisar = findViewById(R.id.receita_btn_pesquisar);
        perfil = findViewById(R.id.receita_btn_perfil);
        notificacao = findViewById(R.id.receita_btn_notificacao);
        nome = findViewById(R.id.receita_nome);
        tempo = findViewById(R.id.receita_tempo);
        nota = findViewById(R.id.receita_nota);
        ingredientes = findViewById(R.id.receita_txt_ingredientes);
        preparo = findViewById(R.id.receita_txt_preparo);
        imagem = findViewById(R.id.receita_imagem);
        loading = findViewById(R.id.receita_carregar);
        comentario = findViewById(R.id.receita_btn_comentarios);
        curtir = findViewById(R.id.receita_btn_curtir);
        prep = findViewById(R.id.receita_btn_preparo);
        ingred = findViewById(R.id.receita_btn_ingredientes);
        receitaEdit = findViewById(R.id.receita_receita_edit);
        autor = findViewById(R.id.receita_autor);
        listview = findViewById(R.id.receita_listview_nutricional);
        btnnutrientes = findViewById(R.id.receita_btn_nutricional);
        back = findViewById(R.id.receita_btn_back);
        mercado = findViewById(R.id.receita_btn_mercado);
        compartilhar = findViewById(R.id.receita_btn_compartilhar);

        prepup = false;
        ingredup = false;

        nome.setText(receita.getNome());
        tempo.setText(Utils.converterTempo(receita.getTempo()));
        nota.setText(""+receita.getNota());
        preparo.setText(receita.getDescricao());
        autor.setText("Publicado por "+(receita.getCriador().getNome()));
        ingredientes.setText(receita.listarIngredientes());

        Intent intent = getIntent();
        visualizacao = intent.getBooleanExtra("visualizacao", false);

        ingredientes.setMovementMethod(new ScrollingMovementMethod());
        preparo.setMovementMethod(new ScrollingMovementMethod());

        if(receita.getImagem() == null) {
            block(true);
            db.requestScript("select imagem from receita where id_receita='@id';".replace("@id", receita.getIdreceita()), end -> {
                block(false);
                receita.setImagem(ImageDatabase.decode(end.get(0).get("imagem")));
                imagem.setImageBitmap(receita.getImagem());
            });
        } else imagem.setImageBitmap(receita.getImagem());

        ImageView[] estrela = new ImageView[] {findViewById(R.id.receita_press_01), findViewById(R.id.receita_press_02), findViewById(R.id.receita_press_03), findViewById(R.id.receita_press_04), findViewById(R.id.receita_press_05)};

        if(receita.getReceitausuario().getCurtida())
            curtir.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_favorito));
        else
            curtir.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_btn_favorito_vazio));

        int[] full = new int[]{R.drawable.saborear_estrela_01_press, R.drawable.saborear_estrela_02_press, R.drawable.saborear_estrela_03_press, R.drawable.saborear_estrela_04_press, R.drawable.saborear_estrela_05_press};
        int[] vazio;

        if(InternalDatabase.isDarkmode())
            vazio = new int[]{R.drawable.saborear_dark_estrela_01, R.drawable.saborear_dark_estrela_02, R.drawable.saborear_dark_estrela_03, R.drawable.saborear_dark_estrela_04, R.drawable.saborear_dark_estrela_05};
        else
            vazio = new int[]{R.drawable.saborear_estrela_01, R.drawable.saborear_estrela_02, R.drawable.saborear_estrela_03, R.drawable.saborear_estrela_04, R.drawable.saborear_estrela_05};

        for (int i = 0; i<5; i++)
            estrela[i].setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), vazio[i]));

        ImageView[] finalEstrela = estrela;

        int nota = receita.getReceitausuario().getNota();
        if(nota > 0) {
            pos = nota - 1;
            finalEstrela[nota - 1].setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), full[nota - 1]));
        } else
            pos = -1;

        atualizar();

        if(visualizacao) {
            home.setVisibility(View.GONE);
            notificacao.setVisibility(View.GONE);
            perfil.setVisibility(View.GONE);
            pesquisar.setVisibility(View.GONE);
            receitaEdit.setVisibility(View.GONE);
            comentario.setVisibility(View.GONE);

            for (ImageView view : estrela)
                view.setVisibility(View.GONE);

            ArrayList<View> ignore = new ArrayList<>();
            for (ImageView star : estrela)
                ignore.add(star);
            estrela = new ImageView[] {findViewById(R.id.receita_estrela_01), findViewById(R.id.receita_estrela_02), findViewById(R.id.receita_estrela_03), findViewById(R.id.receita_estrela_04), findViewById(R.id.receita_estrela_05), comentario};
            for (ImageView star : estrela)
                ignore.add(star);
            ignore.add(comentario); ignore.add(curtir); ignore.add(imagem);
            ignore.add(findViewById(R.id.receita_filtro));
            Utils.blackMode(findViewById(R.id.receita_layout), ignore, null);
            return;
        }

        String sql = "update receita set views = views + 1 where id_receita = '@id';".replace("@id", receita.getIdreceita());
        Manage.findReceita(receita).setViews(receita.getViews()+1);
        db.sendScript(sql);

        for (int i = 0; i<5; i++) {
            int finalI = i;
            estrela[i].setOnClickListener(v -> {
                block(true);
                String cmd;
                if(pos == finalI)
                    cmd = "delete from usuario_receita where email = '@email' and id_receita = '@id';";
                else
                    cmd = "delete from usuario_receita where email = '@email' and id_receita = '@id'; insert into usuario_receita(email, id_receita, nota) values('@email', '@id', @nota);".replace("@nota", ""+(finalI+1));
                cmd += "select coalesce(round(avg(nota), 2), 0) as nota from usuario_receita where id_receita = '@id';";
                cmd = cmd.replace("@id", receita.getIdreceita());
                cmd = InternalDatabase.convert(cmd);

                db.requestScript(cmd, data -> {
                    for (Map<String, String> item : data) {
                        this.nota.setText(item.get("nota"));
                    }

                    if(pos == finalI) receita.getReceitausuario().setNota(-1);
                    else receita.getReceitausuario().setNota(finalI+1);

                    receita.setNota(Double.parseDouble(this.nota.getText().toString()));

                    if (pos != -1)
                        finalEstrela[pos].setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), vazio[pos]));
                    if(pos != finalI) {
                        finalEstrela[finalI].setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), full[finalI]));
                        pos = finalI;
                    } else
                        pos = -1;

                    Manage.findReceita(receita).update(new classeReceita(receita));

                    atualizar();
                    block(false);
                });
            });
        }

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        for (ImageView star : estrela)
            ignore.add(star);
        estrela = new ImageView[] {findViewById(R.id.receita_estrela_01), findViewById(R.id.receita_estrela_02), findViewById(R.id.receita_estrela_03), findViewById(R.id.receita_estrela_04), findViewById(R.id.receita_estrela_05), comentario};
        for (ImageView star : estrela)
            ignore.add(star);

        ignore.add(comentario); ignore.add(curtir); ignore.add(imagem);
        ignore.add(findViewById(R.id.receita_filtro)); ignore.add(mercado);
        inverted.add(listview);
        Utils.blackMode(findViewById(R.id.receita_layout), ignore, inverted);
    }

    private void atualizar() {
        ArrayList<classeNutricional> nutrientes = new ArrayList<>();

        nutrientes.add(receita.getNutricional());

        NutrientesView adapterView = new NutrientesView(this, nutrientes);
        listview.setAdapter(adapterView);

        ImageView[] estrela = new ImageView[]{findViewById(R.id.receita_estrela_01), findViewById(R.id.receita_estrela_02), findViewById(R.id.receita_estrela_03), findViewById(R.id.receita_estrela_04), findViewById(R.id.receita_estrela_05), comentario};
        double nota = receita.getNota();

        for (int i = 0; i < 5; i++)
            if (nota >= (i + 1) && nota > 0)
                estrela[i].setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_estrela));
            else if (InternalDatabase.isDarkmode())
                estrela[i].setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_dark_estrela_vazia));
            else
                estrela[i].setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_estrela_vazia));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || resultCode == RESULT_OK && data != null) {
            foto = StorageDatabase.imagem;
            initialize();
        }
    }

    protected void onResume() {
        super.onResume();
        atualizar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityTracker.delActivity(this);
    }
}