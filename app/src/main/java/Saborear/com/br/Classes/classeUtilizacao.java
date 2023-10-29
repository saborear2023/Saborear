package Saborear.com.br.Classes;

import android.graphics.Bitmap;

public class classeUtilizacao {
    private final String nome, id_utilizacao;
    private final Bitmap imagem;
    public classeUtilizacao(String id_utilizacao, String nome, Bitmap imagem) {
        this.id_utilizacao = id_utilizacao;
        this.nome = nome;
        this.imagem = imagem;
    }

    public String createScript(String idtempero) {
        String sql = "insert into tempero_utilizacao(id_tempero, id_utilizacao) values(@idtempero, @idutilizacao);";

        sql = sql.replace("@idtempero", idtempero);
        sql = sql.replace("@idutilizacao", id_utilizacao);

        return sql;
    }

    public String getId_utilizacao() { return id_utilizacao; }

    public String getNome() { return nome; }

    public Bitmap getImagem() { return imagem; }

}
