export interface CategoriesInterface {
    data:{
        /** Nombre de la categoría. */
        name: string;
        /** Slug identificador de la categoría. */
        id: number;
        /** URL del endpoint de la categoría. */
        active: boolean;
    }
}