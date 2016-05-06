import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Atakan Arıkan on 06.05.2016.
 */
public class Knn {
    public static final int K = 5;
    public static HashMap<Integer, Document> documents = Main.documents;

    public static String test(Document d) {
        TreeMap<Double, ArrayList<String>> result = new TreeMap<>();
        HashMap<String, Integer> finalResult = new HashMap<>();
        for (int id: documents.keySet()) {
            Document current = documents.get(id);
            double res = Rocchio.cosineSimilarity(current.getTfidfVector(), d.getTfidfVector());
            result.put(-1 *res, current.getCategories());
        }
        int x = 0;
        for(Map.Entry m:result.entrySet()){
            ArrayList<String> categoriez = (ArrayList<String>) m.getValue();
            if (x <= K && x != 0) {
                for (String cat : categoriez) {
                    if (finalResult.containsKey(cat)) {
                        int newValue = finalResult.get(cat) + 1;
                        finalResult.put(cat, newValue);
                    } else {
                        finalResult.put(cat, 1);
                    }
                }
            }
            x++;
            if (x > K) {
                break;
            }
        }
        int max = 0;
        String winner = "";
        for (String cat : finalResult.keySet()) {
            if (finalResult.get(cat) > max) {
                winner = cat;
                max = finalResult.get(cat);
            }
        }
        return winner;
    }
}
