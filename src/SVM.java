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

import javax.print.Doc;
import java.text.CollationElementIterator;
import java.util.*;

public class SVM {

    public static HashMap<Integer, Document> documents = Main.documents;
    public static HashMap<String, Integer> categories = new HashMap<>();
    public static HashMap<Integer, String> categories2 = new HashMap<>();
    public static HashMap<String, ArrayList<Document>> categoriesToDocs = Main.categoriesToDocs;
    public static TreeSet<String> words = new TreeSet<>(Main.vocabulary.keySet());

    public SVM() {
        // create a new SVM implementation in the C SVC style.
        long start = System.currentTimeMillis();
        System.out.println("start: "+start);
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
        builder.eps = 0.1f; // epsilon
        builder.Cset = cSet; // C values used
        builder.kernelSet = kernelSet; //Kernel used

        ImmutableSvmParameter params = builder.build();
        // / create training parameters ------------
        ArrayList<Document> fakeDocs =new ArrayList<>();
        HashMap<Document, String> fakeDocCat = new HashMap<>();
        // create problem -------------------
     /*   int index = 0;
        for (String category :
                categoriesToDocs.keySet()) {
            for (Document d: categoriesToDocs.get(category)) {
                fakeDocCat.put(d, category);
                fakeDocs.add(d);
            }


         //   if (index ==1)  break;
            categories.put(category, index);
            categories2.put(index, category);
            index++;
        }*/
        MutableBinaryClassificationProblemImpl problem = new MutableBinaryClassificationProblemImpl(String.class, documents.size());
        int num = 0;
        for (Integer curInt : documents.keySet()) {
            Document cur = documents.get(curInt);
            problem.addExample(getSparseVector(cur),cur.getCategories().get(0));
           // if (num == fakeDocs.size()) break;
            num++;
            System.out.println(num+" gecen sure until now since start "+( System.currentTimeMillis() - start));
        }
        // / create problem -------------------

        // train ------------------------
        BinaryModel model = svm.train(problem, params);
        // / train ------------------------
        int succ = 0;
        int total = 0;
        long end = System.currentTimeMillis();
        System.out.println("SVM: "+(end-start));
        Collections.shuffle(fakeDocs);
        for (Document doc : fakeDocs) {
            total++;
            SparseVector sparsedoc = getSparseVector(doc);
            // predict -------------------------
            //      SparseVector xTest = generateFeatures(new float[]{0.2f, 0.1f});
            String predictedLabel = (String) model.predictLabel(sparsedoc);
            System.out.println("original: " + doc.getCategories().toString() + " predicted: " + (predictedLabel));
            if (doc.getCategories().contains(predictedLabel)) {
                succ++;
            }
        }


        System.out.println(100 *(double) succ /(double) total);

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
            index++;
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
