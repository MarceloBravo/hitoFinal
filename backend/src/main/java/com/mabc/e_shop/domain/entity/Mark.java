package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Name;

import java.util.Objects;

/**
 * Entidad de dominio que representa una marca de productos.
 *
 * <p>Una marca tiene un nombre y un estado de actividad que permite
 * activarla o desactivarla dentro del catálogo.
 */
public class Mark {

    private final Long id;
    private Name name;
    private boolean active;

    /**
     * Crea una marca activa con el nombre indicado.
     *
     * @param id   identificador de la marca; no puede ser {@code null}.
     * @param name nombre de la marca; no puede ser {@code null}.
     * @throws NullPointerException si {@code id} o {@code name} son {@code null}.
     */
    public Mark(Long id, Name name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "El nombre de la marca no puede ser nulo.");
        this.active = true;
    }

    /**
     * Obtiene el identificador de la marca.
     *
     * @return el identificador de la marca.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la marca.
     *
     * @return el nombre de la marca.
     */
    public Name getName() {
        return name;
    }

    /**
     * Indica si la marca está activa.
     *
     * @return {@code true} si la marca está activa, {@code false} en caso contrario.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Renombra la marca.
     *
     * @param newName el nuevo nombre; no puede ser {@code null}.
     * @throws NullPointerException si {@code newName} es {@code null}.
     */
    public void rename(Name newName) {
        this.name = Objects.requireNonNull(newName, "El nuevo nombre de la marca no puede ser nulo.");
    }

    /**
     * Activa la marca.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Desactiva la marca.
     */
    public void deactivate() {
        this.active = false;
    }
}
