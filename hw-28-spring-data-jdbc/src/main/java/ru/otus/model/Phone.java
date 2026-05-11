package ru.otus.model;

import org.springframework.data.relational.core.mapping.Table;

@Table("phone")
public record Phone(String number) {}
