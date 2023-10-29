package Saborear.com.br;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Saborear.com.br.Adapter.IngredienteView;
import Saborear.com.br.Classes.classeCategoriaReceita;
import Saborear.com.br.Classes.classeIngrediente;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Classes.classeReceitaSubcategoria;
import Saborear.com.br.Classes.classeReceitaUsuario;
import Saborear.com.br.Classes.classeUsuario;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.FireDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Manage.Manage;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class CadastrarReceita extends AppCompatActivity {

    private static Activity activity;
    private static Database db;
    private static ImageButton back, home, pesquisar, perfil, notificacao, salvar, voltar, deletar, addchatbot, criar;
    private static Button imagem, chatingredientes, addingrediente;
    private static ImageView finish, ok;

    private static EditText titulo, tempo, modo;

    public static Bitmap bitmap;
    private static ListView listview;

    private static ArrayList<String> id, medidas;
    private static Map<String, String> quantidade;
    private static ArrayList<classeIngrediente> ingredientes;

    private static Spinner categoria, visibilidade;
    private long time = 0;

    public static ArrayList<String> storage;

    public static int size = 1;

    public static classeReceita receita;
    public static String idload;
    public static boolean preload = false, newfoto = false;
    private static ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastroreceita);
        initialize();

        voltar.setOnClickListener(v -> {
            if(!hasChanges()) {
                this.finish();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Há alterações não salvas")
                    .setTitle("Saborear")
                    .setNegativeButton("Cancelar", (dialog, id) -> {})
                    .setPositiveButton("Voltar", (dialog, id) -> this.finish());
            builder.create().show();
        });

        addingrediente.setOnClickListener(v -> {
            addIngrediente();
            atualizar();
        });

        chatingredientes.setOnClickListener(v -> {
            Chatingredientes.ingredientes = (ArrayList<classeIngrediente>) ingredientes.clone();
            Intent myIntent = new Intent(getApplicationContext(), Chatingredientes.class);
            startActivityForResult(myIntent, 3);
            Chatingredientes.receita = new classeReceita(receita);
            this.overridePendingTransition(0, 0);
        });

        addchatbot.setOnClickListener(v -> {
            Chatbot.receita = preload ? new classeReceita(receita) : receita;
            Chatbot.cadastrando = true;
            Intent myIntent = new Intent(getApplicationContext(), Chatbot.class);
            this.overridePendingTransition(0, 0);
            startActivityForResult(myIntent, 2);
        });

        deletar.setOnClickListener(v -> {
            if(!preload) {
                Toast.makeText(getApplicationContext(), "Primeiro cadastre a receita", Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Você realmente deseja deletar a receita?")
                    .setTitle("Saborear")
                    .setNegativeButton("Cancelar", (dialog, id) -> {})
                    .setPositiveButton("Sim", (dialog, id) -> {
                        block(true);
                        if(preload) {
                            db.requestScript(receita.scriptDeletar(), onFinish -> {
                                Manage.deletarReceita(receita.getIdreceita());

                                Toast.makeText(getApplicationContext(), "Receita deletada", Toast.LENGTH_SHORT).show();
                                FireDatabase.updateReceita(receita.getIdreceita(), "deletar");


                                ActivityTracker.finishActivity(Receita.activity);
                                activity.finish();
                            });
                        } else finish();
                    });
            builder.create().show();
        });

        back.setOnClickListener(v -> {
            if(!hasChanges()) {
                this.finish();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Há alterações não salvas")
                    .setTitle("Saborear")
                    .setNegativeButton("Cancelar", (dialog, id) -> {})
                    .setPositiveButton("Voltar", (dialog, id) -> this.finish());
            builder.create().show();
        });

        imagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10);
        });

        salvar.setOnClickListener(v -> save());
        criar.setOnClickListener(v -> save());

        perfil.setOnClickListener(v -> {
            if(hasChanges()) {
                openDialog(Perfil.class);
                return;
            }

            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Perfil.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        pesquisar.setOnClickListener(v -> {
            if(hasChanges()) {
                openDialog(Pesquisar.class);
                return;
            }

            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Pesquisar.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        home.setOnClickListener(v -> {
            if(hasChanges()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Há alterações não salvas")
                        .setTitle("Saborear")
                        .setNegativeButton("Cancelar", (dialog, id) -> {})
                        .setPositiveButton("Voltar", (dialog, id) -> {
                            ActivityTracker.finishExceptMain(this);
                            this.finish();
                        });
                builder.create().show();
                return;
            }

            ActivityTracker.finishExceptMain(this);
            finish();
        });

        notificacao.setOnClickListener(v -> {
            if(hasChanges()) {
                openDialog(Notificacao.class);
                return;
            }

            ActivityTracker.finishExceptMain(this);
            Intent myIntent = new Intent(getApplicationContext(), Notificacao.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });
    }

    public static void block(Boolean block) {
        ViewGroup layout = activity.findViewById(R.id.cadastroreceita_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            view.setEnabled(!block);
        }

        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);
        activity = CadastrarReceita.this;

        home = findViewById(R.id.cadastroreceita_btn_home);
        pesquisar = findViewById(R.id.cadastroreceita_btn_pesquisar);
        perfil = findViewById(R.id.cadastroreceita_btn_perfil);
        notificacao = findViewById(R.id.cadastroreceita_btn_notificacao);
        voltar = findViewById(R.id.cadastroreceita_btn_voltar);
        salvar = findViewById(R.id.cadastroreceita_btn_salvar);
        imagem = findViewById(R.id.cadastroreceita_btn_imagem);
        titulo = findViewById(R.id.cadastroreceita_insert_01);
        tempo = findViewById(R.id.cadastroreceita_insert_02);
        modo = findViewById(R.id.cadastroreceita_insert_03);
        listview = findViewById(R.id.cadastroreceita_listview_ingredientes);
        categoria = findViewById(R.id.cadastroreceita_categoria);
        visibilidade = findViewById(R.id.cadastroreceita_visibilidade);
        loading = findViewById(R.id.cadastroreceita_carregar);
        finish = findViewById(R.id.cadastroreceita_icon_finish);
        ok = findViewById(R.id.cadastroreceita_icon_finish_02);
        deletar = findViewById(R.id.cadastroreceita_btn_deletar);
        addchatbot = findViewById(R.id.cadastroreceita_btn_addchatbot);
        chatingredientes = findViewById(R.id.cadastroreceita_btn_chatingredientes);
        addingrediente = findViewById(R.id.cadastroreceita_btn_addingrediente);
        back = findViewById(R.id.cadastroreceita_btn_back);
        criar = findViewById(R.id.cadastroreceita_btn_criar);

        if(!preload) {
            deletar.setVisibility(View.INVISIBLE);
            salvar.setVisibility(View.INVISIBLE);
            criar.setVisibility(View.VISIBLE);

            receita = new classeReceita();

            ConstraintLayout constraintLayout = findViewById(R.id.cadastroreceita_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(salvar.getId(), ConstraintSet.BOTTOM, pesquisar.getId(), ConstraintSet.TOP, Utils.intToDp(16));
            constraintSet.applyTo(constraintLayout);
        }

        id = new ArrayList<>();
        quantidade = new HashMap<>();
        medidas = new ArrayList<>();
        ingredientes = new ArrayList<>();

        newfoto = false;

        medidas.add("un."); medidas.add("kg"); medidas.add("g"); medidas.add("ml");
        medidas.add("lata"); medidas.add("xícara"); medidas.add("colher de sopa"); medidas.add("colher de sobremesa");

        if(!preload) {
            voltar.setImageBitmap(Utils.drawableToBitmap(activity, R.drawable.saborear_voltar_cadastrarreceita));
            addIngrediente();
            addIngrediente();
            atualizar();
        } else
            voltar.setImageBitmap(Utils.drawableToBitmap(activity, R.drawable.saborear_btn_editarreceita));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, StorageDatabase.nomescategoria);
        categoria.setAdapter(adapter);

        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i == 0)
                        ok.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_delete));
                    else
                        ok.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_done));

                    String value = categoria.getSelectedItem().toString();
                    receita.setCategoriareceita(new classeCategoriaReceita(StorageDatabase.categ.get(value), value));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        ArrayList<String> nomes = new ArrayList<>();
        nomes.add("Pública"); nomes.add("Privada");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomes);
        visibilidade.setAdapter(adapter);

        if(preload)
            preLoad();

        ImageView[] icons = new ImageView[]{findViewById(R.id.cadastroreceita_icon_tempo), findViewById(R.id.cadastroreceita_icon_titulo), findViewById(R.id.cadastroreceita_icon_finish_02), findViewById(R.id.cadastroreceita_icon_finish), findViewById(R.id.cadastroreceita_icon_categoria), findViewById(R.id.cadastroreceita_icon_imagem), findViewById(R.id.cadastroreceita_icon_modo), findViewById(R.id.cadastroreceita_icon_olho)};
        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        for (ImageView icon : icons) {
            ignore.add(icon);
        }

        ignore.add(titulo); ignore.add(tempo); ignore.add(modo); ignore.add(imagem); ignore.add(criar);
        ignore.add(salvar); ignore.add(deletar); inverted.add(listview); ignore.add(chatingredientes);
        ignore.add(findViewById(R.id.cadastroreceita_btn_addingrediente));
        ignore.add(findViewById(R.id.cadastroreceita_icon_chat));
        ignore.add(findViewById(R.id.cadastroreceita_btn_addchatbot));
        ignore.add(findViewById(R.id.cadastroreceita_icon_lanche));
        Utils.blackMode(findViewById(R.id.cadastroreceita_layout), ignore, inverted);
    }

    public static void preLoad() {
        voltar.setVisibility(View.VISIBLE);
        titulo.setText(receita.getNome());
        tempo.setText(receita.getTempo()+"");
        modo.setText(receita.getDescricao());
        visibilidade.setSelection(receita.getPublica() ? 0 : 1);
        categoria.setSelection(StorageDatabase.nomescategoria.indexOf(receita.getCategoriareceita().getNome()));

        for (classeIngrediente ingrediente : receita.getIngredientes()) {
            ingredientes.add(new classeIngrediente(ingrediente));
            id.add(ingrediente.getIndingrediente());
        }
        atualizar();

        finish.setImageBitmap(Utils.drawableToBitmap(activity, R.drawable.saborear_icon_done));
    }

    public static void adapterAtualizar() {
        try {
            IngredienteView adapter = (IngredienteView) listview.getAdapter();
            if(adapter != null) {
                id = adapter.getId();
                ingredientes = adapter.getIngredientes();
                size = id.size();
            }
        } catch (Exception e) {
            Log.i("SaborearDatabase", e.getMessage());
        }
    }
    public static void delIngrediente() {
        size--;
        id.remove(id.size()-1);
        ingredientes.remove(ingredientes.size()-1);

        ViewGroup.LayoutParams layoutParams = listview.getLayoutParams();
        layoutParams.height = Utils.intToDp(40*id.size()+16*(id.size()-1));
        listview.setLayoutParams(layoutParams);

        Log.i("SaborearDatabase", id.toString());
        IngredienteView adapterView = new IngredienteView(activity, id, StorageDatabase.nomes, medidas, ingredientes);
        listview.setAdapter(adapterView);
    }

    public static void addIngrediente() {
        adapterAtualizar();

        size++;
        id.add("" + size);
        ingredientes.add(new classeIngrediente("0", null, "Clique aqui", "0 un."));
    }

    public static void addIngrediente(classeIngrediente ingrediente) {
        adapterAtualizar();

        size++;
        id.add("" + size);
        ingredientes.add(ingrediente);
    }

    public static void atualizar() {
        block(true);
        adapterAtualizar();

        try {
            ViewGroup.LayoutParams layoutParams = listview.getLayoutParams();
            layoutParams.height = Utils.intToDp(40*id.size()+16*(id.size()-1));
            listview.setLayoutParams(layoutParams);

            IngredienteView adapterView = new IngredienteView(activity, (ArrayList<String>) id.clone(), StorageDatabase.nomes, medidas, ingredientes);
            listview.setAdapter(adapterView);

        } catch (Exception e) {
            Log.i("SaborearDatabase", e.getMessage());
        }

        block(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                newfoto = true;
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                finish.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_done));
                receita.setImagem(bitmap);
            } catch (IOException e) {
                Log.i("SaborearDatabase", e.getMessage());
            }
        }
    }
    private Boolean hasChanges() {

        String title = titulo.getText().toString();
        String time = tempo.getText().toString();
        String descricao = modo.getText().toString();
        String visivel = visibilidade.getSelectedItem().toString();

        if(!preload) {
            if(title.length() > 0) return true;
            if(time.length() > 0) return true;
            if(descricao.length() > 0) return true;
            if(newfoto) return true;

            return false;
        }

        classeReceita novareceita = new classeReceita(receita);
        classeReceita oldreceita = Manage.findReceita(receita);

        novareceita.setNome(title);
        novareceita.setTempo(Integer.parseInt(time));
        novareceita.setPublica(visivel.equals("Pública") ? true : false);
        novareceita.setIngredientes(ingredientes);
        novareceita.setDescricao(descricao);

        Boolean change = false;
        if(!novareceita.getNome().equals(oldreceita.getNome())) change = true;
        if(novareceita.getTempo() != oldreceita.getTempo()) change = true;
        if(novareceita.getPublica() != oldreceita.getPublica()) change = true;

        if(novareceita.getIngredientes().size() != oldreceita.getIngredientes().size()) change = true;
        else for (classeIngrediente novoIngrediente : novareceita.getIngredientes()) {
            Boolean find = false;
            for (classeIngrediente oldIngrediente : oldreceita.getIngredientes()) {
                if (!oldIngrediente.getNome().equals(novoIngrediente.getNome())) continue;
                if (!oldIngrediente.getQuantidadeString().equals(novoIngrediente.getQuantidadeString())) continue;
                if (!oldIngrediente.getMedida().equals(novoIngrediente.getMedida())) continue;
                find = true;
                break;
            }
            if(!find) change = true;
        }

        if(!novareceita.getDescricao().equals(oldreceita.getDescricao())) change = true;
        if(newfoto) change = true;
        if(!novareceita.getCategoriareceita().getNome().equals(oldreceita.getCategoriareceita().getNome())) change = true;

        ArrayList<String> novasub = novareceita.getSubcategoria().getSubcategorias();
        ArrayList<String> oldsub = oldreceita.getSubcategoria().getSubcategorias();

        if(oldsub.size() != novasub.size() && !novasub.toString().equals(oldsub.toString())) change = true;
        else for (String subcategoria : novasub) {
            if(oldsub.contains(subcategoria)) continue;
            change = true;
        }
        return change;
    }

    private void openDialog(Class nclass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Há alterações não salvas")
                .setTitle("Saborear")
                .setNegativeButton("Cancelar", (dialog, id) -> {})
                .setPositiveButton("Voltar", (dialog, id) -> {
                    ActivityTracker.finishExceptMain(this);
                    Intent myIntent = new Intent(getApplicationContext(), nclass);
                    startActivity(myIntent);
                    this.overridePendingTransition(0, 0);
                    this.finish();
                });
        builder.create().show();
    }

    public void save() {
        String title = titulo.getText().toString();
        String time = tempo.getText().toString();
        String descricao = modo.getText().toString();
        String visivel = visibilidade.getSelectedItem().toString();

        if(title.length() == 0 || categoria.getSelectedItemPosition() == 0 || time.length() == 0 || descricao.length() == 0 || visivel.length() == 0) {
            Toast.makeText(getApplicationContext(), "Preencha os campos", Toast.LENGTH_SHORT).show();
            return;
        } else if(bitmap == null && !preload) {
            Toast.makeText(getApplicationContext(), "Selecione uma foto", Toast.LENGTH_SHORT).show();
            return;
        }

        adapterAtualizar();


        ArrayList<classeIngrediente> ningredientes = new ArrayList<>();
        for (classeIngrediente ingrediente : ingredientes) {
            if(ingrediente.getNome().equals("Clique aqui")) continue;
            ningredientes.add(ingrediente);
        }

        if(ningredientes.size() < 2) {
            Toast.makeText(getApplicationContext(), "Adicione no mínimo 2 ingredientes", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!preload) {
            Random random = new Random();
            int i = random.nextInt(99010) + 500;

            receita.setIdreceita(""+i);
            receita.setReceitausuario(new classeReceitaUsuario(0, false));
            receita.setNome(title);
            receita.setTempo(Integer.parseInt(time));
            receita.setPublica(visivel.equals("Pública") ? true : false);
            receita.setIngredientes(ningredientes);
            receita.setDescricao(descricao);
            receita.setCriador(new classeUsuario(InternalDatabase.getEmail(), InternalDatabase.getNome(), InternalDatabase.getSenha(), InternalDatabase.getTelefone(), StorageDatabase.isAdmin, StorageDatabase.imagem));
            receita.setCriacao(System.currentTimeMillis());

            if(StorageDatabase.isAdmin) receita.setValidar(false);
            else receita.setValidar(true);

            if(receita.getSubcategoria() == null) receita.setSubcategoria(new classeReceitaSubcategoria(new ArrayList<>()));

            block(true);
            db.requestScript(receita.scriptCriar()+receita.scriptSubcategoria(), data -> {
                StorageDatabase.receitas.add(new classeReceita(receita));

                if(StorageDatabase.isAdmin) Toast.makeText(getApplicationContext(), "Receita adicionada", Toast.LENGTH_SHORT).show();
                else Toast.makeText(getApplicationContext(), "Sua receita foi enviada para análise", Toast.LENGTH_SHORT).show();

                StorageDatabase.imagem = bitmap;
                finish();

                FireDatabase.updateReceita(receita.getIdreceita(), "criar");

                block(false);
            });

        } else {
            receita.setNome(title);
            receita.setTempo(Integer.parseInt(time));
            receita.setPublica(visivel.equals("Pública") ? true : false);
            receita.setIngredientes(ningredientes);
            receita.setDescricao(descricao);

            block(true);
            db.requestScript(receita.scriptAtualizar()+receita.scriptSubcategoria(), finish -> {
                Main.atualizar();
                StorageDatabase.imagem = bitmap;

                for (classeReceita nreceita : StorageDatabase.receitas) {
                    if(!nreceita.getIdreceita().equals(receita.getIdreceita())) continue;
                    nreceita.update(receita);
                    break;
                }
                Receita.receita = new classeReceita(receita);

                Toast.makeText(getApplicationContext(), "Receita atualizada", Toast.LENGTH_SHORT).show();
                finish();

                FireDatabase.updateReceita(receita.getIdreceita(), "atualizar");

                block(false);
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(!hasChanges()) {
            super.onBackPressed();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Há alterações não salvas")
                .setTitle("Saborear")
                .setNegativeButton("Cancelar", (dialog, id) -> {})
                .setPositiveButton("Voltar", (dialog, id) -> super.onBackPressed());
        builder.create().show();
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