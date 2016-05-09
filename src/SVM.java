/**
 * Created by Atakan Arıkan on 09.05.2016.
 */

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
import edu.berkeley.compbio.jlibsvm.binary.BinaryModel;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.binary.MutableBinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.kernel.LinearKernel;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class SVM {

    public static HashMap<Integer, Document> documents = Main.documents;
    public static HashMap<String, Integer> categories = new HashMap<>();
    public static HashMap<String, ArrayList<Document>> categoriesToDocs = Main.categoriesToDocs;
    public static TreeSet<String> words = new TreeSet<>(Main.vocabulary.keySet());
    public SVM() {
        // create a new SVM implementation in the C SVC style.
        C_SVC svm = new C_SVC();
        // build parameters
        ImmutableSvmParameterGrid.Builder builder = ImmutableSvmParameterGrid.builder();

        // create training parameters ------------
        HashSet<Float> cSet;
        HashSet<LinearKernel> kernelSet;

        cSet = new HashSet<>();
        cSet.add(1.0f);

        kernelSet = new HashSet<LinearKernel>();
        kernelSet.add(new LinearKernel());

        // configure finetuning parameters
        builder.eps = 0.001f; // epsilon
        builder.Cset = cSet; // C values used
        builder.kernelSet = kernelSet; //Kernel used

        ImmutableSvmParameter params = builder.build();
        // / create training parameters ------------

        // create problem -------------------
        int index = 0;
        for (String category :
                categoriesToDocs.keySet()) {
            categories.put(category, index);
            index++;
        }
        MutableBinaryClassificationProblemImpl problem = new MutableBinaryClassificationProblemImpl(String.class, documents.keySet().size());
        for (int i : documents.keySet()) {
            Document cur = documents.get(i);
            problem.addExample(getSparseVector(cur), categories.get(cur.getCategories().get(0)));
        }
        // / create problem -------------------

        // train ------------------------
        BinaryModel model = svm.train(problem, params);
        // / train ------------------------
        SparseVector doc = getSparseVector(documents.get(3000));
        // predict -------------------------
  //      SparseVector xTest = generateFeatures(new float[]{0.2f, 0.1f});
        int predictedLabel = (Integer) model.predictLabel(doc);
        System.out.println("predicted:" + predictedLabel);
        System.out.println();
        // / predict -------------------------


    }
    public SparseVector getSparseVector(Document document) {
        SparseVector sparseVector = new SparseVector(words.size());
        float[] floats = new float[words.size()];
        int[] indexes = new int[words.size()];
        int index = 0;
        for (String w : words) {
            indexes[index] = index;
            double res = 0;
            if (document.getTfidfVector().containsKey(w)) {
                res = document.getTfidfVector().get(w);
            }
            floats[index] = (float) res;
        }
        sparseVector.indexes = indexes;
        sparseVector.values = floats;
        return sparseVector;

    }
    private SparseVector generateFeatures(float[] floats) {
        SparseVector sparseVector = new SparseVector(floats.length);
        int[] indices = new int[2];
        for (int i = 0; i < floats.length; i++) {
            indices[i] = new Integer(i);
        }
        sparseVector.indexes = indices;
        sparseVector.values = floats;
        return sparseVector;
    }
}
