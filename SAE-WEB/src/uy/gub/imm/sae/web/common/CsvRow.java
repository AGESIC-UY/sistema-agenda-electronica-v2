package uy.gub.imm.sae.web.common;

public class CsvRow {
    private final int id;
    private final String mensaje;
    public CsvRow(int id, String mensaje) {
        this.id = id;
        this.mensaje = mensaje;
    }

    public int getId() {
        return id;
    }

    public String getMensaje() {
        return mensaje;
    }
}
