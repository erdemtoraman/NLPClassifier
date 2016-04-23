import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Atakan Arıkan on 23.04.2016.
 */
public class Main {
    public static HashMap<Integer, Document> documents = new HashMap<>();
    public static HashMap<String, Double> vocabulary = new HashMap<>();
    public static HashMap<String, ArrayList<Document>> categoriesToDocs = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        readFiles();
        //calculateAndAssigntfidf();
        //  deserialize();
        findClassDocuments();
        HashMap<String, HashMap<String, Double>> temp = Rocchio.categoriesCentroids;
        System.out.println();
    }

    public static void deserialize() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream("tf-idf.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        documents = (HashMap<Integer, Document>) in.readObject();
        in.close();
        fileIn.close();

        fileIn = new FileInputStream("categories-centroids.ser");
        in = new ObjectInputStream(fileIn);
        Rocchio.categoriesCentroids = (HashMap<String, HashMap<String, Double>>) in.readObject();
        in.close();
        fileIn.close();
    }
    public static void findClassDocuments(){
        HashSet<String> h = new HashSet<>();
        for (int id : documents.keySet()) {
            Document current = documents.get(id);
            System.out.println();
            h.add(current.getCategory());
            if(!categoriesToDocs.keySet().contains(current.getCategory())) {
                ArrayList<Document> temp = new ArrayList<>();
                temp.add(current);
                categoriesToDocs.put(current.getCategory(), temp);
            } else {
                categoriesToDocs.get(current.getCategory()).add(current);
            }
        }
        int x =6;
    }
    public static void readFiles() throws IOException {
        BufferedReader read = new BufferedReader(new FileReader(new File("REUTERS_training_data.dat")));
        String str;
        int currentDocId = 0;
        HashMap<String, Double> currentTf = new HashMap<>();
        while ((str = read.readLine()) != null) {
            if (str.startsWith(".I ")) {
                currentDocId = Integer.parseInt(str.substring(str.indexOf(" ") + 1));
                currentTf = new HashMap<>();
                str = read.readLine();
                str = read.readLine();
            }
            if (str.equals("")) {
                for (String w : currentTf.keySet()) {
                    currentTf.put(w, currentTf.get(w) / (double) currentTf.keySet().size());
                    if (vocabulary.keySet().contains(w)) {
                        vocabulary.put(w, vocabulary.get(w) + 1);
                    } else {
                        vocabulary.put(w, 1.0);
                    }
                }

                documents.put(currentDocId, new Document(currentDocId, currentTf));
            } else {
                String[] words = str.split(" ");
                for (String w : words) {
                    if (currentTf.keySet().contains(w)) {
                        currentTf.put(w, currentTf.get(w) + 1);
                    } else {
                        currentTf.put(w, 1.0);
                    }
                }
            }

        }
        read = new BufferedReader(new FileReader(new File("REUTERS_categories.dat")));
        HashSet<String> h = new HashSet<>();
        while ((str = read.readLine()) != null) {
            String[] line = str.split(" ");
            if (documents.get(Integer.parseInt(line[1])) != null ) {
                documents.get(Integer.parseInt(line[1])).setCategory(line[0]);
                System.out.println(line[0] + " " + line[1]);
                h.add(line[0]);
            }
        }
        read.close();
    }


    public static void calculateAndAssigntfidf() {
        for (int docId : documents.keySet()) {
            Document current = documents.get(docId);
            for (String word : vocabulary.keySet()) {
                double idf = Math.log((double) documents.size() / vocabulary.get(word));
                if (current.getTfidfVector().get(word) != null) {
                    double tf = current.getTfidfVector().get(word);
                    current.getTfidfVector().put(word, tf * idf);
                }
            }
        }
    }
}
