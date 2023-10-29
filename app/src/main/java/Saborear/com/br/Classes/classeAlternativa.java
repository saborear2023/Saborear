package Saborear.com.br.Classes;

public class classeAlternativa {

    private String idpergunta, alternativa, idacao;
    int prioridade;
    private boolean block, select;

    public classeAlternativa(String idpergunta, String alternativa, String idacao, int prioridade) {
        this.idpergunta = idpergunta;
        this.alternativa = alternativa;
        this.idacao = idacao;
        this.prioridade = prioridade;
        this.block = false;
        this.select = false;
    }

    public classeAlternativa(classeAlternativa alternativa) {
        setIdpergunta(alternativa.getIdpergunta());
        setAlternativa(alternativa.getAlternativa());
        setIdacao(alternativa.getIdacao());
        setPrioridade(alternativa.getPrioridade());
        setBlock(alternativa.getBlock());
        setSelect(alternativa.getSelected());
    }

    public void setIdpergunta(String idpergunta) { this.idpergunta = idpergunta; }
    public void setAlternativa(String alternativa) { this.alternativa = alternativa; }
    public void setIdacao(String idacao) { this.idacao = idacao; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }
    public void setBlock(boolean block) { this.block = block; }
    public void setSelect(boolean select) { this.select = select; }

    public String getIdpergunta() { return idpergunta; }
    public String getAlternativa() { return alternativa; }
    public String getIdacao() {return idacao; }
    public int getPrioridade() { return prioridade; }

    public Boolean getBlock() {return block; }
    public Boolean getSelected() {return select; }

    public Boolean isBlock() {return block; }
    public Boolean isSelected() {return select; }

}
