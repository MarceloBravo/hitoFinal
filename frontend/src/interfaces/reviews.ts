/**
 * Representa una reseña de producto devuelta por la API.
 */
export interface reviews {
    "rating": number;
    "comment": string;
    "date": Date,
    "reviewerName": string;
    "reviewerEmail": string;
}