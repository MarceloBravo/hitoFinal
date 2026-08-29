package com.mabc.e_shop;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConsoleCoverageReporter {
    public static void main(String[] args) throws Exception {
        String reportPath = args.length > 0 ? args[0] : "target/site/jacoco/jacoco.xml";
        File reportFile = new File(reportPath);

        if (!reportFile.exists()) {
            System.out.println("[coverage] No report found at " + reportFile.getAbsolutePath());
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        Document document = factory.newDocumentBuilder().parse(reportFile);

        Element root = document.getDocumentElement();
        Map<String, Counter> counters = new LinkedHashMap<>();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if ("counter".equals(element.getTagName())) {
                    counters.put(element.getAttribute("type"), new Counter(
                            Integer.parseInt(element.getAttribute("missed")),
                            Integer.parseInt(element.getAttribute("covered"))
                    ));
                }
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("[coverage summary]");
        if (counters.isEmpty()) {
            System.out.println("No coverage counters were found.");
            return;
        }
        


        for (Map.Entry<String, Counter> entry : counters.entrySet()) {
            if (!"INSTRUCTION".equals(entry.getKey())
                    && !"LINE".equals(entry.getKey())
                    && !"BRANCH".equals(entry.getKey())) {
                continue;
            }

            Counter counter = entry.getValue();
            double total = counter.covered + counter.missed;
            double percent = total == 0 ? 100.0 : (counter.covered * 100.0) / total;

            System.out.printf("%-20s %-8.2f%% (%d/%d)%n",
                    entry.getKey(),
                    percent,
                    counter.covered,
                    (int) total);
        }
        System.out.println("--------------------------------------------------");
    }

    private static class Counter {
        private final int missed;
        private final int covered;

        private Counter(int missed, int covered) {
            this.missed = missed;
            this.covered = covered;
        }
    }
}
