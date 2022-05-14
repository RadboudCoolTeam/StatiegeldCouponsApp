package io.github.textrecognisionsample;

public class Coupon {

    private String date;
    private String money;
    private SupermarketChain supermarketChain;


    public Coupon(String date, String money, SupermarketChain supermarketChain) {
        this.date = date;
        this.money = money;
        this.supermarketChain = supermarketChain;
    }

    public SupermarketChain getSupermarketChain() {
        return supermarketChain;
    }

    public void setSupermarketChain(SupermarketChain supermarketChain) {
        this.supermarketChain = supermarketChain;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
