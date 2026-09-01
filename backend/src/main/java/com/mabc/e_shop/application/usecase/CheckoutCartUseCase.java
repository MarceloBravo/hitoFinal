package com.mabc.e_shop.application.usecase;

import com.mabc.e_shop.domain.entity.Cart;
import com.mabc.e_shop.domain.entity.CartItem;
import com.mabc.e_shop.domain.entity.Product;
import com.mabc.e_shop.domain.exception.ResourceNotFoundException;
import com.mabc.e_shop.domain.repository.CartRepository;
import com.mabc.e_shop.domain.repository.ProductRepository;
import com.mabc.e_shop.domain.valueobject.Quantity;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso que concreta una compra (checkout ficticio) de un carrito.
 *
 * <p>Por cada ítem del carrito rebaja el stock del producto en la cantidad
 * comprada y luego elimina el carrito. El stock solo se descuenta aquí, al
 * <b>concretar la compra</b>, y no al agregar productos al carrito.
 *
 * <p>La atomicidad de la operación (si un producto no tiene stock suficiente,
 * no se descuenta ninguno) la garantiza la transacción declarada a nivel del
 * controlador {@code @Transactional}, ya que este caso de uso es una clase
 * plana sin anotaciones de Spring.
 */
public class CheckoutCartUseCase {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Crea el caso de uso con los repositorios necesarios.
     *
     * @param cartRepository    repositorio de carritos de compras.
     * @param productRepository repositorio de productos.
     */
    public CheckoutCartUseCase(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Resultado del checkout con el resumen de la compra realizada.
     *
     * @param cartId    identificador del carrito concretado.
     * @param total     monto total de la compra.
     * @param itemCount cantidad total de unidades compradas.
     * @param products  identificadores de los productos cuyas existencias se rebajaron.
     */
    public record CheckoutResult(Long cartId, double total, int itemCount, List<Long> products) {
    }

    /**
     * Concreta la compra del carrito indicado.
     *
     * <p>Valida que el carrito exista y que haya stock suficiente para cada
     * producto; rebaja el stock y elimina el carrito al finalizar.
     *
     * @param cartId identificador del carrito a concretar.
     * @return resumen del checkout realizado.
     * @throws ResourceNotFoundException si el carrito no existe.
     * @throws IllegalStateException     si algún producto no tiene stock suficiente.
     */
    public CheckoutResult execute(Long cartId) {
        if (cartId == null) {
            throw new ResourceNotFoundException("El carrito no existe o no es válido.");
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("El carrito no existe o no es válido."));

        List<Long> updatedProducts = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "El producto " + item.getProduct().getName().value() + " ya no está disponible."));

            product.reduceStock(new Quantity(item.getQuantity().value()));
            productRepository.save(product);
            updatedProducts.add(product.getId());
        }

        cartRepository.deleteById(cartId);

        return new CheckoutResult(
                cartId,
                cart.getSubTotal(),
                cart.getItems().stream().mapToInt(i -> i.getQuantity().value()).sum(),
                updatedProducts
        );
    }
}
