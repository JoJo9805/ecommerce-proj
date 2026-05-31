package com.webthuongmai.service;
import com.webthuongmai.entity.ShippingUnit;
import com.webthuongmai.repository.ShippingUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShippingUnitService {
    @Autowired
    private ShippingUnitRepository shippingUnitRepository;

    public List<ShippingUnit> getAllShippingUnits() {
        return shippingUnitRepository.findAll();
    }

    public ShippingUnit createShippingUnit(ShippingUnit shippingUnit) {
        return shippingUnitRepository.save(shippingUnit);
    }
}