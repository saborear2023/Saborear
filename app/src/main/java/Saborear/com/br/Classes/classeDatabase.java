package Saborear.com.br.Classes;

import android.util.Log;

import java.util.ArrayList;

import Saborear.com.br.Callback.Callback;

public class classeDatabase {
    private String sql;
    private ArrayList<Callback> callback;
    private boolean ativado;
    public classeDatabase(String sql, ArrayList<Callback> callback, boolean ativado) {
        this.sql = sql;
        this.callback = callback;
        this.ativado = ativado;
    }

    public void show() {
        Log.i("SaborearDatabase", "SQL:" + sql +" - ESTADO: "+ (ativado ? "Ativo" : "Desativado"));
    }

    public ArrayList<Callback> getCallback() {
        return callback;
    }

    public String getSql() {
        return sql;
    }
    public boolean getAtivado() {
        return ativado;
    }

    public void setAtivado(boolean ativado) {
        this.ativado = ativado;
    }

    public void setCallback(ArrayList<Callback> callback) {
        this.callback = callback;
    }

    public void addCallback(Callback callback) {
        this.callback.add(callback);
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
