package com.bank.pfe1.service;

import com.bank.pfe1.entity.Brand;
import com.bank.pfe1.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand createBrand(Brand brand) {
        if (brandRepository.existsByBrandName(brand.getBrandName())) {
            throw new RuntimeException("Brand already exists!");
        }
        return brandRepository.save(brand);
    }

    public Brand updateBrand(Long id, Brand updated) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found!"));
        brand.setBrandName(updated.getBrandName());
        brand.setCountry(updated.getCountry());
        return brandRepository.save(brand);
    }

    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }
}