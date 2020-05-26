package nsu.Baiduk;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsmParser {
    private static final Logger LOG = LoggerFactory.getLogger(OsmParser.class);
    private static final String NODE = "node";
    private static final String TAG = "tag";

    public Map<String, Integer> users = new HashMap<>();
    public Map<String, Integer> tags = new HashMap<>();

    private void addCount(Map<String, Integer> changeMap, String val) {
        changeMap.compute(val, (k, v) -> (v == null) ? 1 : v + 1);
    }

    public void process(InputStream inputStream) throws XMLStreamException {
        LOG.info("OSM processing start");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = null;
        try {
            eventReader = factory.createXMLEventReader(inputStream);
            while (eventReader.hasNext()) {
                XMLEvent eventNode = eventReader.nextEvent();
                if (XMLStreamConstants.START_ELEMENT == eventNode.getEventType()) {
                    StartElement startElement = eventNode.asStartElement();
                    if (NODE.equals(startElement.getName().getLocalPart())) {
                        Attribute userAttributeNode = startElement.getAttributeByName(new QName("user"));
                        addCount(users, userAttributeNode.getValue());
                        while (eventReader.hasNext()) {
                            XMLEvent eventTag = eventReader.nextEvent();
                            if (XMLStreamConstants.END_ELEMENT == eventTag.getEventType()
                                    && NODE.equals(eventTag.asEndElement().getName().getLocalPart())) {
                                break;
                            }
                            if (XMLStreamConstants.START_ELEMENT == eventTag.getEventType()) {
                                StartElement startElementTag = eventTag.asStartElement();
                                if (TAG.equals(startElementTag.getName().getLocalPart())) {
                                    Attribute key = startElementTag.getAttributeByName(new QName("k"));
                                    addCount(tags, key.getValue());
                                }
                            }
                        }
                    }
                }
            }
            LOG.info("OSM processing end");
        } finally {
            if(eventReader != null) eventReader.close();
        }
        LOG.info("OSM processing finish");
    }
}