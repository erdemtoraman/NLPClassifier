import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Atakan Arıkan on 23.04.2016.
 */
public class Main {
    public static HashMap<Integer, Document> documents = new HashMap<>();
    public static HashMap<String, Double> vocabulary = new HashMap<>();
    public static HashMap<String, ArrayList<Document>> categoriesToDocs = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
       //serializeAll();
        deserialize();
        System.out.println("Deserialize is over");

        int total = 0;
        int win = 0;
        long start = System.currentTimeMillis();
        for (int id : documents.keySet()) {
            total++;
            Document current = documents.get(id);
            String winner = NaiveBayes.guess(current);
            if (current.getCategories().contains(winner)) {
                win++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("NAIVE TOTALDE 23K elemanda "+ (end-start));
        System.out.println((double) win / (double) total);
        System.out.println();


     //
        //   SVM svm = new SVM();
    }

    public static void serializeAll() throws IOException {

       long start = System.currentTimeMillis();
        readFiles();
        long end = System.currentTimeMillis();
        System.out.println("Reading files took: "+ (end-start));

        start = System.currentTimeMillis();
        calculateAndAssigntfidf();
        end = System.currentTimeMillis();
        System.out.println("CalculateTf-idfValues: "+ (end-start));

        start = System.currentTimeMillis();
        findClassDocuments();
        end = System.currentTimeMillis();
        System.out.println("findClassDocs: "+ (end-start));

        start = System.currentTimeMillis();
         NaiveBayes.calculateTFIDFValuesOfAllCategories();
        end = System.currentTimeMillis();
        System.out.println("NaiveBayes training: "+ (end-start));
        start = System.currentTimeMillis();
        Rocchio.calculateAllCentroids();
        end = System.currentTimeMillis();
        System.out.println("Rocchio training: "+ (end-start));
        start = System.currentTimeMillis();
        Neural_Network.train(50);
        end = System.currentTimeMillis();
        System.out.println("Neural training: "+ (end-start));
        serialize();

    }

    public static void serialize() throws IOException {
        FileOutputStream fileOut = new FileOutputStream("tf-idf.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(documents);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("categories-centroids.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(Rocchio.categoriesCentroids);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("vocabulary.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(vocabulary);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("categories-docs.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(categoriesToDocs);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("category-tf-idf.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(NaiveBayes.categoriesTFIDF);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("totalNumberOfWords.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(NaiveBayes.totalNumberOfWords);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("neural-network.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(Neural_Network.neuralNetwork);
        out.close();
        fileOut.close();
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
        fileIn = new FileInputStream("vocabulary.ser");
        in = new ObjectInputStream(fileIn);
        vocabulary = (HashMap<String, Double>) in.readObject();
        in.close();
        fileIn.close();
        fileIn = new FileInputStream("categories-docs.ser");
        in = new ObjectInputStream(fileIn);
        categoriesToDocs = (HashMap<String, ArrayList<Document>>) in.readObject();
        in.close();
        fileIn.close();
        fileIn = new FileInputStream("category-tf-idf.ser");
        in = new ObjectInputStream(fileIn);
        NaiveBayes.categoriesTFIDF = (HashMap<String, HashMap<String, Double>>) in.readObject();
        in.close();
        fileIn.close();
        fileIn = new FileInputStream("totalNumberOfWords.ser");
        in = new ObjectInputStream(fileIn);
        NaiveBayes.totalNumberOfWords = (HashMap<String, Double>) in.readObject();
        in.close();
        fileIn.close();
        fileIn = new FileInputStream("neural-network.ser");
        in = new ObjectInputStream(fileIn);
        Neural_Network.neuralNetwork = (HashMap<String, HashMap<String, Double>>) in.readObject();
        in.close();
        fileIn.close();

    }

    public static void findClassDocuments() {
        for (int id : documents.keySet()) {
            Document current = documents.get(id);
            for (String category : current.getCategories()) {
                if (!categoriesToDocs.keySet().contains(category)) {
                    ArrayList<Document> temp = new ArrayList<>();
                    temp.add(current);
                    categoriesToDocs.put(category, temp);
                } else {
                    categoriesToDocs.get(category).add(current);
                }
            }
        }
        int x = 6;
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
        while ((str = read.readLine()) != null) {
            String[] line = str.split(" ");
            if (documents.get(Integer.parseInt(line[1])) != null) {
                documents.get(Integer.parseInt(line[1])).getCategories().add(line[0]);
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
