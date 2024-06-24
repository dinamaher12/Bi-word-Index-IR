import java.util.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args)  throws IOException {
        Scanner scanner = new Scanner(System.in);
        BiWordIndex biwordIndex=new BiWordIndex();
        String indexFile = "Bi-wordIndex.txt";
        String[] files;
        files = new String[]
                // Change this documents paths and put your own.
                {"C:\\Users\\Documents\\one.txt", "C:\\Users\\Documents\\two.txt"};
        biwordIndex.createBiWordIndex(files, indexFile);
        biwordIndex.printPostingList();
        boolean continueSearch = true;
        while (continueSearch) {
            System.out.print("\nEnter query or '0' to exit: ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("0")) {
                continueSearch = false;
            } else {
                biwordIndex.SearchIndex(query);
            }
        }
        scanner.close();
    }
}

