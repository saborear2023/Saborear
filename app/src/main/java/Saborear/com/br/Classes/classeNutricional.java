package Saborear.com.br.Classes;

import android.util.Log;

public class classeNutricional {

    String nome, peso, calorias, saturada, trans, fibra, acucar, potassio, ferro, calcio, vitaminad, categoria;
    public classeNutricional() {
        setNome("");
        setPeso("");
        setCalorias("");
        setSaturada("");
        setTrans("");
        setFibra("");
        setAcucar("");
        setPotassio("");
        setFerro("");
        setCalcio("");
        setVitaminad("");
        setCategoria("");
    }

    public void show() {
        Log.i("SaborearNutricional", "Exibindo: "+getNome());
        Log.i("SaborearNutricional", "Categoria: "+getCategoria());
        Log.i("SaborearNutricional", "Peso: "+getPeso());
        Log.i("SaborearNutricional", "Cálcio: "+getCalcio());
        Log.i("SaborearNutricional", "Gordura saturada: "+getSaturada());
        Log.i("SaborearNutricional", "Gordura trans: "+getTrans());
        Log.i("SaborearNutricional", "Fibras: "+getFibra());
        Log.i("SaborearNutricional", "Açúcar: "+getAcucar());
        Log.i("SaborearNutricional", "Potássio: "+getPotassio());
        Log.i("SaborearNutricional", "Ferro: "+getFerro());
        Log.i("SaborearNutricional", "Cálcio: "+getCalcio());
        Log.i("SaborearNutricional", "Vitamina D: "+getVitaminad());
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setAcucar(String acucar) { this.acucar = acucar.replace(",", "."); }
    public void setCalcio(String calcio) { this.calcio = calcio.replace(",", "."); }
    public void setFerro(String ferro) {
        this.ferro = ferro.replace(",", ".");
    }
    public void setCalorias(String calorias) { this.calorias = calorias.replace(",", "."); }
    public void setFibra(String fibra) { this.fibra = fibra.replace(",", "."); }
    public void setPeso(String peso) { this.peso = peso.replace(",", "."); }
    public void setSaturada(String saturada) { this.saturada = saturada.replace(",", "."); }
    public void setTrans(String trans) { this.trans = trans.replace(",", "."); }
    public void setPotassio(String potassio) { this.potassio = potassio.replace(",", "."); }
    public void setVitaminad(String vitaminad) { this.vitaminad = vitaminad.replace(",", "."); }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNome() {
        return nome;
    }

    public String getAcucar() {
        return acucar;
    }
    public double getAcucarQuantidade() {return Double.parseDouble(acucar.replace("g", ""));}

    public String getCalcio() {
        return calcio;
    }
    public double getCalcioQuantidade() {return Double.parseDouble(calcio.replace("mg", "")); }

    public String getCalorias() {
        return calorias;
    }

    public double getCaloriasQuantidade() {return Double.parseDouble(calorias.replace("kcal", "")); }

    public String getFerro() {
        return ferro;
    }

    public double getFerroQuantidade() {return Double.parseDouble(ferro.replace("mg", "")); }

    public String getFibra() {
        return fibra;
    }

    public double getFibraQuantidade() {return Double.parseDouble(fibra.replace("g", "")); }

    public String getPeso() {
        return peso;
    }
    public double getPesoQuantidade() {return Double.parseDouble(peso.replace("g", "")); }

    public String getPotassio() {
        return potassio;
    }

    public double getPotassioQuantidade() {return Double.parseDouble(potassio.replace("mg", "")); }

    public String getSaturada() {
        return saturada;
    }

    public double getSaturadaQuantidade() {return Double.parseDouble(saturada.replace("g", "")); }

    public String getTrans() {
        return trans;
    }

    public double getTransQuantidade() {return Double.parseDouble(trans.replace("g", "")); }
    public String getVitaminad() {
        return vitaminad;
    }
    public double getVitaminaDQuantidade() {return Double.parseDouble(vitaminad.replace("µg", "")); }
}
