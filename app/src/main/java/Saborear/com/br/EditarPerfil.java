package Saborear.com.br;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import Saborear.com.br.Utils.ActivityTracker;
import Saborear.com.br.Database.Database;
import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.InternalDatabase;
import Saborear.com.br.Utils.Utils;

@SuppressWarnings("deprecation")
public class EditarPerfil  extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Database db;
    private ImageView foto, box, titulo;
    private ImageButton back, salvar, editar, galeria, tirar, voltar, home, pesquisar, perfil, notificacao;
    private Button deletar;
    private EditText nome, telefone, senha, confirmar;
    private Bitmap bitmap;
    private ProgressBar loading;
    private View layout;
    private CheckBox checkbox;
    boolean up = false;

    @SuppressLint({"ClickableViewAccessibility", "QueryPermissionsNeeded"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setup(this);
        setContentView(R.layout.editarperfil);
        initialize();

        Utils.setupChat(this, findViewById(R.id.editarperfil_layout));

        back.setOnClickListener(v -> this.finish());

        editar.setOnClickListener(v -> {
            if(!up)
                up();
            else
                down();
            loading.setVisibility(View.INVISIBLE);
            loading.setIndeterminate(false);
            editar.setEnabled(true);
        });

        galeria.setOnClickListener(v -> {
            if(!up) return;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        tirar.setOnClickListener(v -> {
            if(!up) return;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        salvar.setOnClickListener(v -> {
            block(true);
            if(up) return;
            if(nome.getText().length() == 0 || telefone.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), "Preencha os campos", Toast.LENGTH_SHORT).show();
                block(false);
                return;
            }

            if(!Utils.isNumber(telefone.getText().toString())) {
                telefone.setError("Digite um número com ddd válido!");
                block(false);
                return;
            }

            String sql = "select (case when exists (select 1 from usuario where telefone = '@telefone' and not email='@email') then 'true' else 'false' end) as telefone;";
            sql = InternalDatabase.convert(sql);
            sql = sql.replace("@telefone", telefone.getText().toString());
            db.requestScript(sql, data -> {
                for (Map<String, String> i : data) {
                    if(i.get("telefone").equals("true")) {
                        telefone.setError("Telefone já cadastrado!");
                        block(false);
                        return;
                    }
                }
                String s = "update usuario set nome='@nome', senha='@senha', telefone='@telefone', imagem='@imagem' where email='@email';";
                s = s.replace("@nome", nome.getText());
                s = s.replace("@telefone", telefone.getText());

                if(bitmap != null) {
                    s = s.replace("@imagem", ImageDatabase.encodeResize(bitmap));
                    ImageDatabase.setPerfil(bitmap);
                } else
                    s = s.replace("@imagem", "imagem");
                if (senha.getText().length() > 0) {
                    if (senha.getText().toString().equals(confirmar.getText().toString())) {
                        s = s.replace("@senha", senha.getText().toString());
                        InternalDatabase.saveUser(getApplicationContext(), nome.getText().toString(), telefone.getText().toString(), InternalDatabase.getEmail(),  senha.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "As senhas devem ser iguais", Toast.LENGTH_SHORT).show();
                        block(false);
                        return;
                    }
                } else
                    InternalDatabase.saveUser(this, nome.getText().toString(), telefone.getText().toString(), InternalDatabase.getEmail(), InternalDatabase.getSenha());
                s = InternalDatabase.convert(s);
                Log.i("SaborearDatabase", s);
                db.sendScript(s);

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(getApplicationContext(), "Perfil atualizado", Toast.LENGTH_SHORT).show();
                block(false);
            });

        });

        deletar.setOnClickListener(v -> {
            if(up) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Você realmente deseja deletar sua conta?")
                    .setTitle("Saborear")
                    .setNegativeButton("Cancelar", (dialog, id) -> {

                    })
                    .setPositiveButton("Sim", (dialog, id) -> {
                        String sql = InternalDatabase.convert("delete from redefinicao where telefone in (select telefone from usuario where email = '@email');delete from comentario where email='@email';delete from usuario_receita where email='@email';delete from salva where email='@email';delete from usuario_notificacao where email='@email';delete from usuario where email='@email';");
                        db.sendScript(sql);
                        InternalDatabase.clear(getApplicationContext());

                        Toast.makeText(getApplicationContext(), "Você deletou sua conta", Toast.LENGTH_SHORT).show();

                        ActivityTracker.finish(this);
                        Intent myIntent = new Intent(getApplicationContext(), Acessar.class);
                        startActivity(myIntent);
                        this.overridePendingTransition(0, 0);
                        this.finish();
                    });
            builder.create().show();
        });

        layout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                if(box.getY() > event.getY()) {
                    down();
                }
            return false;
        });

        voltar.setOnClickListener(v -> this.finish());

        perfil.setOnClickListener(v -> this.finish());

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
        ViewGroup layout = findViewById(R.id.editarperfil_layout);

        ArrayList<View> livre = new ArrayList<>();
        livre.add(galeria); livre.add(tirar);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if(livre.contains(view)) continue;

            view.setEnabled(!block);
        }
        loading.setIndeterminate(block);
        loading.setVisibility(block ? View.VISIBLE : View.INVISIBLE);
    }

    public void initialize() {
        ActivityTracker.addActivity(this);
        db = new Database(this);

        foto = findViewById(R.id.editarperfil_imagem_perfil);
        salvar = findViewById(R.id.perfil_btn_salvar);
        editar = findViewById(R.id.editarperfil_btn_editar);
        nome = findViewById(R.id.perfil_insert_nome);
        telefone = findViewById(R.id.perfil_insert_telefone);
        senha = findViewById(R.id.perfil_insert_senha);
        confirmar = findViewById(R.id.perfil_insert_confirmar);
        deletar = findViewById(R.id.editarperfil_btn_deletar);
        box = findViewById(R.id.editarperfil_box_editar);
        galeria = findViewById(R.id.editarperfil_btn_galeria);
        tirar = findViewById(R.id.editarperfil_btn_tirar);
        voltar = findViewById(R.id.editarperfil_btn_voltar);
        home = findViewById(R.id.editarperfil_btn_home);
        pesquisar = findViewById(R.id.editarperfil_btn_pesquisar);
        perfil = findViewById(R.id.editarperfil_btn_perfil);
        notificacao = findViewById(R.id.editarperfil_btn_notificacao);
        loading = findViewById(R.id.editarperfil_carregar_dados);
        layout = findViewById(R.id.editarperfil_layout);
        checkbox = findViewById(R.id.editarperfil_checkbox);
        titulo = findViewById(R.id.editarperfil_box_titulo);
        back = findViewById(R.id.editarperfil_btn_back);


        if(InternalDatabase.isDarkmode())
            checkbox.setChecked(true);

        checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!InternalDatabase.isDarkmode())
                InternalDatabase.changeDarkmode(getApplicationContext(), true);
            else
                InternalDatabase.changeDarkmode(getApplicationContext(), false);
            this.overridePendingTransition(0, 0);

            ActivityTracker.finish(this);
            Intent myIntent = new Intent(getApplicationContext(), Main.class);
            startActivity(myIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        });

        bitmap = ImageDatabase.getPerfil();
        foto.setImageBitmap(bitmap);
        block(false);

        EditText[] editText = new EditText[]{nome, telefone, senha, confirmar};
        for (EditText text : editText) {
            text.setOnClickListener(v -> {
                if(!up) return;
                down();
            });
        }

        nome.setText(InternalDatabase.getNome());
        telefone.setText(InternalDatabase.getTelefone());

        ArrayList<View> ignore = new ArrayList<>();
        ArrayList<View> inverted = new ArrayList<>();
        ignore.add(foto); ignore.add(salvar); ignore.add(telefone); ignore.add(nome);
        ignore.add(editar); inverted.add(tirar); ignore.add(telefone); ignore.add(senha);
        inverted.add(galeria); ignore.add(confirmar);
        inverted.add(findViewById(R.id.editarperfil_box_titulo));
        Utils.blackMode(findViewById(R.id.editarperfil_layout), ignore, inverted);
    }

    void up() {
        View[] v = new View[]{box, galeria, tirar, titulo};
        for(int i = 0; i<v.length; i++) {
            v[i].setVisibility(View.VISIBLE);
            v[i].setY(Utils.getHeight(this));
            v[i].animate().translationY(10f).setDuration(2000).start();
        }
        up = true;
        block(true);
    }

    void down() {
        View[] v = new View[]{box, galeria, tirar, titulo};
        for(int i = 0; i<v.length; i++) {
            v[i].animate().translationY(1500f).setDuration(2000).start();
        }
        up = false;
        block(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                foto.setImageBitmap(bitmap);
                down();
            } catch (IOException e) {
                Log.i("SaborearDatabase", e.getMessage());
            }

        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            foto.setImageBitmap(bitmap);
            down();
        }
        down();
    }


    @Override
    public void onBackPressed() {
        if(up)
            down();
        else
            this.finish();
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
