package Saborear.com.br.Manage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Saborear.com.br.Classes.classeIngrediente;
import Saborear.com.br.Classes.classeReceita;
import Saborear.com.br.Database.InternalDatabase;

public class ManageReceita {

    private ArrayList<classeReceita> receitas;

    public ManageReceita(ArrayList<classeReceita> receitas) {
        if(receitas.size() == 0) this.receitas = new ArrayList<>();
        else this.receitas = new ArrayList<>(receitas);
    }

    public ManageReceita getByIDCATEG(String idcateg) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (receita.getCategoriareceita().getIdcatreceita().equals(idcateg)) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getByIDCATEG(ArrayList<String> idcateg) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (idcateg.contains(receita.getCategoriareceita().getIdcatreceita())) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getByNome(String nome) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (receita.getNome().toLowerCase().contains(nome.toLowerCase())) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }
    public ManageReceita getByOwner(String nome) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (receita.getCriador().getNome().equals(nome)) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getByLike() {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (receita.getReceitausuario().getCurtida()) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getByTempo(int min, int max) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (min < receita.getTempo() && receita.getTempo() < max) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getBySubcategoria(String subcategoria) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (receita.getSubcategoria().contains(subcategoria)) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getByExceptSubcategoria(String subcategoria) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if (!receita.getSubcategoria().contains(subcategoria)) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        receitas = filteredReceitas;
        return this;
    }
    public ManageReceita getBySubcategoria(ArrayList<String> subcategoria, int minOcurrencies) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            int count = 0;
            for (String s : subcategoria) {
                if(!receita.getSubcategoria().contains(s)) continue;
                if(++count != minOcurrencies) continue;

                filteredReceitas.add(new classeReceita(receita));
                break;
            }
        }
        receitas = filteredReceitas;
        return this;
    }
    public ManageReceita getByIngrediente(String nome) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            for (classeIngrediente ingrediente : receita.getIngredientes()) {
                if(!ingrediente.getNome().equals(nome)) continue;
                filteredReceitas.add(new classeReceita(receita));
                break;
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getByAllOpcIngredients(ArrayList<String> nomes) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            for (classeIngrediente ingrediente : receita.getIngredientes()) {
                if(!nomes.contains(ingrediente.getNome())) continue;
                filteredReceitas.add(new classeReceita(receita));
                break;
            }
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita getByAllObrigIngredients(ArrayList<String> nomes) {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            ArrayList<String> find = new ArrayList<>();
            for (classeIngrediente ingrediente : receita.getIngredientes()) {
                if(!nomes.contains(ingrediente.getNome())) continue;
                find.add(ingrediente.getNome());
            }
            if(find.size() == nomes.size()) filteredReceitas.add(new classeReceita(receita));
        }
        receitas = filteredReceitas;
        return this;
    }

    public ManageReceita limitar(int max) {
        if (max >= receitas.size()) { return this; }
        receitas = new ArrayList<>(receitas.subList(0, max));
        return this;
    }
    public ManageReceita inverter() {
        Collections.reverse(receitas);
        return this;
    }
    public ManageReceita randomizar() {
        Collections.shuffle(receitas);
        return this;
    }
    public ManageReceita ordenarByID() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getIdreceita);
        Collections.sort(receitas, comparador);
        return this;
    }
    public ManageReceita ordenarByNome() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getNome);
        Collections.sort(receitas, comparador);
        return this;
    }
    public ManageReceita ordenarByNota() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getNota);
        Collections.sort(receitas, comparador);
        return this;
    }
    public ManageReceita ordenarByViews() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getViews);
        Collections.sort(receitas, comparador);
        return this;
    }
    public ManageReceita ordenarByComentario() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getComentarios);
        Collections.sort(receitas, comparador);
        return this;
    }

    public ManageReceita ordenarByTempo() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getTempo);
        Collections.sort(receitas, comparador);
        return this;
    }

    public ManageReceita ordenarbyCriacao() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getCriacao);
        Collections.sort(receitas, comparador);
        return this;
    }

    public ManageReceita ordenarByCalorias() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getCalorias);
        Collections.sort(receitas, comparador);
        return this;
    }

    public ManageReceita ordenarByNumIngredientes() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getIngredientesSize);
        Collections.sort(receitas, comparador);
        return this;
    }

    public ManageReceita ordenarByCusto() {
        Comparator<classeReceita> comparador = Comparator.comparing(classeReceita::getCusto);
        Collections.sort(receitas, comparador);

        return this;
    }

    public ArrayList<classeReceita> getReceitas() {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if(receita.getValidar() || receita.getIdreceita().equals("-1")) continue;
            if (receita.getPublica() || receita.getCriador().getEmail().equals(InternalDatabase.getEmail())) {
                filteredReceitas.add(new classeReceita(receita));
            }
        }
        return filteredReceitas;
    }

    public ArrayList<classeReceita> getReceitasValidar() {
        ArrayList<classeReceita> filteredReceitas = new ArrayList<>();
        for (classeReceita receita : receitas) {
            if(!receita.getValidar() || receita.getIdreceita().equals("-1")) continue;
            filteredReceitas.add(new classeReceita(receita));
        }
        return filteredReceitas;
    }
}
