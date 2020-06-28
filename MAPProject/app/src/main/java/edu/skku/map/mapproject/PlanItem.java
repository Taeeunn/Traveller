package edu.skku.map.mapproject;


public class PlanItem {
    private String title;
    private String city;
    private String plan;
    private String budget;
    private String materials;
    private String memo;
    private String who;
    private String url;



    public PlanItem(String title, String city, String plan, String budget, String materials, String memo, String who, String url) {
        this.title=title;
        this.city=city;
        this.plan=plan;
        this.budget=budget;
        this.materials=materials;
        this.memo=memo;
        this.who=who;
        this.url=url;
    }

    public String getPlan() {
        return plan;
    }

    public String getBudget() {
        return budget;
    }

    public String getMaterials() {
        return materials;
    }

    public String getMemo() {
        return memo;
    }

    public String getWho() {
        return who;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getUrl() {
        return url;
    }
}
