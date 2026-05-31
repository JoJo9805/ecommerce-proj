package com.webthuongmai.controller;
import com.webthuongmai.entity.ShippingUnit;
import com.webthuongmai.service.ShippingUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shipping-units")
@CrossOrigin("*")
public class ShippingUnitController {
    @Autowired
    private ShippingUnitService shippingUnitService;

    @GetMapping
    public List<ShippingUnit> getAll() {
        return shippingUnitService.getAllShippingUnits();
    }

    @PostMapping
    public ShippingUnit create(@RequestBody ShippingUnit shippingUnit) {
        return shippingUnitService.createShippingUnit(shippingUnit);
    }
}