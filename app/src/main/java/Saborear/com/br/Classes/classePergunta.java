package Saborear.com.br.Classes;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class classePergunta {

    private String idpergunta, pergunta, acao;
    private boolean multiplos;
    private ArrayList<classeAlternativa> alternativas;

    public classePergunta(String idpergunta, String pergunta, String acao, Boolean multiplos, ArrayList<classeAlternativa> alternativas) {
        this.idpergunta = idpergunta;
        this.pergunta = pergunta;
        this.acao = acao;
        this.multiplos = multiplos;
        this.alternativas = alternativas;
    }

    public classePergunta(classePergunta pergunta) {
        setIdpergunta(pergunta.getIdpergunta());
        setPergunta(pergunta.getPergunta());
        setAcao(pergunta.getAcao());
        setMultiplos(pergunta.isMultiplos());
        setAlternativas(pergunta.getAlternativas());
    }

    public classePergunta(Map<String, String> map) {
        try {
            this.idpergunta = map.get("id_pergunta");
            this.pergunta = map.get("pergunta");
            this.acao = map.get("acao");
            this.multiplos = map.get("multiplos").equals("t") ? true : false;

            this.alternativas = new ArrayList<>();

            String[] alternativas = map.get("alternativas").split("//");
            for (String alternativa : alternativas) {
                String[] values = alternativa.split(",,");
                this.alternativas.add(new classeAlternativa(values[0], values[1], values[2], Integer.parseInt(values[3])));
            }

            Comparator<classeAlternativa> comparador = Comparator.comparing(classeAlternativa::getPrioridade);
            Collections.sort(this.alternativas, comparador);

        } catch(Exception e) {
            Log.i("SaborearDatabase", "Erro na criação de receitasubcategoria: "+e.getMessage());
        }
    }

    public void show() {
        Log.i("SaborearDatabase", "Pergunta: "+getPergunta());
        alternativas.forEach(alternativa -> Log.i("SaborearDatabase", "Alternativa: "+alternativa.getAlternativa()));
    }

    public ArrayList<classeAlternativa> getAlternativas() {
        ArrayList<classeAlternativa> nalternativas = new ArrayList<>();
        alternativas.forEach(alternativa -> nalternativas.add(new classeAlternativa(alternativa)));
        return nalternativas;
    }
    public ArrayList<classeAlternativa> getAlternativasReference() { return alternativas; }
    public String getAcao() { return acao; }
    public String getIdpergunta() { return idpergunta; }
    public String getPergunta() { return pergunta; }
    public Boolean getMultiplos() { return multiplos; }
    public Boolean isMultiplos() { return multiplos; }
    public void setAcao(String acao) { this.acao = acao; }
    public void setAlternativas(ArrayList<classeAlternativa> alternativas) { this.alternativas = (ArrayList<classeAlternativa>) alternativas.clone(); }
    public void setIdpergunta(String idpergunta) { this.idpergunta = idpergunta; }
    public void setMultiplos(boolean multiplos) { this.multiplos = multiplos; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }
}
