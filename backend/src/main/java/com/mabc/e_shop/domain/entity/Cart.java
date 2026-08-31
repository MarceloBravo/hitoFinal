package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Quantity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio que representa un carrito de compras.
 *
 * <p>
 * Agrupa una lista de {@link CartItem} y mantiene el subtotal calculado
 * a partir de los ítems que lo componen. La fecha de creación se registra
 * en la zona horaria de Chile (America/Santiago).
 */
public class Cart {

    private final Long id;
    private final List<CartItem> items;
    private final LocalDateTime creationDate;
    private double subTotal;

    /**
     * Crea un carrito de compras vacío.
     *
     * @param id identificador del carrito; no puede ser {@code null}.
     * @throws NullPointerException si {@code id} es {@code null}.
     */
    public Cart(Long id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.creationDate = LocalDateTime.now(ZoneId.of("America/Santiago"));
        this.subTotal = 0.0;
    }

    /**
     * Obtiene el identificador del carrito.
     *
     * @return el identificador del carrito.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene los ítems del carrito como una lista no modificable.
     *
     * @return lista inmutable de ítems del carrito.
     */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Obtiene la fecha de creación del carrito.
     *
     * @return la fecha de creación del carrito.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Obtiene el subtotal acumulado del carrito.
     *
     * @return el subtotal del carrito.
     */
    public double getSubTotal() {
        return subTotal;
    }

    /**
     * Agrega un producto al carrito con la cantidad indicada y recalcula el
     * subtotal.
     *
     * @param product  producto a agregar; no puede ser {@code null}.
     * @param quantity cantidad de unidades a agregar; no puede ser {@code null}.
     * @return el ítem de carrito creado.
     * @throws NullPointerException  si {@code product} o {@code quantity} son
     *                               {@code null}.
     * @throws IllegalStateException si no hay stock suficiente del producto.
     */
    public CartItem addItem(Product product, Quantity quantity) {
        Objects.requireNonNull(product, "El producto no puede ser nulo.");
        Objects.requireNonNull(quantity, "La cantidad no puede ser nula.");

        if (!product.hasStock(quantity)) {
            throw new IllegalStateException("Stock insuficiente para el producto " + product.getName().value());
        }

        CartItem item = new CartItem(null, product, quantity); // id null: Hibernate generará el ID al persistir
        items.add(item);
        calculateSubTotal();
        return item;
    }

    /**
     * Recalcula el subtotal del carrito sumando el subtotal de todos sus ítems.
     */
    public void calculateSubTotal() {
        this.subTotal = items.stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();
    }

    /**
     * Restaura un ítem ya persistido al carrito sin validar stock.
     *
     * <p>
     * Uso exclusivo de la capa de persistencia al reconstruir el estado
     * del carrito desde la base de datos. El {@code id} debe ser el
     * identificador real asignado por la BD.
     *
     * @param id       identificador real del ítem en la base de datos.
     * @param product  producto del ítem.
     * @param quantity cantidad del ítem.
     * @return el ítem restaurado.
     */
    public CartItem addItemWithId(Long id, Product product, Quantity quantity) {
        Objects.requireNonNull(product, "El producto no puede ser nulo.");
        Objects.requireNonNull(quantity, "La cantidad no puede ser nula.");

        CartItem item = new CartItem(id, product, quantity);
        items.add(item);
        calculateSubTotal();
        return item;
    }

    /**
     * Elimina del carrito el ítem con el identificador entregado y recalcula
     * el subtotal.
     *
     * @param itemId identificador del ítem a eliminar; no puede ser {@code null}.
     * @throws NullPointerException           si {@code itemId} es {@code null}.
     * @throws IllegalArgumentException      si el ítem no existe en el carrito.
     */
    public void removeItemById(Long itemId) {
        Objects.requireNonNull(itemId, "El id del ítem no puede ser nulo.");

        boolean removed = items.removeIf(item -> Objects.equals(item.getId(), itemId));
        if (!removed) {
            throw new IllegalArgumentException("El ítem no existe en el carrito.");
        }
        calculateSubTotal();
    }
}
