package Saborear.com.br.Manage;

import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Classes.classeTempero;
import Saborear.com.br.Database.StorageDatabase;

public class Manage {

    public static classeReceita findReceita(classeReceita receita) {
        for (classeReceita nreceita : StorageDatabase.receitas) {
            if(!nreceita.getIdreceita().equals(receita.getIdreceita())) continue;
            return nreceita;
        }
        return null;
    }

    public static classeReceita findReceita(String idreceita) {
        for (classeReceita nreceita : StorageDatabase.receitas) {
            if(!nreceita.getIdreceita().equals(idreceita)) continue;
            return nreceita;
        }
        return null;
    }
    public static classeTempero findTempero(String idtempero) {
        for (classeTempero ntempero : StorageDatabase.temperos) {
            if(!ntempero.getIdtempero().equals(idtempero)) continue;
            return ntempero;
        }
        return null;
    }

    public static void deletarReceita(String idreceita) {
        for (int i = 0; i < StorageDatabase.receitas.size(); i++) {
            if(StorageDatabase.receitas.get(i).getIdreceita().equals(idreceita)) {
                StorageDatabase.receitas.remove(i);
            }
        }
    }

    public static void deletarTempero(String idtempero) {
        for (int i = 0; i < StorageDatabase.temperos.size(); i++) {
            if(StorageDatabase.temperos.get(i).getIdtempero().equals(idtempero)) {
                StorageDatabase.temperos.remove(i);
            }
        }
    }

    public static String criarNotificacao(String idnotificacao, String idreceita, String titulo, String mensagem) {
        String sql = "insert into notificacao(id_notificacao, id_receita, titulo, mensagem) values('@id', '@receita', '@titulo', '@mensagem'); select * from receita where id_receita='@receita';";

        sql = sql.replace("@id", ""+idnotificacao);
        sql = sql.replace("@receita", idreceita);
        sql = sql.replace("@titulo", titulo);
        sql = sql.replace("@mensagem", mensagem);

        return sql;
    }

    public static String insertNotificacao(String idnotificacao, String email) {
        String sql = "insert into usuario_notificacao(id_notificacao, email) values('@id', '@email');";

        sql = sql.replace("@email", email);
        sql = sql.replace("@id", ""+idnotificacao);

        return sql;
    }
}
