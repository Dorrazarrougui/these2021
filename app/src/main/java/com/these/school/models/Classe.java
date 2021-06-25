package com.these.school.models;

public class Classe {
    private String id;
    private String teacher;
    private String classeName;
    private String classeLevel;

    public Classe() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClasseName() {
        return classeName;
    }

    public void setClasseName(String classeName) {
        this.classeName = classeName;
    }

    public String getClasseLevel() {
        return classeLevel;
    }

    public void setClasseLevel(String classeLevel) {
        this.classeLevel = classeLevel;
    }
}
