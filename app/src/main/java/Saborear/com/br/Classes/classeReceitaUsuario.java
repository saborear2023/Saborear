package Saborear.com.br.Classes;

import android.util.Log;

import java.util.Map;

public class classeReceitaUsuario {
    private int nota;
    private Boolean curtida;

    public classeReceitaUsuario(Integer nota, Boolean curtida) {
        this.nota = nota;
        this.curtida = curtida;
    }

    public classeReceitaUsuario(Map<String, String> map) {
        try {
            this.nota = Integer.parseInt(map.get("nota_usuario"));
            this.curtida = map.get("curtida").equals("true");
        } catch(Exception e) {
            Log.i("SaborearDatabase", "Erro na criação de receitausuario: "+e.getMessage());
        }
    }


    public void setCurtida(Boolean curtida) { this.curtida = curtida; }

    public void setNota(Integer nota) { this.nota = nota; }

    public Boolean getCurtida() { return curtida; }

    public Integer getNota() { return nota; }
}
