import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HtmlParserTest {

    HtmlParser htmlParser = new HtmlParser();

    @Test
    public void shouldExtractOnlyHttpAndHttpsLinks_whenHtmlContainsVariousSchemes() {
        String html = """
                <a href="https://www.google.com/">Google HTTPS</a>
                <a href="http://www.google.com/">Google HTTP</a>
                <a href="https://www.amazon.de/">Amazon HTTPS</a>
                <a href="http://www.amazon.de/">Amazon HTTP</a>
                <a href="mailto:name@beispiel.de">Mail</a>
                <a href="javascript:alert('Test')">JS</a>
                """;

        Set<String> actualLinks = htmlParser.extractLinks(html, "https://example.com/");
        Set<String> expectedLinks =  Set.of(
                "https://www.google.com/",
                "http://www.google.com/",
                "https://www.amazon.de/",
                "http://www.amazon.de/"
        );

        assertEquals(expectedLinks,actualLinks);
    }

    @Test
    public void shouldStripAnchorFragments_whenExtractingLinks() {
        String html = """
                <a href="https://example.com/article.html#heading-1">Standard Anker</a>
                <a href="https://example.com/article.html#comments">Zweiter Anker</a>
                <a href="https://example.com/search?category=java#result-5">Query + Anker</a>
                <a href="https://example.com/about#">Leerer Anker</a>
                <a href="#top">Relativer Anker</a>
                <a href="https://example.com/contact">Kein Anker</a>
                """;

        Set<String> actualLinks = htmlParser.extractLinks(html, "https://example.com/article");
        Set<String> expectedLinks =  Set.of(
                "https://example.com/article.html",
                "https://example.com/search?category=java",
                "https://example.com/about",
                "https://example.com/article",
                "https://example.com/contact"
        );

        assertEquals(expectedLinks,actualLinks);
    }

    @Test
    public void shouldResolveRelativeUrlsToAbsolute_whenExtractingLinks() {
        String html = """ 
                <a href="/pfad/seite.html">Link</a>
                """;

        Set<String> actualLinks = htmlParser.extractLinks(html, "https://example.com/");
        Set<String> expectedLinks =  Set.of(
                "https://example.com/pfad/seite.html"
        );

        assertEquals(expectedLinks,actualLinks);
    }



    @Test
    public void shouldIgnoreScriptAndStyleTags_whenExtractingText() {
        String html = """
                <script>console.log("Hallo Welt!");</script>
                <style>body { background-color: #f0f0f0; }</style>
                <noscript><p>JS deaktiviert</p></noscript>
                <p>Text</p>
                """;
        String actualText = htmlParser.extractText(html);

        assertEquals("Text", actualText);
    }

    @Test
    public void shouldReturnEmptyString_whenHtmlBodyIsEmptyOrMissing() {
        String html = "";
        String actualText = htmlParser.extractText(html);

        assertEquals("",actualText);
    }
}

