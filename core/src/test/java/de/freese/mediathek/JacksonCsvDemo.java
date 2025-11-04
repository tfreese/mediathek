// Created: 04 Nov. 2025
package de.freese.mediathek;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvReadFeature;
import tools.jackson.dataformat.csv.CsvSchema;
import tools.jackson.dataformat.csv.CsvWriteFeature;

/**
 * <a href="https://github.com/FasterXML/jackson-dataformats-text/tree/3.x/csv">jackson-dataformats-text</a>
 *
 * @author Thomas Freese
 */
public final class JacksonCsvDemo {
    // private static final Logger LOGGER = LoggerFactory.getLogger(JacksonCsvDemo.class);

    static void main() {
        // Schema from POJO (usually has @JsonPropertyOrder annotation)
        // final CsvSchema csvSchema = mapper.schemaFor(Pojo.class);

        // Read schema from the first line; start with bootstrap instance
        // to enable reading of schema from the first line
        // NOTE: reads schema and uses it for binding
        // final CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();

        // Manually-built schema: one with type, others default to "STRING"
        final CsvSchema csvSchema = CsvSchema.builder()
                .addColumn("ARTIST")
                .addColumn("SONG")
                .addColumn("PLAYCOUNT", CsvSchema.ColumnType.NUMBER)
                .setUseHeader(true)
                .setStrictHeaders(true)
                .setColumnSeparator(',')
                .setQuoteChar('"')
                .setLineSeparator(System.lineSeparator())
                // .setEscapeChar('\\')
                .build();

        final ObjectMapper objectMapper = new CsvMapper();
        // objectMapper.readerFor(Pojo.class).with(bootstrapSchema).readValue(csv);

        final List<Map<String, String>> data;

        try (MappingIterator<Map<String, String>> mappingIterator = objectMapper
                .readerFor(Map.class)
                .with(csvSchema)
                .with(CsvReadFeature.EMPTY_STRING_AS_NULL)
                .with(CsvReadFeature.EMPTY_UNQUOTED_STRING_AS_NULL)
                .with(CsvReadFeature.SKIP_EMPTY_LINES)
                .with(CsvReadFeature.SKIP_EMPTY_LINES)
                .with(CsvReadFeature.SKIP_EMPTY_LINES)
                .with(CsvReadFeature.TRIM_HEADER_SPACES)
                .with(CsvReadFeature.TRIM_SPACES)
                .readValues(Path.of("/home/tommy/dokumente/linux/musik-report-strawberry.csv"))) {

            data = mappingIterator.readAll();

            // while (mappingIterator.hasNext()) {
            //     final Map<String, String> row = mappingIterator.next();
            //
            //     if (row.get("ARTIST").contains("Louie Vega")) {
            //         LOGGER.info("{}", row);
            //     }
            // }
        }

        objectMapper
                .writerFor(List.class)
                .with(csvSchema)
                .with(CsvWriteFeature.ALWAYS_QUOTE_EMPTY_STRINGS)
                .with(CsvWriteFeature.ALWAYS_QUOTE_NUMBERS)
                .with(CsvWriteFeature.ALWAYS_QUOTE_STRINGS)
                .writeValue(Path.of("/tmp/test.csv"), data);
    }

    private JacksonCsvDemo() {
        super();
    }
}
