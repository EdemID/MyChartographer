package com.kuvyrkom.chartographer.domain.model;

import javax.persistence.*;

@Entity
@Table(name = "charta")
public class Charta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int width;
    private int height;
    private String fileUUID;
    private String filePath;

    public Charta() {
    }

    public Charta(int width, int height, String fileUUID, String filePath) {
        this.width = width;
        this.height = height;
        this.fileUUID = fileUUID;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFileUUID() {
        return fileUUID;
    }

    public void setFileUUID(String fileUUID) {
        this.fileUUID = fileUUID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
