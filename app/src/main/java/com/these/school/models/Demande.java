package com.these.school.models;

import com.google.firebase.Timestamp;

public class Demande {
    private String id;
    private int state;//0 attente 1 accepte 2 refuse
    private User parent;
    private User ens;
    private Classe classe;
    private Timestamp time;

    public Demande() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public User getEns() {
        return ens;
    }

    public void setEns(User ens) {
        this.ens = ens;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
