package com.bank.pfe1.entity;

public enum AccessLevel {
    NONE,    // Cannot see it at all
    VIEW,    // Can only read
    MANAGE   // Full access: read, write, update, delete
}