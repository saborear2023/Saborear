package Saborear.com.br.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.R;
import Saborear.com.br.Utils.Utils;

public class classeTempero {

    private String idtempero;
    private String nome, descricao;
    private Bitmap bitmap;
    private ArrayList<classeUtilizacao> utilizacoes;
    public classeTempero(String idTempero, String nome, String descricao, Bitmap bitmap, ArrayList<classeUtilizacao> utilizacoes) {
        Random random = new Random();

        this.idtempero = idTempero.equals("-1") ? ""+random.nextInt(99010) + 500 : idTempero;
        this.nome = nome;
        this.descricao = descricao;
        this.bitmap = bitmap;
        this.utilizacoes = (ArrayList<classeUtilizacao>) utilizacoes.clone();
    }

    public classeTempero(classeTempero temp) {
        this.idtempero = temp.getIdtempero();
        this.nome = temp.getNome();
        this.descricao = temp.getDescricao();
        this.bitmap = temp.getBitmap();
        this.utilizacoes = (ArrayList<classeUtilizacao>) temp.getUtilizacoes().clone();
    }

    public classeTempero(Context context, Map<String, String> map) {
        this.idtempero = map.get("id_tempero");
        this.nome = map.get("nome");
        this.descricao = map.get("descricao");

        this.bitmap = ImageDatabase.decode(map.get("imagem"));
        this.utilizacoes = new ArrayList<>();

        int[] ids = new int[]{R.drawable.saborear_utilizacao_00, R.drawable.saborear_utilizacao_01, R.drawable.saborear_utilizacao_02, R.drawable.saborear_utilizacao_03, R.drawable.saborear_utilizacao_04, R.drawable.saborear_utilizacao_05, R.drawable.saborear_utilizacao_06, R.drawable.saborear_utilizacao_07, R.drawable.saborear_utilizacao_08, R.drawable.saborear_utilizacao_09};
        ArrayList<String> ar = new ArrayList<>(Arrays.asList(map.get("utilizacoes").split(",,")));
        for (String s : ar) {
            String[] split = s.split("/");
            utilizacoes.add(new classeUtilizacao(split[0], split[1], Utils.drawableToBitmap(context, ids[Integer.parseInt(split[0])])));
        }
    }
    public void update(classeTempero classeTempero) {
        try {
            setIdtempero(classeTempero.getIdtempero());
            setNome(classeTempero.getNome());
            setDescricao(classeTempero.getDescricao());
            setBitmap(classeTempero.getBitmap());
            setUtilizacoes(classeTempero.getUtilizacoes());
        } catch(Exception e) {
            Log.e("SaborearDatabase", "Erro na criação de classe: "+e.getMessage());
        }
    }
    public String deleteScript() {
        return "delete from tempero_utilizacao where id_tempero='@id'; delete from tempero where id_tempero='@id';".replace("@id", ""+idtempero);
    }

    public String createScript() {
        String sql =  "insert into tempero(id_tempero, nome, descricao, imagem) values('@id', '@nome', '@desc', '@imagem');";

        sql = sql.replace("@id", idtempero);
        sql = sql.replace("@nome", nome);
        sql = sql.replace("@desc", descricao);
        sql = sql.replace("@imagem", ImageDatabase.encode(bitmap));

        return sql;
    }

    public String utilizacoesScript() {
        String sql = "";

        for (classeUtilizacao utilizacao : utilizacoes)
            sql += utilizacao.createScript(idtempero);
        sql = sql.replace("@idtempero", idtempero);
        return sql;
    }

    public void setIdtempero(String idtempero) { this.idtempero = idtempero; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public void setNome(String nome) { this.nome = nome; }

    public void setUtilizacoes(ArrayList<classeUtilizacao> utilizacoes) { this.utilizacoes = utilizacoes; }

    public String getIdtempero() { return idtempero; }
    public String getDescricao() { return descricao; }

    public String getNome() { return nome; }

    public Bitmap getBitmap() { return bitmap; }

    public void setBitmap(Bitmap imagem) { this.bitmap = imagem; }
    public ArrayList<classeUtilizacao> getUtilizacoes() { return utilizacoes; }

}
