package com.mabc.e_shop.infrastructure.config;

import com.mabc.e_shop.application.usecase.AddItemToCartUseCase;
import com.mabc.e_shop.application.usecase.CheckoutCartUseCase;
import com.mabc.e_shop.application.usecase.CreateCartUseCase;
import com.mabc.e_shop.application.usecase.CreateProductUseCase;
import com.mabc.e_shop.application.usecase.DecrementItemQuantityFromCartUseCase;
import com.mabc.e_shop.application.usecase.DeleteCartUseCase;
import com.mabc.e_shop.application.usecase.DeleteCategoryUseCase;
import com.mabc.e_shop.application.usecase.DeleteMarkUseCase;
import com.mabc.e_shop.application.usecase.DeleteProductUseCase;
import com.mabc.e_shop.application.usecase.GetAllCategoriesUseCase;
import com.mabc.e_shop.application.usecase.GetAllMarksUseCase;
import com.mabc.e_shop.application.usecase.GetAllProductsUseCase;
import com.mabc.e_shop.application.usecase.GetCartByIdUseCase;
import com.mabc.e_shop.application.usecase.GetCategoryByIdUseCase;
import com.mabc.e_shop.application.usecase.GetMarkByIdUseCase;
import com.mabc.e_shop.application.usecase.GetProductByIdUseCase;
import com.mabc.e_shop.application.usecase.RemoveItemFromCartUseCase;
import com.mabc.e_shop.application.usecase.SaveCategoryUseCase;
import com.mabc.e_shop.application.usecase.SaveMarkUseCase;
import com.mabc.e_shop.domain.repository.CartRepository;
import com.mabc.e_shop.domain.repository.CategoryRepository;
import com.mabc.e_shop.domain.repository.MarkRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.infrastructure.storage.ImageStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración que ensambla los casos de uso de la capa de aplicación
 * con sus dependencias del dominio.
 *
 * <p>Los casos de uso son clases planas sin anotaciones de Spring; esta
 * configuración los registra como beans e inyecta los adaptadores de
 * repositorio disponibles en el contexto, manteniendo la capa de aplicación
 * libre de dependencias del framework.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Crea el caso de uso que registra o actualiza marcas.
     *
     * @param markRepository repositorio de marcas.
     * @return el caso de uso configurado.
     */
    @Bean
    public SaveMarkUseCase saveMarkUseCase(MarkRepository markRepository) {
        return new SaveMarkUseCase(markRepository);
    }

    /**
     * Crea el caso de uso que registra o actualiza categorías.
     *
     * @param categoryRepository repositorio de categorías.
     * @return el caso de uso configurado.
     */
    @Bean
    public SaveCategoryUseCase saveCategoryUseCase(CategoryRepository categoryRepository) {
        return new SaveCategoryUseCase(categoryRepository);
    }

    /**
     * Crea el caso de uso que elimina una categoría por su identificador,
     * rechazándola si está asociada a productos.
     *
     * @param categoryRepository repositorio de categorías.
     * @param productRepository  repositorio de productos.
     * @return el caso de uso configurado.
     */
    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(
        CategoryRepository categoryRepository,
        ProductRepository productRepository
    ) {
        return new DeleteCategoryUseCase(categoryRepository, productRepository);
    }

    /**
     * Crea el caso de uso que elimina una marca por su identificador,
     * rechazándola si está asociada a productos.
     *
     * @param markRepository     repositorio de marcas.
     * @param productRepository  repositorio de productos.
     * @return el caso de uso configurado.
     */
    @Bean
    public DeleteMarkUseCase deleteMarkUseCase(
        MarkRepository markRepository,
        ProductRepository productRepository
    ) {
        return new DeleteMarkUseCase(markRepository, productRepository);
    }

    /**
     * Crea el caso de uso que crea o actualiza productos.
     *
     * @param productRepository  repositorio de productos.
     * @param categoryRepository repositorio de categorías.
     * @param markRepository     repositorio de marcas.
     * @return el caso de uso configurado.
     */
    @Bean
    public CreateProductUseCase createProductUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        MarkRepository markRepository
    ) {
        return new CreateProductUseCase(productRepository, categoryRepository, markRepository);
    }

    /**
     * Crea el caso de uso que genera carritos de compras nuevos.
     *
     * @param cartRepository repositorio de carritos de compras.
     * @return el caso de uso configurado.
     */
    @Bean
    public CreateCartUseCase createCartUseCase(CartRepository cartRepository) {
        return new CreateCartUseCase(cartRepository);
    }

    /**
     * Crea el caso de uso que agrega productos a un carrito.
     *
     * @param cartRepository    repositorio de carritos de compras.
     * @param productRepository repositorio de productos.
     * @return el caso de uso configurado.
     */
    @Bean
    public AddItemToCartUseCase addItemToCartUseCase(
        CartRepository cartRepository,
        ProductRepository productRepository
    ) {
        return new AddItemToCartUseCase(cartRepository, productRepository);
    }

    /**
     * Crea el caso de uso que elimina un ítem específico de un carrito.
     *
     * @param cartRepository repositorio de carritos de compras.
     * @return el caso de uso configurado.
     */
    @Bean
    public RemoveItemFromCartUseCase removeItemFromCartUseCase(CartRepository cartRepository) {
        return new RemoveItemFromCartUseCase(cartRepository);
    }

    /**
     * Crea el caso de uso que disminuye en una unidad la cantidad de un ítem.
     *
     * @param cartRepository repositorio de carritos de compras.
     * @return el caso de uso configurado.
     */
    @Bean
    public DecrementItemQuantityFromCartUseCase decrementItemQuantityFromCartUseCase(CartRepository cartRepository) {
        return new DecrementItemQuantityFromCartUseCase(cartRepository);
    }

    /**
     * Crea el caso de uso que concreta una compra y rebaja el stock.
     *
     * @param cartRepository    repositorio de carritos de compras.
     * @param productRepository repositorio de productos.
     * @return el caso de uso configurado.
     */
    @Bean
    public CheckoutCartUseCase checkoutCartUseCase(
        CartRepository cartRepository,
        ProductRepository productRepository
    ) {
        return new CheckoutCartUseCase(cartRepository, productRepository);
    }

    /**
     * Crea el caso de uso que consulta todos los productos.
     *
     * @param productRepository repositorio de productos.
     * @return el caso de uso configurado.
     */
    @Bean
    public GetAllProductsUseCase getAllProductsUseCase(ProductRepository productRepository) {
        return new GetAllProductsUseCase(productRepository);
    }

    /**
     * Crea el caso de uso que consulta un producto por su identificador.
     *
     * @param productRepository repositorio de productos.
     * @return el caso de uso configurado.
     */
    @Bean
    public GetProductByIdUseCase getProductByIdUseCase(ProductRepository productRepository) {
        return new GetProductByIdUseCase(productRepository);
    }

    /**
     * Crea el caso de uso que elimina un producto por su identificador.
     *
     * @param productRepository repositorio de productos.
     * @param imageStorage       almacenamiento de las imágenes de los productos.
     * @return el caso de uso configurado.
     */
    @Bean
    public DeleteProductUseCase deleteProductUseCase(
        ProductRepository productRepository,
        ImageStorage imageStorage
    ) {
        return new DeleteProductUseCase(productRepository, imageStorage);
    }

    /**
     * Crea el caso de uso que consulta todas las categorías.
     *
     * @param categoryRepository repositorio de categorías.
     * @return el caso de uso configurado.
     */
    @Bean
    public GetAllCategoriesUseCase getAllCategoriesUseCase(CategoryRepository categoryRepository) {
        return new GetAllCategoriesUseCase(categoryRepository);
    }

    /**
     * Crea el caso de uso que consulta una categoría por su identificador.
     *
     * @param categoryRepository repositorio de categorías.
     * @return el caso de uso configurado.
     */
    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase(CategoryRepository categoryRepository) {
        return new GetCategoryByIdUseCase(categoryRepository);
    }

    /**
     * Crea el caso de uso que consulta todas las marcas.
     *
     * @param markRepository repositorio de marcas.
     * @return el caso de uso configurado.
     */
    @Bean
    public GetAllMarksUseCase getAllMarksUseCase(MarkRepository markRepository) {
        return new GetAllMarksUseCase(markRepository);
    }

    /**
     * Crea el caso de uso que consulta una marca por su identificador.
     *
     * @param markRepository repositorio de marcas.
     * @return el caso de uso configurado.
     */
    @Bean
    public GetMarkByIdUseCase getMarkByIdUseCase(MarkRepository markRepository) {
        return new GetMarkByIdUseCase(markRepository);
    }

    /**
     * Crea el caso de uso que consulta un carrito por su identificador.
     *
     * @param cartRepository repositorio de carritos de compras.
     * @return el caso de uso configurado.
     */
    @Bean
    public GetCartByIdUseCase getCartByIdUseCase(CartRepository cartRepository) {
        return new GetCartByIdUseCase(cartRepository);
    }

    /**
     * Crea el caso de uso que elimina un carrito por su identificador.
     *
     * @param cartRepository repositorio de carritos de compras.
     * @return el caso de uso configurado.
     */
    @Bean
    public DeleteCartUseCase deleteCartUseCase(CartRepository cartRepository) {
        return new DeleteCartUseCase(cartRepository);
    }
}
