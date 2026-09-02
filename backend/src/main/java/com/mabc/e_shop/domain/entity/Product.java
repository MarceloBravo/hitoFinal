package com.mabc.e_shop.domain.entity;

import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Quantity;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio que representa un producto del catálogo.
 *
 * <p>Un producto pertenece a una {@link Mark}, puede asociarse a varias
 * {@link Category} y dispone de nombre, descripción, stock, peso y precios
 * de costo y de venta. Provee operaciones para renombrarlo, actualizar su
 * descripción, reabastecerlo y actualizar sus precios.
 */
public class Product {

    private final Long id;
    private Mark mark;
    private List<Category> categories;
    private Name name;
    private Description description;
    private Stock stock;
    private Weight weight;
    private Price priceCost;
    private Price priceSale;
    private ImagePath imagePath;

    /**
     * Crea un producto con todos sus atributos.
     *
     * @param id          identificador del producto; no puede ser {@code null}.
     * @param mark        marca del producto; no puede ser {@code null}.
     * @param categories  categorías del producto; puede ser {@code null} o vacía.
     * @param name        nombre del producto; no puede ser {@code null}.
     * @param description descripción del producto; no puede ser {@code null}.
     * @param stock       stock del producto; no puede ser {@code null}.
     * @param weight      peso del producto; no puede ser {@code null}.
     * @param priceCost   precio de costo del producto; no puede ser {@code null}.
     * @param priceSale   precio de venta del producto; no puede ser {@code null}.
     * @param imagePath   imagen del producto; puede ser {@code null}.
     * @throws NullPointerException si alguno de los parámetros requeridos es {@code null}.
     */
    public Product(
        Long id, 
        Mark mark, 
        List<Category> categories, 
        Name name, 
        Description description,
        Stock stock, 
        Weight weight, 
        Price priceCost, 
        Price priceSale,
        ImagePath imagePath
    ) {
        this.id = id;
        this.mark = Objects.requireNonNull(mark, "La marca del producto no puede ser nula.");
        this.categories = categories == null ? new ArrayList<>() : new ArrayList<>(categories);
        this.name = Objects.requireNonNull(name, "El nombre del producto no puede ser nulo.");
        this.description = Objects.requireNonNull(description, "La descripción del producto no puede ser nula.");
        this.stock = Objects.requireNonNull(stock, "El stock del producto no puede ser nulo.");
        this.weight = Objects.requireNonNull(weight, "El peso del producto no puede ser nulo.");
        this.priceCost = Objects.requireNonNull(priceCost, "El precio de costo no puede ser nulo.");
        this.priceSale = Objects.requireNonNull(priceSale, "El precio de venta no puede ser nulo.");
        this.imagePath = imagePath;
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return el identificador del producto.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene la marca del producto.
     *
     * @return la marca del producto.
     */
    public Mark getMark() {
        return mark;
    }

    /**
     * Obtiene las categorías del producto como lista no modificable.
     *
     * @return lista inmutable de categorías del producto.
     */
    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return el nombre del producto.
     */
    public Name getName() {
        return name;
    }

    /**
     * Obtiene la descripción del producto.
     *
     * @return la descripción del producto.
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Obtiene el stock del producto.
     *
     * @return el stock del producto.
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * Obtiene el peso del producto.
     *
     * @return el peso del producto.
     */
    public Weight getWeight() {
        return weight;
    }

    /**
     * Obtiene el precio de costo del producto.
     *
     * @return el precio de costo del producto.
     */
    public Price getPriceCost() {
        return priceCost;
    }

    /**
     * Obtiene el precio de venta del producto.
     *
     * @return el precio de venta del producto.
     */
    public Price getPriceSale() {
        return priceSale;
    }

    public ImagePath getImagePath(){
        return imagePath;
    }

    /**
     * Renombra el producto.
     *
     * @param newName el nuevo nombre; no puede ser {@code null}.
     * @throws NullPointerException si {@code newName} es {@code null}.
     */
    public void rename(Name newName) {
        this.name = Objects.requireNonNull(newName, "El nuevo nombre del producto no puede ser nulo.");
    }

    /**
     * Actualiza la descripción del producto.
     *
     * @param newDescription la nueva descripción; no puede ser {@code null}.
     * @throws NullPointerException si {@code newDescription} es {@code null}.
     */
    public void updateDescription(Description newDescription) {
        this.description = Objects.requireNonNull(newDescription, "La nueva descripción no puede ser nula.");
    }

    /**
     * Reabastece el stock del producto.
     *
     * @param newStock el nuevo stock; no puede ser {@code null}.
     * @throws NullPointerException si {@code newStock} es {@code null}.
     */
    public void restock(Stock newStock) {
        this.stock = Objects.requireNonNull(newStock, "El nuevo stock no puede ser nulo.");
    }

    /**
     * Actualiza los precios de costo y de venta del producto.
     *
     * @param newCost el nuevo precio de costo; no puede ser {@code null}.
     * @param newSale el nuevo precio de venta; no puede ser {@code null}.
     * @throws NullPointerException si {@code newCost} o {@code newSale} son {@code null}.
     */
    public void updatePrices(Price newCost, Price newSale) {
        this.priceCost = Objects.requireNonNull(newCost, "El nuevo precio de costo no puede ser nulo.");
        this.priceSale = Objects.requireNonNull(newSale, "El nuevo precio de venta no puede ser nulo.");
    }

    /**
     * Indica si el stock disponible alcanza para la cantidad solicitada.
     *
     * @param quantity cantidad a consultar.
     * @return {@code true} si hay stock suficiente, {@code false} en caso contrario.
     */
    public boolean hasStock(Quantity quantity) {
        return this.stock.value() >= quantity.value();
    }

    /**
     * Reduce el stock del producto en la cantidad indicada.
     *
     * @param quantity cantidad de unidades a descontar del stock.
     * @throws IllegalStateException si no hay stock suficiente.
     */
    public void reduceStock(Quantity quantity) {
        if (!hasStock(quantity)) {
            throw new IllegalStateException("Stock insuficiente para el producto " + name.value());
        }
        this.stock = new Stock(this.stock.value() - quantity.value());
    }

    /**
     * Actualiza la ruta de la imagen del producto.
     *
     * @param newImage la nueva imagen; puede ser {@code null}.
     */
    public void updateImagePath(ImagePath newImage){
        this.imagePath = newImage;
    }

    public void updateWeight(Weight newWeight){
        this.weight = Objects.requireNonNull(newWeight, "El nuevo peso del producto no puede ser nulo.");
    }

    public void updateMark(Mark newMark){
        this.mark = Objects.requireNonNull(newMark, "La nueva marca del producto no puede ser nula.");
    }

    public void updateCategories(List<Category> newCategories){
        this.categories = newCategories == null ? new ArrayList<>() : new ArrayList<>(newCategories);
    }
}
