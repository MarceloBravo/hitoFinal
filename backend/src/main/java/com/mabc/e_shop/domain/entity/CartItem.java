package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Quantity;

import java.util.Objects;

/**
 * Entidad de dominio que representa un ítem dentro de un carrito de compras.
 *
 * <p>
 * Vincula un {@link Product} con una {@link Quantity} y mantiene el
 * subtotal calculado como el precio de venta del producto por la cantidad.
 */
public class CartItem {

    private final Long id;
    private final Product product;
    private Quantity quantity;
    private double subTotal;

    /**
     * Crea un ítem de carrito con un producto y una cantidad.
     *
     * @param id       identificador del ítem; puede ser {@code null} para ítems nuevos
     *                 que aún no han sido persistidos (la base de datos asignará el ID).
     * @param product  producto del ítem; no puede ser {@code null}.
     * @param quantity cantidad del ítem; no puede ser {@code null}.
     * @throws NullPointerException si {@code product} o {@code quantity} son {@code null}.
     */
    public CartItem(Long id, Product product, Quantity quantity) {
        this.id = id; // puede ser null para ítems nuevos
        this.product = Objects.requireNonNull(product, "El producto del ítem no puede ser nulo.");
        this.quantity = Objects.requireNonNull(quantity, "La cantidad del ítem no puede ser nula.");
        this.subTotal = product.getPriceSale().value() * quantity.value();
    }

    /**
     * Obtiene el identificador del ítem.
     *
     * @return el identificador del ítem.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el producto del ítem.
     *
     * @return el producto asociado al ítem.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Obtiene la cantidad de unidades del ítem.
     *
     * @return la cantidad del ítem.
     */
    public Quantity getQuantity() {
        return quantity;
    }

    /**
     * Obtiene el subtotal del ítem.
     *
     * @return el subtotal del ítem.
     */
    public double getSubTotal() {
        return subTotal;
    }

    /**
     * Cambia la cantidad del ítem y recalcula su subtotal.
     *
     * @param newQuantity la nueva cantidad; no puede ser {@code null}.
     * @throws NullPointerException si {@code newQuantity} es {@code null}.
     */
    public void changeQuantity(Quantity newQuantity) {
        this.quantity = Objects.requireNonNull(newQuantity, "La nueva cantidad no puede ser nula.");
        this.subTotal = product.getPriceSale().value() * quantity.value();
    }

    /**
     * Recalcula el subtotal del ítem a partir del precio de venta y la cantidad.
     */
    public void calculateSubTotal() {
        this.subTotal = product.getPriceSale().value() * quantity.value();
    }
}
