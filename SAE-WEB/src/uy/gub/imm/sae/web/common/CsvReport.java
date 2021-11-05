package uy.gub.imm.sae.web.common;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvReport {
    static final String[] HEADERS = {"id_recurso", "comentario"};

    public byte[] convertToCSV(List<CsvRow> rows) {
        StringBuilder contents = new StringBuilder();
        contents.append(String.format("%s,%s%n", HEADERS[0], HEADERS[1]));
        for (CsvRow row : rows) {
            contents.append(String.format("%s,%s%n", row.getId(), row.getMensaje()));
        }
        return contents.toString().getBytes(StandardCharsets.UTF_8);
    }
}
