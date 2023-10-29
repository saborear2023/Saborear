package Saborear.com.br.Database;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Objects;

public class InternalDatabase {

    private static final String nomeArquivo = "dados.dat";
    private static Database db;
    public static boolean firstTime = false;
    public static HashMap<String, String> hashMap = new HashMap<>();

    public static void start(Context context) {
        db = new Database(context);
        File arquivo = new File(context.getFilesDir(), nomeArquivo);

        if (!arquivo.exists()) {
            try {
                if (arquivo.createNewFile()) {
                    firstTime = true;
                    hashMap.put("email", "");
                    hashMap.put("senha", "");
                    hashMap.put("nome", "");
                    hashMap.put("telefone", "");
                    hashMap.put("darkmode", "false");
                    save(context);
                }
            } catch (IOException e) {
                Log.e("SaborearDatabase", "InternalDatabase: "+e.getMessage());
            }
        } else {
            try {
                FileInputStream fis = context.openFileInput("dados.dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                hashMap = (HashMap<String, String>) ois.readObject();
                ois.close();
                fis.close();
            } catch (IOException | ClassNotFoundException e) {
                Log.e("SaborearDatabase", "InternalDatabase: "+e.getMessage());
            }

        }
    }
    public static void save(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(nomeArquivo, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hashMap);
            fos.flush();
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.e("SaborearDatabase", "Erro: "+e.getMessage());
        }
    }


    public static String convert(String script) {
        script = script.replace("@email", Objects.requireNonNull(hashMap.get("email")));
        script = script.replace("@senha", Objects.requireNonNull(hashMap.get("senha")));
        return script;
    }
    public static void saveUser(Context context, String nome, String telefone, String email, String senha) {
        hashMap.put("nome", nome);
        hashMap.put("telefone", telefone);
        hashMap.put("email", email);
        hashMap.put("senha", senha);
        save(context);
    }

    public static void clear(Context context) {
        hashMap.put("nome", "");
        hashMap.put("telefone", "");
        hashMap.put("email", "");
        hashMap.put("senha", "");
        hashMap.put("darkmode", "");
        save(context);
    }

    public static String getEmail() { return hashMap.get("email"); };

    public static String getSenha() { return hashMap.get("senha"); };

    public static String getNome() { return hashMap.get("nome"); };

    public static String getTelefone() { return hashMap.get("telefone"); };
    public static boolean isDarkmode() {
        return hashMap.get("darkmode").equals("true");
    }
    public static void changeDarkmode(Context context, boolean active) {
        InternalDatabase.hashMap.put("darkmode", active ? "true" : "false");
        save(context);
    }
}
