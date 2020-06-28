package edu.skku.map.mapproject;


public class WalletItem {
    private String money;
    private String date;
    private String purpose;
    private String people;
    private String url;



    public WalletItem(String money, String date, String purpose, String people, String url) {
        this.money=money;
        this.date=date;
        this.purpose=purpose;
        this.people=people;
        this.url=url;
    }

    public String getPeople() {
        return people;
    }

    public String getMoney() {
        return money;
    }

    public String getDate() {
        return date;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getUrl() {
        return url;
    }
}
