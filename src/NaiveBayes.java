import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Atakan Arıkan on 06.05.2016.
 */
public class NaiveBayes {
    public static HashMap<Integer, Document> documents = Main.documents;
    public static HashMap<String, Double> vocabulary = Main.vocabulary;
    public static HashMap<String, HashMap<String, Double>> categoriesTFIDF = new HashMap<>();
    public static HashMap<String, ArrayList<Document>> categoriesToDocs = Main.categoriesToDocs;
    public static HashMap<String, Double> totalNumberOfWords = new HashMap<String, Double>();

    public static String guess(Document testDocument) {
        String bestGuess = "";
        double max = Double.MAX_VALUE * -1;
        for (String category : categoriesToDocs.keySet()) { // just to get every category name, we wont be using the centroid values
            double result = 0;
            for (String word : testDocument.getTfidfVector().keySet()) {
                double occurence = testDocument.getTfidfVector().get(word);
                double data = 0;
                if (categoriesTFIDF.get(category).containsKey(word)) {
                    data = categoriesTFIDF.get(category).get(word);
                }
                double totalNumberOfWordsOfThisCategory = totalNumberOfWords.get(category);
                double insideValue = occurence * Math.log((data + 0.1) / (0.1 * vocabulary.size() + totalNumberOfWordsOfThisCategory));
                result += insideValue;
            }
            if (result > max) {
                max = result;
                bestGuess = category;
            }
        }
        return bestGuess;
    }

    public static void calculateTFIDFValuesOfAllCategories() {

        for (String category : categoriesToDocs.keySet()) {
            HashMap<String, Double> tfIdf = new HashMap<>();
            ArrayList<Document> docs = categoriesToDocs.get(category);
            double allWordsNumber = 0;
            for (String key : vocabulary.keySet()) {
                double total = 0;
                for (Document d : docs) {
                    if (d.getTfidfVector().containsKey(key)) {
                        total += d.getTfidfVector().get(key);
                    }
                }
                allWordsNumber += total;
                if (total != 0) {
                    tfIdf.put(key, total);
                }
            }
            totalNumberOfWords.put(category, allWordsNumber);
            categoriesTFIDF.put(category, tfIdf);


        }
    }


}
