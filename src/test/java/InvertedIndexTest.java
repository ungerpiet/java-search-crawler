import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class InvertedIndexTest {

    private InvertedIndex index = new InvertedIndex();

    @BeforeEach
    void setUp() {
        index = new InvertedIndex();
    }

    @Test
    void searchReturnsUrlWhenWordMatches() {
        index.addDocument("https://example.com", "Java Concurrency");

        Set<String> result = index.search("java");

        assertEquals(1, result.size());
        assertTrue(result.contains("https://example.com"));
    }
}