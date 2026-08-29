package com.mabc.e_shop.domain.valueobject;

import com.mabc.e_shop.domain.exception.InvalidStockException;

/**
 * Value object que representa el stock de un producto.
 *
 * <p>Valida que el stock no sea negativo.
 *
 * @param value valor del stock.
 */
public record Stock(int value) {

    /**
     * Constructor compacto que valida el stock.
     *
     * @throws InvalidStockException si el stock es negativo.
     */
    public Stock {
        if (value < 0) {
            throw new InvalidStockException("El stock no puede ser negativo.");
        }
    }
}
