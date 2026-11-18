package cl.duoc.juntazaror.finanzas.model;

import cl.duoc.juntazaror.finanzas.exception.BadRequestException;

/** Cada categoría pertenece a un tipo: INGRESO o GASTO. */
public enum CategoriaMovimiento {
    ARRIENDO("INGRESO"),
    CUOTA_SOCIAL("INGRESO"),
    OTRO_INGRESO("INGRESO"),
    LUZ("GASTO"),
    AGUA("GASTO"),
    GAS("GASTO"),
    MANTENCION("GASTO"),
    OTRO_GASTO("GASTO");

    private final String tipo;

    CategoriaMovimiento(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() { return tipo; }

    public static CategoriaMovimiento desdeCodigo(String codigo) {
        for (CategoriaMovimiento c : values()) {
            if (c.name().equals(codigo)) return c;
        }
        throw new BadRequestException("Categoría inválida: " + codigo);
    }
}
