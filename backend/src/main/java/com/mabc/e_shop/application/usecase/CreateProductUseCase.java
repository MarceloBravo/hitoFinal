package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Category;
import com.mabc.e_shop.domain.entity.Mark;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Description;
import com.mabc.e_shop.domain.valueobject.ImagePath;
import com.mabc.e_shop.domain.valueobject.Name;
import com.mabc.e_shop.domain.valueobject.Price;
import com.mabc.e_shop.domain.valueobject.Stock;
import com.mabc.e_shop.domain.valueobject.Weight;

import java.util.List;

/**
 * Caso de uso que crea o actualiza un producto.
 *
 * <p>Si se entrega un {@code id} nulo se crea un producto nuevo; en caso
 * contrario se actualizan los datos del producto existente.
 */
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MarkRepository markRepository;

    /**
     * Crea el caso de uso con los repositorios necesarios.
     *
     * @param productRepository  repositorio de productos.
     * @param categoryRepository repositorio de categorías.
     * @param markRepository     repositorio de marcas.
     */
    public CreateProductUseCase(
        ProductRepository productRepository, 
        CategoryRepository categoryRepository,
        MarkRepository markRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.markRepository = markRepository;
    }

    /**
     * Crea un producto nuevo o actualiza uno existente.
     *
     * @param id          identificador del producto; si es {@code null} se crea uno nuevo.
     * @param markId      identificador de la marca del producto.
     * @param categoryIds lista de identificadores de categorías del producto.
     * @param name        nombre del producto.
     * @param description descripción del producto.
     * @param stock       cantidad de unidades en stock.
     * @param weight      peso del producto.
     * @param priceCost   precio de costo del producto.
     * @param priceSale   precio de venta del producto.
     * @param imagePath   ruta donde se aloja la imagen del producto.
     * @return el producto creado o actualizado y persistido.
     * @throws IllegalArgumentException si la marca no existe o alguna
     *                                  categoría no existe.
     */
    public Product execute(
        Long id, 
        Long markId, 
        List<Long> categoryIds, 
        String name, 
        String description,
        int stock, 
        double weight, 
        double priceCost, 
        double priceSale,
        String imagePath
    ) {
        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new IllegalArgumentException("La marca no existe."));

        List<Category> categories = categoryRepository.findAllByIds(categoryIds);
        if (categories.isEmpty() || categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("Alguna categoría no existe.");
        }

        Product product;
        if (id == null) {
            product = new Product(null, mark, categories, new Name(name), new Description(description),
                    new Stock(stock), new Weight(weight), new Price(priceCost), new Price(priceSale), new ImagePath(imagePath));
        } else {
            // Se reconstruye el producto existente con la marca y las categorías
            // recibidas. El producto de dominio conserva mark/categories como
            // campos inmutables (final), por lo que la actualización de estos
            // atributos se realiza reconstruyendo la entidad con los nuevos valores.
            product = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("El producto no existe."));
            product.updateMark(mark);
            product.updateCategories(categories);
            product.rename(new Name(name));
            product.updateDescription(new Description(description));
            product.restock(new Stock(stock));
            product.updateWeight(new Weight(weight));
            product.updatePrices(new Price(priceCost), new Price(priceSale));
            product.updateImagePath(new ImagePath(imagePath));
        }
        return productRepository.save(product);
    }
}
