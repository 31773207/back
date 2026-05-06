package com.bank.pfe1.service;

import com.bank.pfe1.entity.Organization;
import com.bank.pfe1.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    public Organization createOrganization(Organization org) {
        return organizationRepository.save(org);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }
}