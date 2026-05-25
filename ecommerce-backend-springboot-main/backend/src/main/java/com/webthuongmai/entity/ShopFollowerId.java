package com.webthuongmai.entity;

import java.io.Serializable;
import java.util.Objects;

public class ShopFollowerId implements Serializable {
    private Long user;
    private Long shop;

    public ShopFollowerId() {}

    public ShopFollowerId(Long user, Long shop) {
        this.user = user;
        this.shop = shop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopFollowerId that = (ShopFollowerId) o;
        return Objects.equals(user, that.user) && Objects.equals(shop, that.shop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, shop);
    }
}