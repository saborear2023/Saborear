package Saborear.com.br.Database;

import android.Manifest;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Saborear.com.br.Classes.classeMarket;
import Saborear.com.br.Classes.classeNutricional;
import Saborear.com.br.Classes.classePergunta;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Classes.classeTempero;

public class StorageDatabase {
    private static final String scriptSearch = "SELECT r.id_receita, r.id_cat_receita, r.nome, r.descricao, r.views, r.tempo, r.email, r.visibilidade, r.validar, COALESCE(EXTRACT(EPOCH FROM criacao)::bigint, 0) as criado, COALESCE((SELECT STRING_AGG(rs.id_acao, '//' ORDER BY rs.id_acao) FROM receita_subcateg rs WHERE rs.id_receita = r.id_receita), '') AS subcategorias, CASE WHEN EXISTS (SELECT 1 FROM salva WHERE id_receita = r.id_receita AND email = '@email') THEN 'true' ELSE 'false' END AS curtida, ROUND(COALESCE((SELECT AVG(nota) FROM usuario_receita WHERE id_receita = r.id_receita), 0), 2) AS nota, COALESCE((SELECT COUNT(*) FROM comentario WHERE id_receita = r.id_receita), 0) AS comentarios, (SELECT COUNT(*) FROM receita) AS tamanho, u.nome AS nome_usuario, COALESCE((SELECT nota FROM usuario_receita WHERE id_receita = r.id_receita AND email = '@email'), 0) AS notausuario, COALESCE((SELECT nota FROM usuario_receita WHERE id_receita = r.id_receita AND email = '@email'), 0) AS nota_usuario, COALESCE((SELECT admin FROM usuario WHERE email='@email'), 'false') as admin, (SELECT STRING_AGG(CONCAT(ri.id_ingrediente, ',,', i.nome, ',,', ri.quantidade), '//') FROM receita_ingred ri INNER JOIN ingrediente i ON ri.id_ingrediente = i.id_ingrediente WHERE ri.id_receita = r.id_receita) AS ingredientes, cr.nome as nome_categ_receita, cr.id_cat_receita as id_categ_receita FROM receita r LEFT JOIN usuario u ON r.email = u.email LEFT JOIN categ_receita cr ON r.id_cat_receita = cr.id_cat_receita ";
    private static final String scriptSearchAll = "SELECT r.*, COALESCE(EXTRACT(EPOCH FROM criacao)::bigint, 0) as criado, COALESCE((SELECT STRING_AGG(rs.id_acao, '//' ORDER BY rs.id_acao) FROM receita_subcateg rs WHERE rs.id_receita = r.id_receita), '') AS subcategorias, CASE WHEN EXISTS (SELECT 1 FROM salva WHERE id_receita = r.id_receita AND email = '@email') THEN 'true' ELSE 'false' END AS curtida, ROUND(COALESCE((SELECT AVG(nota) FROM usuario_receita WHERE id_receita = r.id_receita), 0), 2) AS nota, COALESCE((SELECT COUNT(*) FROM comentario WHERE id_receita = r.id_receita), 0) AS comentarios, (SELECT COUNT(*) FROM receita) AS tamanho, u.nome AS nome_usuario, COALESCE((SELECT nota FROM usuario_receita WHERE id_receita = r.id_receita AND email = '@email'), 0) AS notausuario, COALESCE((SELECT nota FROM usuario_receita WHERE id_receita = r.id_receita AND email = '@email'), 0) AS nota_usuario, COALESCE((SELECT admin FROM usuario WHERE email='@email'), 'false') as admin, (SELECT STRING_AGG(CONCAT(ri.id_ingrediente, ',,', i.nome, ',,', ri.quantidade), '//') FROM receita_ingred ri INNER JOIN ingrediente i ON ri.id_ingrediente = i.id_ingrediente WHERE ri.id_receita = r.id_receita) AS ingredientes, cr.nome as nome_categ_receita, cr.id_cat_receita as id_categ_receita FROM receita r LEFT JOIN usuario u ON r.email = u.email LEFT JOIN categ_receita cr ON r.id_cat_receita = cr.id_cat_receita ";
    public static final String versao = "5.0";
    public static final String URL = "https://projetoscti.com.br/projetoscti32/";
    public static final String[] permissoes = new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.SEND_SMS};
    public static final int chatperguntas = 8;
    public static float scale;

    public static ArrayList<classeReceita> receitas;
    public static ArrayList<classeTempero> temperos;
    public static ArrayList<classePergunta> perguntas, perguntasdefinicao;
    public static Map<String, String> util = new HashMap<>();
    public static Map<String, classeNutricional> nutricional;
    public static Map<String, ArrayList<classeMarket>> market;

    public static ArrayList<String> nomes, nomescategoria, nomescategingred;

    public static Map<String, String> categ, idingrediente, nomesingred;

    public static Bitmap imagem;

    public static boolean isAdmin = false, atualizarPesquisar = false;
    public static classePergunta findPergunta(String idpergunta) {
        for (int i = 0; i < perguntas.size(); i++) {
            Log.i("SaborearDatabase", perguntas.get(i).getIdpergunta());
            if(perguntas.get(i).getIdpergunta().equals(idpergunta))  return perguntas.get(i);
        }
        return null;
    }

    public static String getScript() {
        String sql = scriptSearch;

        if(InternalDatabase.getEmail() == null)
            return sql;
        else
            return sql.replace("@email", InternalDatabase.getEmail());
    }

    public static String getScriptAll() {
        String sql = scriptSearchAll;

        if(InternalDatabase.getEmail() == null)
            return sql;
        else
            return sql.replace("@email", InternalDatabase.getEmail());
    }
}

