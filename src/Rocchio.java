import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Atakan Arıkan on 23.04.2016.
 */
public class Rocchio {

    public static HashMap<Integer, Document> documents = Main.documents;
    public static HashMap<String, Double> vocabulary = Main.vocabulary;
    public static HashMap<String, ArrayList<Document>> categoriesToDocs = Main.categoriesToDocs;
    public static HashMap<String, HashMap<String, Double>> categoriesCentroids = new HashMap<>();

    public static void calculateAllCentroids() {
        for (String category : categoriesToDocs.keySet()) {
            categoriesCentroids.put(category, calculateCentroid(category));
        }
    }

    public static HashMap<String, Double> calculateCentroid(String category) {
        HashMap<String, Double> centroid = new HashMap<>();
        ArrayList<Document> docs = categoriesToDocs.get(category);
        for (String key : vocabulary.keySet()) {
            double total = 0;
            for (Document d : docs) {
                if (d.getTfidfVector().containsKey(key)) {
                    total += d.getTfidfVector().get(key);
                }
            }
            if (total != 0) {
                centroid.put(key, total / (double) docs.size());
            }
        }
        return centroid;
    }

    public static double cosineSimilarity(HashMap<String, Double> list1, HashMap<String, Double> list2) {
        double dividend = 0;
        double lengthList1 = 0;
        double lengthList2 = 0;
        for (String word : vocabulary.keySet()) {
            if (list1.containsKey(word) && list2.containsKey(word)) {
                double elem1 = list1.get(word);
                double elem2 = list2.get(word);
                dividend += elem1 * elem2;
                lengthList1 += elem2 * elem2;
                lengthList2 += elem1 * elem1;
            }
        }

        return dividend / (Math.sqrt(lengthList1) * Math.sqrt(lengthList2));
    }

    public static void doSomething() {
       double success = 0;
        int i = 0;
        for (Integer id : documents.keySet()) {
            i++;
            Document testDoc = documents.get(id);
            double min = Double.MAX_VALUE;
            String guess = "";
            for (String categoryName : categoriesCentroids.keySet()) {
                Double similarity = cosineSimilarity(testDoc.getTfidfVector(), categoriesCentroids.get(categoryName));
                if (similarity < min) {
                    min = similarity;
                    guess = categoryName;
                }

            }
            if (testDoc.getCategories().contains(guess)){
                success++;
                System.out.println("s"+i);
            }
            if (i == 100){
                break;
            }
        }
        System.out.println(success+" / "+ documents.keySet().size() +" = "+ 100*success/documents.keySet().size());
    }
}
