package uy.gub.imm.sae.web.common;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static uy.gub.imm.sae.web.common.CsvReport.HEADERS;

public class CsvReportTest {

    @Test
    public void givenCsvRows_whenConvertToCSV_thenConvertToByteArray() {
        List<CsvRow> rows = Arrays.asList(new CsvRow(1, "Actualizado correctamente"), new CsvRow(2, "No pudo ser actualizado"));
        CsvReport report = new CsvReport();
        byte[] resultExpected = (String.format("%s,%s%n", HEADERS[0], HEADERS[1]) +
                String.format("1,Actualizado correctamente%n") +
                String.format("2,No pudo ser actualizado%n")).getBytes(StandardCharsets.UTF_8);

        byte[] result = report.convertToCSV(rows);

        assertThat(result).isNotNull().isEqualTo(resultExpected);
    }

}