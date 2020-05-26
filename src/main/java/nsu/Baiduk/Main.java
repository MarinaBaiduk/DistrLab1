package nsu.Baiduk;

import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Map;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static void showStatistic(Map<String, Integer> statistic) {
        statistic.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .forEach(e -> System.out.printf("%s - %d%n", e.getKey(), e.getValue()));
    }
    public static void main(String[] args) {
        LOG.info("start file decompressing");
        try (InputStream inputStream = new BZip2CompressorInputStream(new FileInputStream(args[0]))) {
            LOG.info("end file decompressing");
            LOG.info("start osm parse");
            OsmParser osmParser = new OsmParser();
            osmParser.process(inputStream);
            LOG.info("end osm parse");
            System.out.println("user - edits:");
            showStatistic(osmParser.users);
            System.out.println("key - count tag:");
            showStatistic(osmParser.tags);
        } catch (XMLStreamException e) {
            LOG.error("xml error", e);
        } catch (IOException e) {
            LOG.error("file error", e);
        }
    }
}



