package com.bank.pfe1.service;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartNaftalService {

    @Autowired
    private CartNaftalRepository cartNaftalRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<CartNaftal> getAllCarts() {
        return cartNaftalRepository.findAll();
    }

    public CartNaftal getCartById(Long id) {
        return cartNaftalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));
    }

    public CartNaftal createCart(CartNaftal cart) {
        if (cart.getDriver() != null && cart.getDriver().getId() != null) {
            Driver driver = driverRepository.findById(cart.getDriver().getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            cart.setDriver(driver);
        }
        if (cart.getVehicle() != null && cart.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(cart.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            cart.setVehicle(vehicle);
        }
        cart.setActive(true);
        return cartNaftalRepository.save(cart);
    }

    public CartNaftal assignToDriver(Long cartId, Long driverId) {
        CartNaftal cart = getCartById(cartId);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        cart.setDriver(driver);
        cart.setVehicle(null);
        return cartNaftalRepository.save(cart);
    }

    // ✅ ADDED - assign card to a vehicle
    public CartNaftal assignToVehicle(Long cartId, Long vehicleId) {
        CartNaftal cart = getCartById(cartId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        cart.setVehicle(vehicle);
        cart.setDriver(null);
        return cartNaftalRepository.save(cart);
    }

    public CartNaftal toggleActive(Long id) {
        CartNaftal cart = getCartById(id);
        cart.setActive(!cart.isActive());
        return cartNaftalRepository.save(cart);
    }

    public void deleteCart(Long id) {
        cartNaftalRepository.deleteById(id);
    }
}