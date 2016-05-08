import javax.print.Doc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Erdem on 5/7/2016.
 */
public class Neural_Network {
    public static HashMap<Integer, Document> documents = Main.documents;
    public static HashMap<String, Double> vocabulary = Main.vocabulary;
    public static HashMap<String, ArrayList<Document>> categoriesToDocs = Main.categoriesToDocs;
    public static HashMap<String, HashMap<String, Double>> neuralNetwork = new HashMap<>(); // from word to category weight


    public static String guess(Document testDoc) {
        double max = Double.MAX_VALUE * -1;
        String guessedCategory = "";
        for (String category : categoriesToDocs.keySet()) {
            double totalValue = 0;
            for (String word : testDoc.getTfidfVector().keySet()) {
                Double tfIdf = testDoc.getTfidfVector().get(word);
                totalValue += tfIdf * neuralNetwork.get(word).get(category);
            }
            if (totalValue > max) {
                max = totalValue;
                guessedCategory = category;
            }
        }

        return guessedCategory;
    }

    public static void train(int times) {
        initialize();
        ArrayList keys = new ArrayList(documents.keySet());
        for (int i = 0; i < times; i++) {
            Collections.shuffle(keys);
            improve(keys);
            System.out.println(i +" times completed");
        }
    }

    private static void improve(ArrayList<Integer> keys) {
        for (Integer id : keys) {
            Document doc = documents.get(id);
            double max = Double.MAX_VALUE * -1;
            String guessedCategory = "";
            for (String category : categoriesToDocs.keySet()) {
                double totalValue = 0;
                for (String word : doc.getTfidfVector().keySet()) {
                    Double tfIdf = doc.getTfidfVector().get(word);
                    totalValue += tfIdf * neuralNetwork.get(word).get(category);
                }
                if (totalValue > max) {
                    max = totalValue;
                    guessedCategory = category;
                }
            }
            if (doc.getCategories().contains(guessedCategory)) {
                for (String word : doc.getTfidfVector().keySet()) {
                    Double coefficient = neuralNetwork.get(word).get(guessedCategory);
                    neuralNetwork.get(word).put(guessedCategory, coefficient * 1.0001);
                }
            } else {
                for (String word : doc.getTfidfVector().keySet()) {
                    Double coefficient = neuralNetwork.get(word).get(guessedCategory);
                    neuralNetwork.get(word).put(guessedCategory, coefficient * 0.999);
                }

            }
        }
    }

    public static void initialize() {

        for (String word : vocabulary.keySet()) {
            HashMap<String, Double> coefficients = new HashMap<>();
            for (String category : categoriesToDocs.keySet()) {
                coefficients.put(category, 1.0);
            }
            neuralNetwork.put(word, coefficients);
        }


    }


}
