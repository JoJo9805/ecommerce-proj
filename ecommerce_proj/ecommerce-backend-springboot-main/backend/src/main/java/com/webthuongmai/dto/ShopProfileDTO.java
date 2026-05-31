package com.webthuongmai.dto;

import com.webthuongmai.entity.Shop;

public class ShopProfileDTO {
    private Shop shop;
    private long productCount;
    private long salesCount;

    public ShopProfileDTO() {}

    public ShopProfileDTO(Shop shop, long productCount, long salesCount) {
        this.shop = shop;
        this.productCount = productCount;
        this.salesCount = salesCount;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    public long getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(long salesCount) {
        this.salesCount = salesCount;
    }
}
