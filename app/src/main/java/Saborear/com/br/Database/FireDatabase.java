package Saborear.com.br.Database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireDatabase {

    private static FirebaseDatabase database;
    private static DatabaseReference updateRef;

    public static void start() {
        database =  FirebaseDatabase.getInstance();
        updateRef = database.getReference().child("update");
    }

    public static void updateReceita(String id, String acao) {
        updateRef = database.getReference().child("update");
        updateRef.setValue(id+"//"+acao).addOnFailureListener(e -> Log.e("SaborearDatabase", "Erro ao atualizar firebase: " + e.getMessage()));
    }

    public static void updateTempero(String id, String acao) {
        updateRef = database.getReference().child("update-temperos");
        updateRef.setValue(id+"//"+acao).addOnFailureListener(e -> Log.e("SaborearDatabase", "Erro ao atualizar firebase: " + e.getMessage()));
    }
}
