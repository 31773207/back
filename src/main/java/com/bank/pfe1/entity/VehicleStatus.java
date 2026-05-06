package com.bank.pfe1.entity;

public enum VehicleStatus {
    AVAILABLE,      // ✅ Libre (pas assigné à personne)
    ASSIGNED,       // ✅ Assigné à un employé (non disponible pour missions)
    IN_MISSION,     // ✅ En mission
    IN_REVISION,    // ✅ En révision
    BREAKDOWN,      // ✅ En panne
    REFORMED        // ✅ Réformé
}