import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class HtmlParser {

    public Set<String> extractLinks(String html, String baseUrl) {
        Set<String> links = new HashSet<>();
        Document doc = Jsoup.parse(html, baseUrl);
        Elements linkElements = doc.select("a[href]");

        for (Element link : linkElements) {

            String absUrl = link.attr("abs:href").trim();

            if (isValidHttpUrl(absUrl)) {

                links.add(absUrl.split("#")[0]);
            }
        }
        return links;
    }


    public String extractText(String html) {
        Document doc = Jsoup.parse(html);

        doc.select("script, style, noscript").remove();
        return doc.body() != null ? doc.body().text() : "";
    }

    private boolean isValidHttpUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
