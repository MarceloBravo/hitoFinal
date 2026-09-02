import styles from './style.css?inline';
import type { ProductInterface } from '../../../interfaces/ProductInterface';
import type { MarkInterface } from '../../../interfaces/MarkInterface';
import type { CategoriesInterface } from '../../../interfaces/CategoriesInterface';

const BASE_URL = import.meta.env.VITE_IMAGE_URL;

/**
 * Genera el HTML y los estilos del formulario de productos en el light DOM.
 */
export class Template {
    private host: HTMLElement;

    constructor(host: HTMLElement) {
        this.host = host;
    }

    render(
        heading: string,
        product: ProductInterface | null,
        marks: MarkInterface[],
        categories: CategoriesInterface[],
        isEdit: boolean
    ) {
        const name = product?.name ?? '';
        const description = product?.description ?? '';
        const stock = product?.stock ?? 0;
        const weight = product?.weight ?? 0;
        const priceCost = product?.priceCost ?? 0;
        const priceSale = product?.priceSale ?? 0;
        const selectedMarkId = product?.markId ?? '';
        const selectedCategoryIds = product?.categoryIds ?? [];

        const marksOptions = marks
            .map((m) => `<option value="${m.id}" ${m.id === selectedMarkId ? 'selected' : ''}>${this.escapeHtml(m.name)}</option>`)
            .join('');

        const categoriesOptions = categories
            .map((c) => `<option value="${c.id}" ${selectedCategoryIds.includes(c.id) ? 'selected' : ''}>${this.escapeHtml(c.name)}</option>`)
            .join('');

        let imagePreviewHtml = '';
        if (isEdit && product?.imagePath) {
            const imgSrc = product.imagePath.startsWith('http')
                ? product.imagePath
                : `${BASE_URL}${product.imagePath}`;
            imagePreviewHtml = `<img src="${imgSrc}" alt="${this.escapeHtml(name)}" class="products-form__img-preview" />`;
        }

        const htmlString: string = `
            <div class="products-form__container">
                <h2 class="products-form__title">${heading}</h2>
                <form id="product-form" novalidate>
                    <div class="products-form__row">
                        <div class="products-form__field">
                            <label for="product-name">Nombre</label>
                            <input type="text" id="product-name" name="name" maxlength="100" value="${this.escapeHtml(name)}" class="products-form__input"/>
                            <div class="products-form__error" id="product-name_error"></div>
                        </div>
                        <div class="products-form__field">
                            <label for="product-mark">Marca</label>
                            <select id="product-mark" name="markId" class="products-form__select">
                                <option value="">-- Seleccionar marca --</option>
                                ${marksOptions}
                            </select>
                            <div class="products-form__error" id="product-mark_error"></div>
                        </div>
                    </div>

                    <div class="products-form__field">
                        <label for="product-description">Descripción</label>
                        <textarea id="product-description" name="description" maxlength="500" rows="3" class="products-form__textarea">${this.escapeHtml(description)}</textarea>
                        <div class="products-form__error" id="product-description_error"></div>
                    </div>

                    <div class="products-form__field">
                        <label for="product-categories">Categorías</label>
                        <select id="product-categories" name="categoryIds" multiple class="products-form__select products-form__select--multi" size="${Math.min(categories.length, 5) || 3}">
                            ${categoriesOptions}
                        </select>
                        <div class="products-form__hint">Mantén Ctrl (Cmd en Mac) para seleccionar varias</div>
                        <div class="products-form__error" id="product-categories_error"></div>
                    </div>

                    <div class="products-form__row">
                        <div class="products-form__field">
                            <label for="product-stock">Stock</label>
                            <input type="number" id="product-stock" name="stock" min="0" value="${stock}" class="products-form__input"/>
                            <div class="products-form__error" id="product-stock_error"></div>
                        </div>
                        <div class="products-form__field">
                            <label for="product-weight">Peso (kg)</label>
                            <input type="number" id="product-weight" name="weight" min="0" step="0.1" value="${weight}" class="products-form__input"/>
                            <div class="products-form__error" id="product-weight_error"></div>
                        </div>
                    </div>

                    <div class="products-form__row">
                        <div class="products-form__field">
                            <label for="product-priceCost">Precio Costo</label>
                            <input type="number" id="product-priceCost" name="priceCost" min="0" step="0.01" value="${priceCost}" class="products-form__input"/>
                            <div class="products-form__error" id="product-priceCost_error"></div>
                        </div>
                        <div class="products-form__field">
                            <label for="product-priceSale">Precio Venta</label>
                            <input type="number" id="product-priceSale" name="priceSale" min="0" step="0.01" value="${priceSale}" class="products-form__input"/>
                            <div class="products-form__error" id="product-priceSale_error"></div>
                        </div>
                    </div>

                    <div class="products-form__field">
                        <label for="product-image">Imagen del producto</label>
                        <input type="file" id="product-image" name="image" accept="image/png,image/jpeg,image/webp" class="products-form__file"/>
                        <div class="products-form__error" id="product-image_error"></div>
                        <div id="product-image_preview" class="products-form__img-container">${imagePreviewHtml}</div>
                    </div>

                    <div class="products-form__error products-form__server" id="product-form_error" role="alert"></div>

                    <div class="products-form__actions">
                        <button type="submit" class="products-form__btn products-form__btn--primary" id="btn-save">Guardar</button>
                        <a href="/admin_home/products" data-link>
                            <button type="button" class="products-form__btn products-form__btn--ghost" id="btn-cancel">Cancelar</button>
                        </a>
                    </div>
                </form>
            </div>
        `;

        const fragment = document.createRange().createContextualFragment(htmlString);
        const style = document.createElement('style');
        style.textContent = styles;

        this.host.replaceChildren(style, fragment);

        return this.host;
    }

    private escapeHtml(value: string): string {
        return value
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    }
}
