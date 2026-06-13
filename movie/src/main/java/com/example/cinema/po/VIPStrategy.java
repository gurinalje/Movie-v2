package com.example.cinema.po;

public class VIPStrategy {

    private int id;

    /**
     * 充值额度
     */
    private int chargeLimit;

    /**
     * 赠送额度
     */
    private int giftAmount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChargeLimit() {
        return chargeLimit;
    }

    public void setChargeLimit(int chargeLimit) {
        this.chargeLimit = chargeLimit;
    }

    public int getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(int giftAmount) {
        this.giftAmount = giftAmount;
    }

    public String getDescription(){
        return "满"+this.getChargeLimit()+"赠"+this.getGiftAmount();
    }

    /**
     * 满 chargeLimit 赠 giftAmount
     */
}
