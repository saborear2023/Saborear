package Saborear.com.br.Classes;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import Saborear.com.br.Database.ImageDatabase;
import Saborear.com.br.Database.StorageDatabase;

public class classeReceita {

    private classeCategoriaReceita categoriareceita;

    private classeUsuario criador;
    private classeReceitaUsuario receitausuario;
    private classeReceitaSubcategoria subcategoria;
    private ArrayList<classeIngrediente> ingredientes;
    private String idreceita, nome, descricao;
    private Boolean publica, validar;
    private Bitmap imagem;
    private int views, tempo, comentarios;
    private long criacao;
    private double nota;

    public classeReceita() { }

    public classeReceita(String idreceita, classeCategoriaReceita categoriareceita, ArrayList<classeIngrediente> ingredientes, classeUsuario criador, classeReceitaUsuario receitausuario, classeReceitaSubcategoria subcategoria, String nome, String descricao, Long criacao, int views, int tempo, int comentarios, double nota, Boolean publica, Boolean validar, Bitmap imagem) {
        setIdreceita(idreceita);
        setCategoriareceita(categoriareceita);
        setReceitausuario(receitausuario);
        setSubcategoria(subcategoria);
        setIngredientes(ingredientes);
        setNome(nome);
        setCriador(criador);
        setPublica(publica);
        setImagem(imagem);
        setDescricao(descricao);
        setViews(views);
        setTempo(tempo);
        setNota(nota);
        setComentarios(comentarios);
        setCriacao(criacao);
        setValidar(validar);
    }

    public classeReceita(Map<String, String> map) {
        try {
            setIdreceita(map.get("id_receita"));
            setCategoriareceita(new classeCategoriaReceita(map));
            setReceitausuario(new classeReceitaUsuario(map));
            setSubcategoria(new classeReceitaSubcategoria(map));
            setNome(map.get("nome"));
            setCriador(new classeUsuario(map));
            setPublica(map.get("visibilidade").equals("Pública"));
            setImagem(ImageDatabase.decode(map.get("imagem")));
            setDescricao(map.get("descricao"));
            setViews(Integer.parseInt(map.get("views")));
            setTempo(Integer.parseInt(map.get("tempo")));
            setNota(Double.parseDouble(map.get("nota")));
            setComentarios(Integer.parseInt(map.get("comentarios")));
            setCriacao(Long.parseLong(map.get("criado")));
            setValidar(map.get("validar").toString().equals("t") ? true : false);

            ArrayList<classeIngrediente> ingredientes = new ArrayList<>();

            for (String s : map.get("ingredientes").split("//")) {
                String[] split = s.split(",,");

                classeIngrediente ingred = new classeIngrediente(split[0], new classeCategoriaIngrediente(null, null), split[1], split[2]);
                ingredientes.add(ingred);
            }

            setIngredientes(ingredientes);

        } catch(Exception e) {
            Log.e("SaborearDatabase", "Erro na criação de classe: "+e.getMessage()+" (ID = "+idreceita+")");
        }
    }

    public classeReceita(classeReceita classeReceita) {
        try {
            setIdreceita(classeReceita.getIdreceita());
            setCategoriareceita(classeReceita.getCategoriareceita());
            setReceitausuario(classeReceita.getReceitausuario());
            setSubcategoria(classeReceita.getSubcategoria());
            setIngredientes(classeReceita.getIngredientes());
            setNome(classeReceita.getNome());
            setCriador(classeReceita.getCriador());
            setPublica(classeReceita.getPublica());
            setImagem(classeReceita.getImagem());
            setDescricao(classeReceita.getDescricao());
            setViews(classeReceita.getViews());
            setTempo(classeReceita.getTempo());
            setNota(classeReceita.getNota());
            setComentarios(classeReceita.getComentarios());
            setCriacao(classeReceita.getCriacao());
            setValidar(classeReceita.getValidar());
        } catch(Exception e) {
            Log.e("SaborearDatabase", "Erro na criação de classe: "+e.getMessage());
        }
    }

    public void update(classeReceita classeReceita) {
        try {
            setIdreceita(classeReceita.getIdreceita());
            setCategoriareceita(classeReceita.getCategoriareceita());
            setReceitausuario(classeReceita.getReceitausuario());
            setSubcategoria(classeReceita.getSubcategoria());
            setIngredientes(classeReceita.getIngredientes());
            setNome(classeReceita.getNome());
            setCriador(classeReceita.getCriador());
            setPublica(classeReceita.getPublica());
            setImagem(classeReceita.getImagem());
            setDescricao(classeReceita.getDescricao());
            setViews(classeReceita.getViews());
            setTempo(classeReceita.getTempo());
            setNota(classeReceita.getNota());
            setComentarios(classeReceita.getComentarios());
            setValidar(classeReceita.getValidar());
        } catch(Exception e) {
            Log.e("SaborearDatabase", "Erro na criação de classe: "+e.getMessage());
        }
    }

    public void show() {
        Log.i("SaborearDatabase", "ID Receita: " + idreceita);
        Log.i("SaborearDatabase", "Categoria Receita: " + categoriareceita.getNome());
        Log.i("SaborearDatabase", "ID Categoria Receita: " + categoriareceita.getIdcatreceita());
        Log.i("SaborearDatabase", "Avaliação do Usuário: " + receitausuario.getNota());
        Log.i("SaborearDatabase", "Subcategorias: " + subcategoria.getNomes());
        Log.i("SaborearDatabase", "Nome: " + nome);
        Log.i("SaborearDatabase", "Descrição: " + descricao);
        Log.i("SaborearDatabase", "Criador: " + criador.getNome());
        Log.i("SaborearDatabase", "É pública: " + (publica ? "Sim" : "Não"));
        Log.i("SaborearDatabase", "Views: " + views);
        Log.i("SaborearDatabase", "Tempo: " + tempo);
        Log.i("SaborearDatabase", "Nota: " + nota);
        Log.i("SaborearDatabase", "Comentários: " + comentarios);
        Log.i("SaborearDatabase", "Ingredientes:");

        for (classeIngrediente ingrediente : ingredientes) {
            ingrediente.show();
        }
    }

    public String listarIngredientes() {
        String ingred = "";
        for (int i = 0; i < getIngredientes().size(); i++) {
            classeIngrediente ingrediente = getIngredientes().get(i);

            String aux = "";
            if(ingrediente.getMedida().equals("xícara")) aux = " ("+(Math.round(240*ingrediente.getQuantidade()))+"ml)";
            if(ingrediente.getMedida().equals("colher de sopa")) aux = " ("+(Math.round(15*ingrediente.getQuantidade()))+"ml)";
            if(ingrediente.getMedida().equals("colher de sobremesa")) aux = " ("+(Math.round(10*ingrediente.getQuantidade()))+"ml)";
            if(ingrediente.getMedida().equals("lata")) aux = " ("+(Math.round(350*ingrediente.getQuantidade()))+"ml)";

            ingred += ingrediente.getQuantidadeString()+" "+ingrediente.getMedida()+" de " +ingrediente.getNome().toLowerCase()+aux;
            if(i + 1 < getIngredientes().size()) ingred += "\n";

        }
        return ingred;
    }
    public String scriptSubcategoria() {
        try {
            String sql = "delete from receita_subcateg where id_receita='@idreceita';";

            if(subcategoria.getSubcategorias().size() > 0) {
                sql += "select adicionar_subcateg(@idreceita, array@array);";
                String array = "[";
                for (int i = 0; i < subcategoria.getSubcategorias().size(); i++) {
                    array += "'" + subcategoria.getSubcategorias().get(i) + "'";
                    if (i + 1 < subcategoria.getSubcategorias().size()) array += ",";
                }
                array += "]";

                sql = sql.replace("@array", "" + array);
            }

            sql = sql.replace("@idreceita", getIdreceita());
            return sql;

        } catch (Exception e) {
            return "";
        }
    }


    public String scriptDeletar() {
        String sql = "delete from receita_subcateg where id_receita='@id';delete from notificacao where id_receita='@id'; delete from salva where id_receita='@id';delete from usuario_receita where id_receita='@id';delete from comentario where id_receita='@id';delete from receita_ingred where id_receita='@id';delete from receita where id_receita='@id';";
        sql = sql.replace("@id", idreceita);
        return sql;
    }

    public String scriptCriar() {
        String sql = "insert into receita(id_receita, nome, id_cat_receita, descricao, tempo, email, visibilidade, imagem) values('@receita', '@nome', '@id', '@desc', '@tempo', '@email', '@visibilidade', '@imagem');";
        sql = sql.replace("@nome", nome);
        sql = sql.replace("@id", categoriareceita.getIdcatreceita());
        sql = sql.replace("@desc", descricao);
        sql = sql.replace("@tempo", ""+tempo);
        sql = sql.replace("@email", criador.getEmail());
        sql = sql.replace("@visibilidade", publica ? "Pública" : "Privada");
        sql = sql.replace("@imagem", ImageDatabase.encode(imagem));

        for (classeIngrediente ingrediente : ingredientes) {
            sql += ingrediente.addScript(""+idreceita);
        }

        sql = sql.replace("@receita", idreceita);
        return sql;
    }

    public String scriptAtualizar() {
        String sql = "update receita set nome = '@nome', id_cat_receita = '@cat', descricao = '@desc', tempo = '@tempo', visibilidade = '@visibilidade', imagem='@imagem' where id_receita = '@id';";
        sql = sql.replace("@nome", nome);
        sql = sql.replace("@cat", categoriareceita.getIdcatreceita());
        sql = sql.replace("@desc", descricao);
        sql = sql.replace("@tempo", ""+tempo);
        sql = sql.replace("@visibilidade", publica ? "Pública" : "Privada");
        sql = sql.replace("@imagem", ImageDatabase.encode(imagem));

        sql += "delete from receita_ingred where id_receita='@id';";
        for (classeIngrediente ingrediente : ingredientes) {
            sql += ingrediente.addScript(""+idreceita);
        }

        sql = sql.replace("@id", idreceita);
        return sql;
    }

    public classeUsuario getCriador() { return criador; }
    public classeCategoriaReceita getCategoriareceita() {
        classeCategoriaReceita classeCategoriaReceita = new classeCategoriaReceita(categoriareceita.getIdcatreceita(), categoriareceita.getNome());
        return classeCategoriaReceita;
    }

    public classeNutricional getNutricional() {
        double peso = 0;
        double calorias = 0;
        double saturada = 0;
        double trans = 0;
        double fibra = 0;
        double acucar = 0;
        double potassio = 0;
        double ferro = 0;
        double calcio = 0;
        double vitaminad = 0;

        for (int i = 0; i < getIngredientes().size(); i++) {
            classeIngrediente ingred = getIngredientes().get(i);
            if(StorageDatabase.nutricional.containsKey(ingred.getNome())) {
                try {
                    classeNutricional nutri = StorageDatabase.nutricional.get(ingred.getNome());
                    peso += Double.parseDouble(nutri.getPeso().replace("g", ""));
                    calorias += Double.parseDouble(nutri.getCalorias().replace("kcal", ""));
                    saturada += Double.parseDouble(nutri.getSaturada().replace("g", ""));
                    trans += Double.parseDouble(nutri.getTrans().replace("g", ""));
                    fibra += Double.parseDouble(nutri.getFibra().replace("g", ""));
                    acucar += Double.parseDouble(nutri.getAcucar().replace("g", ""));
                    potassio += Double.parseDouble(nutri.getPotassio().replace("mg", ""));
                    ferro += Double.parseDouble(nutri.getFerro().replace("mg", ""));
                    calcio += Double.parseDouble(nutri.getCalcio().replace("mg", ""));
                    vitaminad += Double.parseDouble(nutri.getVitaminad().replace("µg", ""));
                } catch (Exception e) {
                    Log.i("SaborearDatabase", e.getMessage());
                }
            }
        }

        calorias = calorias > 1000 ? calorias/2 : calorias;

        classeNutricional nutriente = new classeNutricional();
        nutriente.setPeso(String.format("%.2f", peso));
        nutriente.setCalorias(String.format("%.2f", calorias));
        nutriente.setSaturada(String.format("%.2f", saturada));
        nutriente.setTrans(String.format("%.2f", trans));
        nutriente.setFibra(String.format("%.2f", fibra));
        nutriente.setAcucar(String.format("%.2f", acucar));
        nutriente.setPotassio(String.format("%.2f", potassio));
        nutriente.setFerro(String.format("%.2f", ferro));
        nutriente.setCalcio(String.format("%.2f", calcio));
        nutriente.setVitaminad(String.format("%.2f", vitaminad));

        return nutriente;
    }

    public double getCusto() {
        double custototal = 0;
        for (classeIngrediente ingrediente : getIngredientes()) {
            if(!StorageDatabase.market.containsKey(ingrediente.getNome())) continue;

            double custo = -1;
            for (int i = 0; i < StorageDatabase.market.get(ingrediente.getNome()).size(); i++) {
                classeMarket market = StorageDatabase.market.get(ingrediente.getNome()).get(i);
                try {
                    double val = market.getCusto();
                    if(val == 0) continue;

                    if (custo == -1) custo = val;
                    else custo = Math.min(custo, val);
                } catch (Exception e) { Log.i("SaborearDatabase", "Erro ao processar market: "+e.getMessage()); }
            }
            if(custo > 0)  custototal += custo;
        }
        return custototal;
    }

    public double getCalorias() { return getNutricional().getCaloriasQuantidade(); }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }

    public String getIdreceita() { return idreceita; }
    public classeReceitaSubcategoria getSubcategoria() { return subcategoria; }

    public Bitmap getImagem() { return imagem; }
    public Boolean getPublica() { return publica; }
    public int getTempo() { return tempo; }
    public int getIngredientesSize() { return getIngredientes() == null ? 0 :  getIngredientes().size(); }

    public int getViews() { return views; }
    public double getNota() { return nota; }

    public void setCriacao(long criacao) { this.criacao = criacao; }
    public long getCriacao() { return criacao; }

    public void setValidar(Boolean validar) { this.validar = validar; }
    public boolean getValidar() { return validar; }

    public ArrayList<classeIngrediente> getIngredientes() {
        ArrayList<classeIngrediente> ningredientes = new ArrayList<>();
        ingredientes.forEach(ingrediente -> ningredientes.add(new classeIngrediente(ingrediente)));
        return ningredientes;
    }
    public void addIngrediente(classeIngrediente ingrediente) { ingredientes.add(new classeIngrediente(ingrediente)); }

    public void removeIngrediente(int pos) { ingredientes.remove(pos); }
    public classeReceitaUsuario getReceitausuario() { return receitausuario; }

    public int getComentarios() { return comentarios; }

    public void setNome(String nome) { this.nome = nome;}
    public void setCategoriareceita(classeCategoriaReceita categoriareceita) { this.categoriareceita = categoriareceita; }

    public void setCriador(classeUsuario criador) { this.criador = criador; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public void setIdreceita(String idreceita) { this.idreceita = idreceita; }

    public void setImagem(Bitmap imagem) { this.imagem = imagem; }

    public void setPublica(Boolean publica) { this.publica = publica; }

    public void setTempo(int tempo) { this.tempo = tempo; }

    public void setViews(int views) { this.views = views; }

    public void setComentarios(int comentarios) { this.comentarios = comentarios; }

    public void setIngredientes(ArrayList<classeIngrediente> copy)
    {
        ingredientes = new ArrayList<>();

        if(copy == null) return;

        for (classeIngrediente ingrediente : copy)
            ingredientes.add(new classeIngrediente(ingrediente));
    }

    public void setReceitausuario(classeReceitaUsuario receitausuario) { this.receitausuario = receitausuario; }

    public void setNota(double nota) { this.nota = nota; }

    public void setSubcategoria(classeReceitaSubcategoria subcategoria) {this.subcategoria = subcategoria; }
}



