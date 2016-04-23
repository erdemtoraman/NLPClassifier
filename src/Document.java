import java.util.HashMap;

/**
 * Created by Atakan Arıkan on 23.04.2016.
 */
public class Document {
    int id;
    HashMap<String, Double> tfidfVector;
    String category;

    public Document(int id, HashMap<String, Double> tfidfVector) {
        this.id = id;
        this.tfidfVector = tfidfVector;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<String, Double> getTfidfVector() {
        return tfidfVector;
    }

    public void setTfidfVector(HashMap<String, Double> tfidfVector) {
        this.tfidfVector = tfidfVector;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
