package org.example.kdt_bank_client2.UserBank;

public class Product {
    private String product_name;
    private String product_detail;
    private String category;
    private String product_category;
    private String maxRate;
    private String minRate;
    private String limitmoney;

    public Product(String product_name, String product_detail, String category, String product_category, String maxRate, String minRate, String limitmoney) {
        this.product_name = product_name;
        this.product_detail = product_detail;
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.limitmoney = limitmoney;
        this.product_category = product_category;
        this.category = category;

    }
    public String getProduct_name() { return product_name;  }
    public String getProduct_detail() { return product_detail; }
    public String getMaxRate() { return maxRate; }
    public String getMinRate() { return minRate; }
    public String getLimit() { return limitmoney; }
    public String getCategory(){ return category; }
    public String getProduct_category(){ return product_category; }
}
