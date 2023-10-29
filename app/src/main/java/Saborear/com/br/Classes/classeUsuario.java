package Saborear.com.br.Classes;

import android.graphics.Bitmap;

import java.util.Map;

import Saborear.com.br.Database.ImageDatabase;

public class classeUsuario {

    private String email, nome, senha, telefone;
    private Boolean admin;
    private Bitmap imagem;

    public classeUsuario(String email, String nome, String senha, String telefone, Boolean admin, Bitmap imagem) {
        this.email = email;
        this.nome = nome;
        this.senha = senha;
        this.telefone = telefone;
        this.admin = admin;
        this.imagem = imagem;
    }

    public classeUsuario(Map<String, String> map) {
        this.email = map.get("email");
        this.nome = map.get("nome_usuario");
        this.senha = map.get("senha");
        this.telefone = map.get("telefone");
        this.admin = map.get("admin").equals("true");
        this.imagem = ImageDatabase.decode("imagem_usuario");
    }

    public String getEmail() { return email; }

    public String getNome() { return nome; }

    public String getSenha() { return senha; }

    public String getTelefone() { return telefone; }

    public Boolean getAdmin() { return admin; }

    public Bitmap getImagem() { return imagem; }

    public void setEmail(String email) { this.email = email; }

    public void setNome(String nome) { this.nome = nome; }

    public void setSenha(String senha) { this.senha = senha; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public void setAdmin(Boolean admin) { this.admin = admin; }

    public void setImagem(Bitmap imagem) { this.imagem = imagem; }
}
