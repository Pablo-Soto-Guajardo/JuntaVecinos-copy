package cl.duoc.juntazaror.reservas.model;

import cl.duoc.juntazaror.reservas.exception.BadRequestException;

/**
 * Catálogo de dependencias comunitarias que se pueden arrendar.
 * Capacidad y tarifa son valores FICTICIOS (restricción del caso).
 */
public enum Dependencia {
    SALON_PRINCIPAL("Salón principal", 80, 35000),
    SALA_REUNIONES("Sala de reuniones", 20, 12000),
    MULTICANCHA("Multicancha", 40, 18000);

    private final String nombre;
    private final int capacidad;
    private final long tarifaPorBloque;

    Dependencia(String nombre, int capacidad, long tarifaPorBloque) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tarifaPorBloque = tarifaPorBloque;
    }

    public String getNombre() { return nombre; }
    public int getCapacidad() { return capacidad; }
    public long getTarifaPorBloque() { return tarifaPorBloque; }

    /** Convierte el texto recibido ("SALON_PRINCIPAL") en la dependencia; si no existe lanza 400. */
    public static Dependencia desdeCodigo(String codigo) {
        for (Dependencia d : values()) {
            if (d.name().equals(codigo)) return d;
        }
        throw new BadRequestException("Dependencia inválida: " + codigo);
    }
}
