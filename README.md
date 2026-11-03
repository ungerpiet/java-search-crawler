# Java Search Crawler

Ein multithreaded Web-Crawler, der Webseiten parallel durchsucht und Suchanfragen über einen Inverted Index verarbeitet.

## Features

- Paralleles Crawlen mit mehreren Threads
- HTML-Parsing mit JSoup
- Speichereffizienter Inverted Index für O(1)-Lookups

## Quickstart

Voraussetzung: Java 23 und Maven.

```bash
mvn compile
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=Crawler
```
