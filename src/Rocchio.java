import java.util.HashMap;

/**
 * Created by Atakan Arıkan on 23.04.2016.
 */
public class Rocchio {

    public static HashMap<Integer, Document> documents = Main.documents;
    public static HashMap<String, Double> vocabulary = Main.vocabulary;


    public static double cosineSimilarity(HashMap<String, Double> list1, HashMap<String, Double> list2) {
        double dividend = 0;
        double lengthList1 = 0;
        double lengthList2 = 0;
// since their sizes are equal we dont have to check twice
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

}
