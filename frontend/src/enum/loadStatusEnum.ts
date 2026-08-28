/**
 * Estados de carga de la interfaz.
 */
export enum LoadStatus {
  /** La interfaz está cargando datos desde el servidor. */
  LOADING = "LOADING",
  /** Los datos se cargaron correctamente. */
  SUCCESS = "SUCCESS",
  /** Ocurrió un error al cargar los datos. */
  ERROR = "ERROR"
}
