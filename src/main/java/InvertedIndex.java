import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndex {

    private final Map<String, Set<String>> index = new ConcurrentHashMap<>();

    public void addDocument(String url, String rawText){
        if (rawText == null || rawText.isBlank()) {
            return;
        }

        String[] words = tokenize(rawText);
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));

        for (String word : uniqueWords) {
            if (word.isBlank() || word.length() < 2) {
                continue;
            }
            index.computeIfAbsent(word, _ -> ConcurrentHashMap.newKeySet()).add(url);
        }
    }

    public Set<String> search(String query){
        if (query == null || query.isBlank()) {
            return Collections.emptySet();
        }

        String cleanWord = query.toLowerCase().replaceAll("[^a-zA-Z0-9äöüÄÖÜß\\s]", "");
        return index.getOrDefault(cleanWord, Collections.emptySet());
    }

    private String[] tokenize(String text) {
        text = text.toLowerCase().replaceAll("[^a-zA-Z0-9äöüÄÖÜß\\s]", "");
        return text.split("\\s+");
    }
}
