class Documento {
    String[] key;
    float tf, idf;
    double tfidf;
    int[] freq;
    String name;
    int value, totalPalabras;

    // nueva clase
    Palabra palabra;

    Documento(String name, String[] key, int[] freq, int totalPalabras) {
        // arrays
        this.key = key;
        this.freq = freq;

        // primitiva variables
        this.totalPalabras = totalPalabras;
        this.name = name;
    }

    void wordCount(float x, float y) {
        int largoPalabra = 0;
        String todas = "";

        for (int i = 0; i < key.length; i++) {
            if (freq[i] > 5) {
                todas += key[i] + ": " + freq[i] + " ";
                // largoPalabra += key[i].length();
                // largoPalabra += freq[i];
                fill(0);
                // text(key[i] + ": " + freq[i], x+largoPalabra, y);
                text(todas, x, y);
            }
        }
    }

    // boolean ranking(int limit){
    //     if()
    // }

}
