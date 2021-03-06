package ir.madeinlobb.hw1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReqResCoins {
    public class Coin{
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("symbol")
        @Expose
        private String symbol;

        @SerializedName("price")
        @Expose
        private String price;

        @SerializedName("percent_change_1h")
        @Expose
        private String percent_change_1h;

        @SerializedName("percent_change_24h")
        @Expose
        private String percent_change_24h;

        @SerializedName("percent_change_7d")
        @Expose
        private String percent_change_7d;



    }

}
