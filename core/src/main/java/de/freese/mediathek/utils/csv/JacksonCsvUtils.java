// Created: 09 Nov. 2025
package de.freese.mediathek.utils.csv;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SequenceWriter;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvReadFeature;
import tools.jackson.dataformat.csv.CsvSchema;
import tools.jackson.dataformat.csv.CsvWriteFeature;

/**
 * @author Thomas Freese
 */
public final class JacksonCsvUtils implements CsvUtils {
    static void main() {
        final CsvSchema csvSchema = CsvSchema.builder()
                .addColumn("A")
                .addColumn("B")
                .addColumn("C")
                .setUseHeader(true)
                .setStrictHeaders(true)
                .setColumnSeparator(',')
                .setQuoteChar('"')
                .setLineSeparator(System.lineSeparator())
                // .setEscapeChar('\\')
                .build();

        final ObjectMapper objectMapper = new CsvMapper();

        final Map<String, String> map = Map.of("A", "\"a1\" a1", "B", "b\\1", "C", "c,1");
        final List<Map<String, String>> list = List.of(map);

        objectMapper
                .writerFor(List.class)
                .with(csvSchema)
                .with(CsvWriteFeature.ALWAYS_QUOTE_EMPTY_STRINGS)
                .with(CsvWriteFeature.ALWAYS_QUOTE_NUMBERS)
                .with(CsvWriteFeature.ALWAYS_QUOTE_STRINGS)
                .writeValue(System.out, list);
    }

    @Override
    public List<Map<String, String>> readCsv(final Path file) throws Exception {
        final CsvSchema csvSchema = createDefaultCsvSchemaBuilder().build();

        final ObjectMapper objectMapper = new CsvMapper();

        try (MappingIterator<Map<String, String>> mappingIterator = objectMapper
                .readerFor(Map.class)
                .with(csvSchema)
                .with(CsvReadFeature.EMPTY_STRING_AS_NULL)
                .with(CsvReadFeature.EMPTY_UNQUOTED_STRING_AS_NULL)
                .with(CsvReadFeature.SKIP_EMPTY_LINES)
                .with(CsvReadFeature.TRIM_HEADER_SPACES)
                .with(CsvReadFeature.TRIM_SPACES)
                .readValues(file)) {

            return mappingIterator.readAll();
        }
    }

    @Override
    public void writeCsv(final ResultSet resultSet, final PrintStream ps) throws Exception {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        CsvSchema.Builder csvSchemaBuilder = createDefaultCsvSchemaBuilder();

        // Columns
        for (int column = 1; column <= columnCount; column++) {
            final String columnName = metaData.getColumnLabel(column).toUpperCase();

            csvSchemaBuilder = switch (metaData.getColumnType(column)) {
                case Types.BOOLEAN -> csvSchemaBuilder.addColumn(columnName, CsvSchema.ColumnType.BOOLEAN);
                case Types.NUMERIC -> csvSchemaBuilder.addColumn(columnName, CsvSchema.ColumnType.NUMBER);
                default -> csvSchemaBuilder.addColumn(columnName, CsvSchema.ColumnType.STRING);
            };
        }

        final CsvSchema csvSchema = csvSchemaBuilder.build();
        final ObjectMapper objectMapper = new CsvMapper();

        final ObjectWriter objectWriter = objectMapper.writerFor(Map.class)
                .with(csvSchema)
                .with(CsvWriteFeature.ALWAYS_QUOTE_EMPTY_STRINGS)
                .with(CsvWriteFeature.ALWAYS_QUOTE_NUMBERS)
                .with(CsvWriteFeature.ALWAYS_QUOTE_STRINGS)
                // .writeValue(Path.of("/tmp/test.csv"), list);
                ;

        try (SequenceWriter sequenceWriter = objectWriter.writeValues(ps)) {
            // Values
            while (resultSet.next()) {
                final Map<String, String> row = new LinkedHashMap<>();

                for (int column = 1; column <= columnCount; column++) {
                    final Object obj = resultSet.getObject(column);
                    final String value;

                    if (obj instanceof byte[] bytes) {
                        value = new String(bytes, StandardCharsets.UTF_8);
                    }
                    else {
                        value = Objects.toString(obj, "");
                    }

                    row.put(csvSchema.columnName(column - 1), value);
                }

                sequenceWriter.write(row);
            }

            sequenceWriter.flush();
        }

        ps.flush();

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            resultSet.first();
        }
    }

    private CsvSchema.Builder createDefaultCsvSchemaBuilder() {
        // Schema from POJO (usually has @JsonPropertyOrder annotation)
        // final CsvSchema csvSchema = mapper.schemaFor(Pojo.class);

        // Read schema from the first line; start with bootstrap instance to enable reading of schema from the first line.
        // final CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();

        return CsvSchema.builder()
                .setUseHeader(true)
                .setStrictHeaders(true)
                .setColumnSeparator(',')
                .setQuoteChar('"')
                .setLineSeparator(System.lineSeparator())
                // .setEscapeChar('\\')
                ;
    }
}
