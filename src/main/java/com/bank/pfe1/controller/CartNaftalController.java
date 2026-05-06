package com.bank.pfe1.controller;

import com.bank.pfe1.entity.CartNaftal;
import com.bank.pfe1.service.CartNaftalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart-naftal")
public class CartNaftalController {

    @Autowired
    private CartNaftalService cartNaftalService;

    @GetMapping
    public List<CartNaftal> getAllCarts() {
        return cartNaftalService.getAllCarts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartNaftal> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartNaftalService.getCartById(id));
    }

    @PostMapping
    public ResponseEntity<CartNaftal> createCart(@RequestBody CartNaftal cart) {
        return ResponseEntity.ok(cartNaftalService.createCart(cart));
    }

    @PatchMapping("/{cartId}/assign/driver/{driverId}")
    public ResponseEntity<CartNaftal> assignToDriver(
            @PathVariable Long cartId,
            @PathVariable Long driverId) {
        return ResponseEntity.ok(cartNaftalService.assignToDriver(cartId, driverId));
    }


    @PatchMapping("/{cartId}/assign/vehicle/{vehicleId}")
    public ResponseEntity<CartNaftal> assignToVehicle(
            @PathVariable Long cartId,
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(cartNaftalService.assignToVehicle(cartId, vehicleId));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<CartNaftal> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(cartNaftalService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartNaftalService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}