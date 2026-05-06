package com.bank.pfe1.config;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.AppRoleRepository;
import com.bank.pfe1.repository.RolePermissionRepository;
import com.bank.pfe1.repository.WilayaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppRoleRepository roleRepository;
    private final RolePermissionRepository permissionRepository;
    private final WilayaRepository wilayaRepository;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("ADMIN", "System administrator - full system config access",
            new AppModule[]{
                AppModule.DASHBOARD, AppModule.USERS, AppModule.SYSTEM_CONFIG,
                AppModule.VEHICLES, AppModule.DRIVERS, AppModule.MISSIONS,
                AppModule.MAINTENANCE, AppModule.GAS_COUPONS,
                AppModule.TECHNICAL_CHECKS, AppModule.CART_NAFTAL,
                AppModule.EMPLOYEES, AppModule.ORGANIZATIONS
            },
            new AccessLevel[]{
                AccessLevel.VIEW, AccessLevel.MANAGE, AccessLevel.MANAGE,
                AccessLevel.VIEW, AccessLevel.VIEW, AccessLevel.VIEW,
                AccessLevel.VIEW, AccessLevel.VIEW,
                AccessLevel.VIEW, AccessLevel.VIEW,
                AccessLevel.VIEW, AccessLevel.VIEW
            }
        );

        createRoleIfNotExists("MANAGER", "Park Manager - full operational access",
            new AppModule[]{
                AppModule.DASHBOARD, AppModule.USERS, AppModule.SYSTEM_CONFIG,
                AppModule.VEHICLES, AppModule.DRIVERS, AppModule.MISSIONS,
                AppModule.MAINTENANCE, AppModule.GAS_COUPONS,
                AppModule.TECHNICAL_CHECKS, AppModule.CART_NAFTAL,
                AppModule.EMPLOYEES, AppModule.ORGANIZATIONS
            },
            new AccessLevel[]{
                AccessLevel.MANAGE, AccessLevel.NONE, AccessLevel.NONE,
                AccessLevel.MANAGE, AccessLevel.MANAGE, AccessLevel.MANAGE,
                AccessLevel.MANAGE, AccessLevel.MANAGE,
                AccessLevel.MANAGE, AccessLevel.MANAGE,
                AccessLevel.MANAGE, AccessLevel.VIEW
            }
        );

        createRoleIfNotExists("RESPONSABLE", "Boss - dashboard and reports only",
            new AppModule[]{
                AppModule.DASHBOARD, AppModule.USERS, AppModule.SYSTEM_CONFIG,
                AppModule.VEHICLES, AppModule.DRIVERS, AppModule.MISSIONS,
                AppModule.MAINTENANCE, AppModule.GAS_COUPONS,
                AppModule.TECHNICAL_CHECKS, AppModule.CART_NAFTAL,
                AppModule.EMPLOYEES, AppModule.ORGANIZATIONS
            },
            new AccessLevel[]{
                AccessLevel.VIEW, AccessLevel.NONE, AccessLevel.NONE,
                AccessLevel.NONE, AccessLevel.NONE, AccessLevel.NONE,
                AccessLevel.NONE, AccessLevel.NONE,
                AccessLevel.NONE, AccessLevel.NONE,
                AccessLevel.NONE, AccessLevel.NONE
            }
        );

        // Add wilayas if none exist
        if (roleRepository.count() > 0 && !wilayaRepository.existsByCode("01")) {
            String[][] wilayas = {
                {"01","Adrar"},{"02","Chlef"},{"03","Laghouat"},{"04","Oum El Bouaghi"},
                {"05","Batna"},{"06","Béjaïa"},{"07","Biskra"},{"08","Béchar"},
                {"09","Blida"},{"10","Bouira"},{"11","Tamanrasset"},{"12","Tébessa"},
                {"13","Tlemcen"},{"14","Tiaret"},{"15","Tizi Ouzou"},{"16","Alger"},
                {"17","Djelfa"},{"18","Jijel"},{"19","Sétif"},{"20","Saïda"},
                {"21","Skikda"},{"22","Sidi Bel Abbès"},{"23","Annaba"},{"24","Guelma"},
                {"25","Constantine"},{"26","Médéa"},{"27","Mostaganem"},{"28","M'Sila"},
                {"29","Mascara"},{"30","Ouargla"},{"31","Oran"},{"32","El Bayadh"},
                {"33","Illizi"},{"34","Bordj Bou Arréridj"},{"35","Boumerdès"},
                {"36","El Tarf"},{"37","Tindouf"},{"38","Tissemsilt"},{"39","El Oued"},
                {"40","Khenchela"},{"41","Souk Ahras"},{"42","Tipaza"},{"43","Mila"},
                {"44","Aïn Defla"},{"45","Naâma"},{"46","Aïn Témouchent"},{"47","Ghardaïa"},
                {"48","Relizane"},{"49","Timimoun"},{"50","Bordj Badji Mokhtar"},
                {"51","Ouled Djellal"},{"52","Béni Abbès"},{"53","In Salah"},
                {"54","In Guezzam"},{"55","Touggourt"},{"56","Djanet"},
                {"57","El M'Ghair"},{"58","El Meniaa"}
            };
            for (String[] w : wilayas) {
                wilayaRepository.save(Wilaya.builder().code(w[0]).name(w[1]).active(true).build());
            }
            System.out.println("✅ Added all 58 Algerian wilayas");
        }
    }

    private void createRoleIfNotExists(String name, String description,
                                        AppModule[] modules, AccessLevel[] levels) {
        if (roleRepository.existsByName(name)) return;

        AppRole role = AppRole.builder()
                .name(name)
                .description(description)
                .build();
        role = roleRepository.save(role);

        for (int i = 0; i < modules.length; i++) {
            RolePermission perm = RolePermission.builder()
                    .role(role)
                    .module(modules[i])
                    .accessLevel(levels[i])
                    .build();
            permissionRepository.save(perm);
        }
        System.out.println("✅ Created default role: " + name);
    }
}