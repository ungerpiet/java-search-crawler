import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Crawler {

    private final int MAX_PAGES = 500;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final AtomicInteger pagesCrawled = new AtomicInteger(0);
    private final AtomicInteger activeTasks = new AtomicInteger(0);

    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private final HtmlParser parser = new HtmlParser();
    private final HtmlFetcher fetcher = new HtmlFetcher();
    private final InvertedIndex index = new InvertedIndex();

    private String targetHost;

    public void submitTask(String url) {
        if (pagesCrawled.get() >= MAX_PAGES) {
            return;
        }

        try {
            String linkHost = URI.create(url).getHost();
            if (targetHost != null && !targetHost.equalsIgnoreCase(linkHost)) {
                return;
            }
        } catch (IllegalArgumentException _) {
            return;
        }

        if (!visitedUrls.add(url)) {
            return;
        }

        activeTasks.incrementAndGet();
        executor.submit(() -> {
            try {
                if (pagesCrawled.get() >= MAX_PAGES) {
                    return;
                }
                Optional<String> htmlContent = fetcher.fetch(url);
                if (htmlContent.isPresent()) {
                    int currentCount = pagesCrawled.incrementAndGet();
                    if (currentCount <= MAX_PAGES) {
                        System.out.println("[" + Thread.currentThread().getName() + "] (" + currentCount + "/" + MAX_PAGES + ") " + url);
                        String html = htmlContent.get();
                        Set<String> discoveredLinks = parser.extractLinks(html, url);

                        for (String link : discoveredLinks) {
                                submitTask(link);
                        }

                        index.addDocument(url, parser.extractText(html));
                    }
                }
            } finally {
                activeTasks.decrementAndGet();
            }
        });
    }

    public void crawl(String startUrl) throws InterruptedException {
            this.targetHost = URI.create(startUrl).getHost();

            submitTask(startUrl);

            while (activeTasks.get() > 0 && pagesCrawled.get() < MAX_PAGES) {
                Thread.sleep(50);
            }

            executor.shutdownNow();
    }

    public InvertedIndex getIndex() {
        return this.index;
    }

    public static void main(String[] args) throws InterruptedException {
        Crawler crawler = new Crawler();
        crawler.crawl("https://books.toscrape.com/");

        System.out.println("\nCrawl abgeschlossen. Suchbegriff eingeben (oder 'exit'):");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input.trim())) {
                break;
            }

            Set<String> results = crawler.getIndex().search(input);
            if (results.isEmpty()) {
                System.out.println("Keine Treffer gefunden.");
            } else {
                System.out.println(results.size() + " Treffer:");
                results.forEach(url -> System.out.println(" - " + url));
            }
        }
    }
}
