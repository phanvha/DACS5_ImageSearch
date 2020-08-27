package com.utt.zipapp.Model;

public class ItemsHistory {
    private int id;
    private String link;
    private String datetime;
    private String person_id;

    public ItemsHistory(int id, String link, String datetime, String person_id) {
        this.id = id;
        this.link = link;
        this.datetime = datetime;
        this.person_id = person_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }
}
