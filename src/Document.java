import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Atakan Arıkan on 23.04.2016.
 */
public class Document implements Serializable {
    int id;
    HashMap<String, Double> tfidfVector;
    ArrayList<String> categories;

    public Document(int id, HashMap<String, Double> tfidfVector) {
        this.id = id;
        this.tfidfVector = tfidfVector;
        this.categories = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", tfidfVector = " + tfidfVector +
                ", categories='" + categories.toString() + '\'' +
                '}';
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

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
}
