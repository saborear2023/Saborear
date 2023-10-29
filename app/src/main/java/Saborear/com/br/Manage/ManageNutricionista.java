package Saborear.com.br.Manage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Saborear.com.br.Classes.classeNutricional;

public class ManageNutricionista {

    private ArrayList<classeNutricional> nutrientes;
    public ManageNutricionista(ArrayList<classeNutricional> nutrientes) {
        if(nutrientes.size() == 0) this.nutrientes = new ArrayList<>();
        else this.nutrientes = new ArrayList<>(nutrientes);
    }
    public ManageNutricionista ordernarByNome() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getNome);
        Collections.sort(nutrientes, comparador);
        return this;
    }
    public ManageNutricionista ordernarBySat() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getSaturadaQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }
    public ManageNutricionista ordernarByTrans() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getTransQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByFibra() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getFibraQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByAcucar() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getAcucarQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByVitaminaD() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getVitaminaDQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByCalcio() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getCalcioQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByFerro() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getFerro);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByPotassio() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getPotassioQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByCalorias() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getCaloriasQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista ordernarByPeso() {
        Comparator<classeNutricional> comparador = Comparator.comparing(classeNutricional::getPesoQuantidade);
        Collections.sort(nutrientes, comparador);
        return this;
    }

    public ManageNutricionista inverter() {
        Collections.reverse(nutrientes);
        return this;
    }

    public ManageNutricionista getByCategoria(ArrayList<String> categorias) {
        ArrayList<classeNutricional> filteredNutrientes = new ArrayList<>();
        for (classeNutricional nutricional : nutrientes) {
            if (categorias.contains(nutricional.getCategoria())) {
                filteredNutrientes.add(nutricional);
            }
        }
        nutrientes = filteredNutrientes;
        return this;
    }

    public ManageNutricionista getByNome(String nome) {
        ArrayList<classeNutricional> filteredNutrientes = new ArrayList<>();
        for (classeNutricional nutriente : nutrientes) {
            if (nutriente.getNome().toLowerCase().contains(nome.toLowerCase())) {
                filteredNutrientes.add(nutriente);
            }
        }
        nutrientes = filteredNutrientes;
        return this;
    }
    public ArrayList<classeNutricional> getNutrientes() {
        return nutrientes;
    }
}
