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

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Saborear.com.br.Adapter.CadastroUtilizacaoView;
import Saborear.com.br.Classes.classeTempero;
import Saborear.com.br.Classes.classeUtilizacao;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.FireDatabase;
import Saborear.com.br.Database.StorageDatabase;
import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Utils.Utils;

public class CadastrarTempero  extends AppCompatActivity {
    private Database db;
    private ImageButton back, voltar, home, notificacao, pesquisar, perfil, cadastro;
    private Button imagem;
    private ImageView finish;
    private classeTempero tempero;
    private EditText nome, descricao;
    private Bitmap bitmap;
    private Map<String, String> util;
    private ProgressBar loading;
    public static classeTempero preload;
    private static ListView listView;

    public static Activity activity;
    public static ArrayList<classeUtilizacao> utilizacoes;

    public static Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.cadastrotempero);
        initialize();

        Utils.setupChat(this, findViewById(R.id.cadastrotempero_layout));

        cadastro.setOnClickListener(v -> {
            if(nome.length() == 0) {
                nome.setError("Digite um nome");
                return;
            }
            if(descricao.length() == 0) {
                descricao.setError("Digite uma descrição");
                return;
            }
            if(bitmap == null) {
                Toast.makeText(getApplicationContext(), "Selecione uma foto", Toast.LENGTH_SHORT).show();
                return;
            }
            if(utilizacoes.size() == 0) {
                Toast.makeText(getApplicationContext(), "Selecione ao menos um uso", Toast.LENGTH_SHORT).show();
                return;
            }

            block(true);
            classeTempero temp = new classeTempero("-1", nome.getText().toString(), descricao.getText().toString(), bitmap, utilizacoes);
            if(preload != null) temp.setIdtempero(preload.getIdtempero());

            db.requestScript(temp.deleteScript()+temp.createScript()+temp.utilizacoesScript(), data -> {
                block(false);

                if(preload == null) {
                    StorageDatabase.temperos.add(new classeTempero(temp));
                    Toast.makeText(getApplicationContext(), "Tempero cadastrado", Toast.LENGTH_SHORT).show();
                    FireDatabase.updateTempero(temp.getIdtempero(), "criar");
                }  else {
                    Tempero.tempero = new classeTempero(temp);
                    Toast.makeText(getApplicationContext(), "Alterações salvas", Toast.LENGTH_SHORT).show();
                    FireDatabase.updateTempero(temp.getIdtempero(), "atualizar");
                }

                this.finish();
            });

        });

        back.setOnClickListener(v -> this.finish());

        imagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
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

    private void block(boolean block) {
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);

        cadastro.setEnabled(!block);
    }

    public void initialize() {
        db = new Database(this);
        activity = this;
        voltar = findViewById(R.id.cadastrotempero_btn_voltar);
        home = findViewById(R.id.cadastrotempero_btn_home);
        pesquisar = findViewById(R.id.cadastrotempero_btn_pesquisar);
        perfil = findViewById(R.id.cadastrotempero_btn_perfil);
        notificacao = findViewById(R.id.cadastrotempero_btn_notificacao);
        cadastro = findViewById(R.id.cadastrotempero_btn_cadastrar);
        imagem = findViewById(R.id.cadastrotempero_btn_imagem);
        nome = findViewById(R.id.cadastrotempero_insert_01);
        descricao = findViewById(R.id.cadastrotempero_insert_02);
        spinner = findViewById(R.id.cadastrotempero_spinner);
        finish = findViewById(R.id.cadastrotempero_icon_finish);
        loading = findViewById(R.id.cadastrotempero_carregar);
        listView = findViewById(R.id.receita_listview_nutricional);
        back = findViewById(R.id.cadastrotempero_btn_back);

        util = new HashMap<>();
        utilizacoes = new ArrayList<>();

        int[] ids = new int[]{R.drawable.saborear_utilizacao_00, R.drawable.saborear_utilizacao_01, R.drawable.saborear_utilizacao_02, R.drawable.saborear_utilizacao_03, R.drawable.saborear_utilizacao_04, R.drawable.saborear_utilizacao_05, R.drawable.saborear_utilizacao_06, R.drawable.saborear_utilizacao_07, R.drawable.saborear_utilizacao_08, R.drawable.saborear_utilizacao_09};

        ArrayList<String> arr = new ArrayList<>();
        ArrayList<String> aux = new ArrayList<>();

        if(preload != null) {
            nome.setText(preload.getNome());
            descricao.setText(preload.getDescricao());

            bitmap = preload.getBitmap();
            finish.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_done));

            preload.getUtilizacoes().forEach(util -> aux.add(util.getNome()));
            utilizacoes = preload.getUtilizacoes();
        }

        for (String key : StorageDatabase.util.keySet()) {
            if(!aux.contains(key)) arr.add(key);
        }

        util = StorageDatabase.util;

        atualizarUsos();

        Collections.sort(arr); arr.add(0, "Clique para adicionar usos");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String value = spinner.getSelectedItem().toString();
                    utilizacoes.add(new classeUtilizacao(util.get(value), value, Utils.drawableToBitmap(getApplicationContext(),  ids[Integer.parseInt(util.get(value))])));
                    spinner.setSelection(0);

                    atualizarUsos();

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
                    adapter.remove(value);
                    spinner.setAdapter(adapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        inverted.add(findViewById(R.id.cadastrotempero_btn_imagem));
        inverted.add(findViewById(R.id.cadastrotempero_icon_finish));
        inverted.add(findViewById(R.id.cadastrotempero_btn_cadastrar));
        Utils.blackMode(findViewById(R.id.cadastrotempero_layout), ignore, inverted);
    }

    public static void atualizarUsos() {
        ArrayList<String> arr = new ArrayList<>();
        utilizacoes.forEach(util -> arr.add(util.getNome()));

        CadastroUtilizacaoView adapterView = new CadastroUtilizacaoView(activity, arr);
        listView.setAdapter(adapterView);

        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = Utils.intToDp(64*(arr.size())+16*(arr.size()-1));
        listView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                finish.setImageBitmap(Utils.drawableToBitmap(getApplicationContext(), R.drawable.saborear_icon_done));
            } catch (IOException e) {
                Log.i("SaborearDatabase", e.getMessage());
            }
        } else if(requestCode == 3) {
            this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


}
