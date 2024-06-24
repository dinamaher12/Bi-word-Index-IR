public class DictEntry {
    int doc_freq = 0;
    Posting postList;

    DictEntry(int docId) {
        postList = new Posting(docId);
        doc_freq++;
    }
}
