import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class BiWordIndex {
    HashMap<String, DictEntry> index;
    BiWordIndex() { index = new HashMap<>(); }
    public void createBiWordIndex(String[] files,String indexFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile));
        int docId = 1;
        try {
            for (String filename : files) {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split("\\W+");
                    for (int i = 0; i < words.length; i++) {
                        if (!isStopWord(words[i]) && !containsNumber(words[i])) {
                            String term = words[i].toLowerCase();
                            if (!index.containsKey(term)) {
                                index.put(term, new DictEntry(docId));
                                index.get(term).doc_freq++;
                            }
                            writer.write(term + " : doc(" + docId + ")" + "\n" );
                            if (i < words.length - 1 && !isStopWord(words[i + 1]) && !containsNumber(words[i + 1])) {
                                String biword = term + "_" + words[i + 1].toLowerCase();
                                if (!index.containsKey(biword)) {
                                    index.put(biword, new DictEntry(docId));
                                    index.get(biword).doc_freq++;
                                }
                                writer.write(biword + " : doc(" + docId + ")" + "\n" );
                            } else if (i < words.length - 2 && (isStopWord(words[i + 1]) || containsNumber(words[i + 1]))) {
                                int j = i + 2;
                                while (j < words.length && (isStopWord(words[j]) || containsNumber(words[j]))) {
                                    j++;
                                }
                                if (j < words.length) {
                                    String biword = term + "_" + words[j].toLowerCase();
                                    if (!index.containsKey(biword)) {
                                        index.put(biword, new DictEntry(docId));
                                        index.get(biword).doc_freq++;
                                    }
                                    writer.write(biword + " : doc(" + docId + ")" + "\n" );
                                }
                                i = j - 2;
                            }
                        }
                    }
                }
                docId++;
                reader.close();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isStopWord(String word) {
        String[] stopWords = {"a", "an", "the", "and", "or", "but",
                "for", "to", "in", "on", "at", "with", "by", "."};
        return Arrays.asList(stopWords).contains(word.toLowerCase());
    }

    public static boolean containsNumber(String word) {
        return word.matches(".*\\d.*");
    }
    public void printPostingList() {
        TreeMap<String, DictEntry> sortedDict = new TreeMap<>(index);
        for (Map.Entry<String, DictEntry> term : sortedDict.entrySet()) {
            System.out.print(term.getKey() + " : doc(");
            Posting poslist = term.getValue().postList;
            List<String> docIds = new ArrayList<>();
            while (poslist != null) {
                docIds.add(String.valueOf(poslist.docId));
                poslist = poslist.next;
            }
            System.out.println(String.join(", ", docIds) + ")");
        }
    }

    public void SearchIndex(String query) {
        List<String> singleWords = new ArrayList<>();
        List<String> biwords = new ArrayList<>();
        boolean isExist = true;

        // Separate single words and biwords
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                String phrase = matcher.group(1).toLowerCase();
                String[] words = phrase.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    if (i < words.length - 1 && !isStopWord(words[i]) && !isStopWord(words[i + 1]) && !containsNumber(words[i + 1])) {
                        String biword = words[i] + "_" + words[i + 1];
                        biwords.add(biword);
                        if (index.get(biword) == null) {
                            isExist = false;
                        }
                    } else if (i < words.length - 2 && (isStopWord(words[i + 1]) || containsNumber(words[i + 1]))) {
                        int j = i + 2;
                        while (j < words.length && (isStopWord(words[j]) || containsNumber(words[j]))) {
                            j++;
                        }
                        if (j < words.length) {
                            String biword = words[i] + "_" + words[j];
                            biwords.add(biword);
                            if (index.get(biword) == null) {
                                isExist = false;
                            }
                        }
                        i = j - 2;
                    }
                }
            } else {
                // Single word
                String singleWord = matcher.group(2).toLowerCase();
                if (!isStopWord(singleWord) && !containsNumber(singleWord)) {
                    singleWords.add(singleWord);
                    if (index.get(singleWord) == null) {
                        isExist = false;
                    }
                }
            }
        }

        // If query contains words not in index
        if (!isExist) {
            System.out.println("No doc contains this query.");
            return;
        }

        Set<Integer> matchingDocuments = new HashSet<>();
        // Match documents for single words
        for (String word : singleWords) {
            DictEntry entry = index.get(word);
            if (entry != null) {
                Posting poslist = entry.postList;
                while (poslist != null) {
                    matchingDocuments.add(poslist.docId);
                    poslist = poslist.next;
                }
            }
        }

        // Match documents for biwords
        for (String biword : biwords) {
            DictEntry entry = index.get(biword);
            if (entry != null) {
                Posting poslist = entry.postList;
                while (poslist != null) {
                    matchingDocuments.add(poslist.docId);
                    poslist = poslist.next;
                }
            }
        }

        if (!matchingDocuments.isEmpty()) {
            System.out.print("Documents containing the query: ");
            System.out.println(matchingDocuments);
        } else {
            System.out.println("No documents contain this query.");
        }
    }

}





