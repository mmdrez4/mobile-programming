package ir.madeinlobb.hw1;

public class DigitalCoin {
    private String symbol;
    private String name;
    private String logoUrl;
    private int price;
    private int changeHour;
    private int changeDay;
    private int changeWeek;

    public DigitalCoin(String symbol, String name, String logoUrl, int price, int changeHour, int changeDay, int changeWeek) {
        this.symbol = symbol;
        this.name = name;
        this.logoUrl = logoUrl;
        this.price = price;
        this.changeHour = changeHour;
        this.changeDay = changeDay;
        this.changeWeek = changeWeek;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public int getPrice() {
        return price;
    }

    public int getChangeHour() {
        return changeHour;
    }

    public int getChangeDay() {
        return changeDay;
    }

    public int getChangeWeek() {
        return changeWeek;
    }
}
