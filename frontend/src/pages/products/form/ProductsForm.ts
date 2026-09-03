import { Template } from './template';
import { ProductService } from '../../../services/productService';
import { marksService } from '../../../services/marksService';
import { categoriesService } from '../../../services/categoriesService';
import { alertMessage } from '../../../utils/dialog';
import type { ProductInterface } from '../../../interfaces/ProductInterface';
import type { MarkInterface } from '../../../interfaces/MarkInterface';
import type { CategoriesInterface } from '../../../interfaces/CategoriesInterface';

/**
 * Web Component de la página del mantenedor de productos (formulario).
 *
 * Sirve tanto para registrar un producto nuevo (`/admin_home/products/new`) como para
 * editar uno existente (`/admin_home/products/edit/{id}`). Usa Light DOM y valida los
 * campos antes de enviarlos como multipart/form-data a la API.
 */
export class ProductsForm extends HTMLElement {

    /** Identificador del producto en modo edición, o `null` en modo creación. */
    private productId: number | null = null;
    /** Datos del producto cargado en modo edición. */
    private product: ProductInterface | null = null;
    /** Marcas disponibles para el select. */
    private marks: MarkInterface[] = [];
    /** Categorías disponibles para el multi-select. */
    private categories: CategoriesInterface[] = [];
    /** Archivo de imagen seleccionado por el usuario. */
    private imageFile: File | null = null;

    static get observedAttributes() {
        return ['title'];
    }

    connectedCallback() {
        this.classList.add('products-form-page');
        this.productId = this.resolveIdFromPath();

        void this.init();
    }

    private resolveIdFromPath(): number | null {
        const segments = window.location.pathname.split('/').filter(Boolean);
        const editIndex = segments.findIndex((seg) => seg === 'edit');
        if (editIndex === -1) {
            return null;
        }
        const id = Number(segments[editIndex + 1]);
        return Number.isInteger(id) && id > 0 ? id : null;
    }

    private async init(): Promise<void> {
        const [marksRes, catsRes] = await Promise.all([
            marksService.getAll(),
            categoriesService.getAll(),
        ]);

        this.marks = (marksRes.ok && marksRes.data?.data) ? marksRes.data.data : [];
        this.categories = (catsRes.ok && catsRes.data?.data) ? catsRes.data.data : [];

        if (this.productId === null) {
            this.renderNew();
            return;
        }

        await this.loadProduct(this.productId);
    }

    private renderNew(): void {
        (new Template(this)).render('Nuevo Producto', null, this.marks, this.categories, false);
        this.configListeners();
    }

    private async loadProduct(id: number): Promise<void> {
        const response = await ProductService.getById(id);

        if (response.ok && response.data?.data) {
            this.product = response.data.data;
            (new Template(this)).render('Editar Producto', this.product, this.marks, this.categories, true);
            this.configListeners();
            return;
        }

        (new Template(this)).render('Editar Producto', null, this.marks, this.categories, false);
        this.configListeners();
        const message = response.ok
            ? 'No se pudo cargar el producto.'
            : (response.data || 'No se pudo cargar el producto.');
        this.showServerError(message);
    }

    configListeners(): void {
        const form = document.getElementById('product-form');
        if (form) {
            form.addEventListener('submit', (event) => {
                event.preventDefault();
                void this.submit();
            });
        }

        const inputName = document.querySelector<HTMLInputElement>('#product-name');
        const divNameError = document.getElementById('product-name_error');
        if (inputName && divNameError) {
            inputName.addEventListener('change', () => {
                divNameError.innerText = '';
                this.clearServerError();
            });
        }

        const fileInput = document.querySelector<HTMLInputElement>('#product-image');
        if (fileInput) {
            fileInput.addEventListener('change', () => {
                this.handleImagePreview(fileInput);
            });
        }
    }

    private handleImagePreview(fileInput: HTMLInputElement): void {
        const file = fileInput.files?.[0];
        const preview = document.getElementById('product-image_preview');
        if (!preview) return;

        if (!file) {
            this.imageFile = null;
            preview.innerHTML = '';
            return;
        }

        const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            preview.innerHTML = '<span class="products-form__error-text">Formato no permitido. Use JPG, PNG o WebP.</span>';
            fileInput.value = '';
            this.imageFile = null;
            return;
        }

        this.imageFile = file;
        const reader = new FileReader();
        reader.onload = () => {
            preview.innerHTML = `<img src="${reader.result}" alt="Preview" class="products-form__img-preview" />`;
        };
        reader.readAsDataURL(file);
    }

    private clearServerError(): void {
        const divError = document.getElementById('product-form_error');
        if (divError) {
            divError.innerText = '';
        }
    }

    private showServerError(message: string): void {
        const divError = document.getElementById('product-form_error');
        if (divError) {
            divError.innerText = message;
        }
    }

    validateData(): boolean {
        let errors = 0;

        const inputName = document.querySelector<HTMLInputElement>('#product-name');
        const divNameError = document.getElementById('product-name_error');
        if (inputName && divNameError) {
            const msg = inputName.value.trim().length < 3 ? 'El nombre debe tener al menos 3 caracteres.' : '';
            divNameError.innerText = msg;
            if (msg) inputName.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        const inputDesc = document.querySelector<HTMLInputElement>('#product-description');
        const divDescError = document.getElementById('product-description_error');
        if (inputDesc && divDescError) {
            const msg = inputDesc.value.trim().length < 3 ? 'La descripción debe tener al menos 3 caracteres.' : '';
            divDescError.innerText = msg;
            if (msg && errors === 0) inputDesc.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        const selectMark = document.querySelector<HTMLSelectElement>('#product-mark');
        const divMarkError = document.getElementById('product-mark_error');
        if (selectMark && divMarkError) {
            const msg = !selectMark.value ? 'Debe seleccionar una marca.' : '';
            divMarkError.innerText = msg;
            if (msg && errors === 0) selectMark.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        const selectCats = document.querySelector<HTMLSelectElement>('#product-categories');
        const divCatsError = document.getElementById('product-categories_error');
        if (selectCats && divCatsError) {
            const selected = Array.from(selectCats.selectedOptions);
            const msg = selected.length === 0 ? 'Debe seleccionar al menos una categoría.' : '';
            divCatsError.innerText = msg;
            if (msg && errors === 0) selectCats.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        const inputStock = document.querySelector<HTMLInputElement>('#product-stock');
        const divStockError = document.getElementById('product-stock_error');
        if (inputStock && divStockError) {
            const val = Number(inputStock.value);
            const msg = inputStock.value === '' || val < 0 ? 'El stock debe ser un número ≥ 0.' : '';
            divStockError.innerText = msg;
            if (msg && errors === 0) inputStock.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        const inputWeight = document.querySelector<HTMLInputElement>('#product-weight');
        const divWeightError = document.getElementById('product-weight_error');
        if (inputWeight && divWeightError) {
            const val = Number(inputWeight.value);
            const msg = inputWeight.value === '' || val <= 0 ? 'El peso debe ser mayor que 0.' : '';
            divWeightError.innerText = msg;
            if (msg && errors === 0) inputWeight.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        const inputCost = document.querySelector<HTMLInputElement>('#product-priceCost');
        const divCostError = document.getElementById('product-priceCost_error');
        if (inputCost && divCostError) {
            const val = Number(inputCost.value);
            const msg = inputCost.value === '' || val < 0 ? 'El precio de costo no puede ser negativo.' : '';
            divCostError.innerText = msg;
            if (msg && errors === 0) inputCost.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        const inputSale = document.querySelector<HTMLInputElement>('#product-priceSale');
        const divSaleError = document.getElementById('product-priceSale_error');
        if (inputSale && divSaleError) {
            const val = Number(inputSale.value);
            const msg = inputSale.value === '' || val < 0 ? 'El precio de venta no puede ser negativo.' : '';
            divSaleError.innerText = msg;
            if (msg && errors === 0) inputSale.focus();
            errors += msg.length > 0 ? 1 : 0;
        }

        if (!this.productId && !this.imageFile) {
            const divImgError = document.getElementById('product-image_error');
            if (divImgError) {
                divImgError.innerText = 'La imagen del producto es obligatoria.';
                errors += 1;
            }
        }

        return errors === 0;
    }

    private buildFormData(): FormData {
        const fd = new FormData();

        const markId = document.querySelector<HTMLSelectElement>('#product-mark')?.value;
        fd.append('markId', markId ?? '');

        const selectCats = document.querySelector<HTMLSelectElement>('#product-categories');
        if (selectCats) {
            Array.from(selectCats.selectedOptions).forEach((opt) => {
                fd.append('categoryIds', opt.value);
            });
        }

        fd.append('name', document.querySelector<HTMLInputElement>('#product-name')?.value.trim() ?? '');
        fd.append('description', document.querySelector<HTMLInputElement>('#product-description')?.value.trim() ?? '');
        fd.append('stock', document.querySelector<HTMLInputElement>('#product-stock')?.value ?? '0');
        fd.append('weight', document.querySelector<HTMLInputElement>('#product-weight')?.value ?? '0');
        fd.append('priceCost', document.querySelector<HTMLInputElement>('#product-priceCost')?.value ?? '0');
        fd.append('priceSale', document.querySelector<HTMLInputElement>('#product-priceSale')?.value ?? '0');

        if (this.imageFile) {
            fd.append('image', this.imageFile);
        }

        return fd;
    }

    private async submit(): Promise<void> {
        if (!this.validateData()) {
            return;
        }

        this.clearServerError();
        const formData = this.buildFormData();

        const response = this.productId === null
            ? await ProductService.create(formData)
            : await ProductService.update(this.productId, formData);

        if (response.ok) {
            await alertMessage(this.productId === null ? 'Producto registrado correctamente.' : 'Producto actualizado correctamente.');
            this.navigate('/admin_home/products');
            return;
        }

        this.showServerError(response.data || 'No se pudo guardar el producto.');
    }

    private navigate(path: string): void {
        window.history.pushState({}, '', path);
        window.dispatchEvent(new PopStateEvent('popstate'));
    }
}

if (!customElements.get('products-form-page')) {
    customElements.define('products-form-page', ProductsForm);
}
