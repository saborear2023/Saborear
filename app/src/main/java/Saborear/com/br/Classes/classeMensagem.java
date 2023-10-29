package Saborear.com.br.Classes;

import android.app.Activity;
import android.content.Intent;

import Saborear.com.br.Receita;
import Saborear.com.br.Manage.Manage;

public class classeMensagem {
    private String mensagem;
    private Boolean admin, hasimagem;
    private classeReceita target;
    private classePergunta pergunta;

    public classeMensagem(String mensagem, Boolean admin, Boolean hasimagem, classeReceita target) {
        this.mensagem = mensagem;
        this.admin = admin;
        this.hasimagem = hasimagem;
        this.target = target;
        this.pergunta = null;
    }

    public void openTarget(Activity activity) {
        if(!hasTarget()) return;

        Receita.receita = new classeReceita(Manage.findReceita(getTarget().getIdreceita()));
        Intent myIntent = new Intent(activity, Receita.class);
        activity.startActivityForResult(myIntent, 1);
        activity.overridePendingTransition(0, 0);
    }

    public boolean isAdmin() { return admin; }
    public boolean hasImage() { return hasimagem; }
    public boolean hasTarget() { return target != null ? true : false; }
    public boolean hasPergunta() { return pergunta != null ? true : false;}
    public void setPergunta(classePergunta pergunta) { this.pergunta = pergunta; }

    public void setAdmin(Boolean admin) { this.admin = admin; }

    public void setImagem(Boolean hasimagem) { this.hasimagem = hasimagem; }

    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public void setTarget(classeReceita target) { this.target = target; }

    public Boolean hasImagem() { return hasimagem; }

    public Boolean getAdmin() { return admin; }

    public String getMensagem() { return mensagem; }

    public classeReceita getTarget() { return target; }

    public classePergunta getPergunta() { return pergunta; }
}
