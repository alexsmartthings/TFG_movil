package com.alex.clientemotorflipside;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class Moto {
    private String id;
    private String id_cliente;
    private String marca, modelo, matricula, color;
    private int kms;
    private String ano;  // <--- VOLVEMOS A STRING

    public Moto() {}

    public Moto(String marca, String modelo, String matricula, int kms, String ano, String color) {
        this.marca = marca;
        this.modelo = modelo;
        this.matricula = matricula;
        this.kms = kms;
        this.ano = ano;
        this.color = color;
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public int getKms() { return kms; }
    public void setKms(int kms) { this.kms = kms; }

    public String getId_cliente() { return id_cliente; }
    public void setId_cliente(String id_cliente) { this.id_cliente = id_cliente; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @PropertyName("ano")
    public String getAno() { return ano; }

    @PropertyName("ano")
    public void setAno(String ano) { this.ano = ano; }
}