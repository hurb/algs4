import java.util.ArrayList;


public class WordNet {
    
    private BinarySearchST<String, WordInfo> dict;
    private ArrayList<String> wordlist;
    private Digraph hypernymsGraph;
    private SAP sap;
    private int maxIndex;
    
    /* 
     * constructor takes the name of the two input files
     */
    public WordNet(String synsets, String hypernyms) {
        readSynsets(synsets);
        readHypernyms(hypernyms);
        
        sap = new SAP(hypernymsGraph);
    }
    
    private class WordInfo {
        private ArrayList<Integer> idx;
        
        public WordInfo() {
            idx = new ArrayList<Integer>();
        }

        public ArrayList<Integer> getIdxs() {
            return idx;
        }
    }
    
    /**
     * Create wordlist (synsets) from file
     * 
     * @param synsets_filename
     */
    private void readSynsets(String synsetsFilename) {
        In in = new In(synsetsFilename);
        String line = in.readLine();
        
        dict = new BinarySearchST<String, WordInfo>();
        wordlist = new ArrayList<String>();
        
        String[] items = {"0"};
        while (line != null) {
            items = line.split(",");
            
            if (items.length < 3) {
                continue;
            }
            
            //Integer.parseInt(items[0]);
            if (dict.contains(items[1])) {
                WordInfo wi = dict.get(items[1]);
                wi.idx.add(Integer.parseInt(items[0]));
            } else {
                WordInfo wi = new WordInfo();
                wi.idx.add(Integer.parseInt(items[0]));
                dict.put(items[1], wi);
            }
            wordlist.add(items[1]);
            
            line = in.readLine();
        }
        
        maxIndex = Integer.parseInt(items[0]) + 1;
    }
    
    private void readHypernyms(String hypernymsFilename) {
        In in = new In(hypernymsFilename);
        String line = in.readLine();
        hypernymsGraph = new Digraph(maxIndex);
        
        while (line != null) {
            String[] items = line.split(",");
            
            if (items.length < 2) {
                line = in.readLine();
                continue;
            }
            
            int v = Integer.parseInt(items[0]);
            for (int i = 1; i < items.length; i++) {
               int w = Integer.parseInt(items[i]);
               hypernymsGraph.addEdge(v, w);
            }

            line = in.readLine();
        }
    }
    


    /*
     * returns all WordNet nouns
     */
    public Iterable<String> nouns() {
        return dict.keys();
    }

    /*
     * is the word a WordNet noun?
     */
    public boolean isNoun(String word) {
        return dict.contains(word);
    }

    /*
     * distance between nounA and nounB (defined below)
     */
    public int distance(String nounA, String nounB) {
        Iterable<Integer> idxA = dict.get(nounA).getIdxs();
        Iterable<Integer> idxB = dict.get(nounB).getIdxs();
        
        return sap.length(idxA, idxB);
    }

    /*
     * a synset (second field of synsets.txt) that is 
     * the common ancestor of nounA and nounB
     * in a shortest ancestral path (defined below)
     */
    public String sap(String nounA, String nounB) {
        Iterable<Integer> idxA = dict.get(nounA).getIdxs();
        Iterable<Integer> idxB = dict.get(nounB).getIdxs();
        int idxAncestor = sap.ancestor(idxA, idxB);
        return wordlist.get(idxAncestor);
    }

    /*
     * for unit testing of this class
     */
    public static void main(String[] args) {
        
    }
}