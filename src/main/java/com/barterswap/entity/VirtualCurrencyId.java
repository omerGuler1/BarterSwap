package com.barterswap.entity;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class VirtualCurrencyId implements Serializable {
    @Id
    private User user;

    public VirtualCurrencyId() {}

    public VirtualCurrencyId(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualCurrencyId that = (VirtualCurrencyId) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
} 