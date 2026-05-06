package com.bank.pfe1.service;

import com.bank.pfe1.entity.VehicleType;
import com.bank.pfe1.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;

    public List<VehicleType> getAllTypes() {
        return vehicleTypeRepository.findAll();
    }

    public VehicleType createType(VehicleType type) {
        if (vehicleTypeRepository.existsByTypeName(type.getTypeName())) {
            throw new RuntimeException("Vehicle type already exists!");
        }
        return vehicleTypeRepository.save(type);
    }

    public VehicleType updateType(Long id, VehicleType updated) {
        VehicleType type = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found!"));
        type.setTypeName(updated.getTypeName());
        type.setDescription(updated.getDescription());
        return vehicleTypeRepository.save(type);
    }

    public void deleteType(Long id) {
        vehicleTypeRepository.deleteById(id);
    }
}
