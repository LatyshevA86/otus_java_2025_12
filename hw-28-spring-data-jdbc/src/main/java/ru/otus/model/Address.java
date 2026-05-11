package ru.otus.model;

import org.springframework.data.relational.core.mapping.Table;

@Table("address")
public record Address(String street) {}
