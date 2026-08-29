package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Name;

import java.util.Objects;

/**
 * Entidad de dominio que representa una categoría de productos.
 *
 * <p>Una categoría tiene un nombre y un estado de actividad que permite
 * activarla o desactivarla dentro del catálogo.
 */
public class Category {

    private final Long id;
    private Name name;
    private boolean active;

    /**
     * Crea una categoría activa con el nombre indicado.
     *
     * @param id   identificador de la categoría; no puede ser {@code null}.
     * @param name nombre de la categoría; no puede ser {@code null}.
     * @throws NullPointerException si {@code id} o {@code name} son {@code null}.
     */
    public Category(Long id, Name name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "El nombre de la categoría no puede ser nulo.");
        this.active = true;
    }

    /**
     * Obtiene el identificador de la categoría.
     *
     * @return el identificador de la categoría.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la categoría.
     *
     * @return el nombre de la categoría.
     */
    public Name getName() {
        return name;
    }

    /**
     * Indica si la categoría está activa.
     *
     * @return {@code true} si la categoría está activa, {@code false} en caso contrario.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Renombra la categoría.
     *
     * @param newName el nuevo nombre; no puede ser {@code null}.
     * @throws NullPointerException si {@code newName} es {@code null}.
     */
    public void rename(Name newName) {
        this.name = Objects.requireNonNull(newName, "El nuevo nombre de la categoría no puede ser nulo.");
    }

    /**
     * Activa la categoría.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Desactiva la categoría.
     */
    public void deactivate() {
        this.active = false;
    }
}
