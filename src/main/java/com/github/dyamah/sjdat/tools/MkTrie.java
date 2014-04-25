package com.github.dyamah.sjdat.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.dyamah.sjdat.Trie;
import com.github.dyamah.sjdat.TrieBuilder;
import com.github.dyamah.sjdat.impl.DoubleArrayTrieImpl;

public class MkTrie {

    static BenchMark bm = new BenchMark();

    @SuppressWarnings("resource")
    static List<String> readKeys(File file){
        BufferedReader reader = null;
        List<String> keys = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            keys = new ArrayList<String>();

            bm.start("[Reading keys]");
            String line = null;
            while((line = reader.readLine()) != null)
                keys.add(line);

            bm.stop();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return keys;
    }

    static Trie buildTrie(List<String> keys){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();

        bm.start("[Building Trie]");
        Trie trie = builder.build(keys);
        bm.stop();


        return trie;
    }

    static void checkTrie(Trie trie, List<String> keys){
        Map<Integer, String> map = new TreeMap<Integer,String>();

        bm.start("[Check Trie]");
        for(String key: keys){
            int id = trie.lookup(key);
            if (id < 0)
                throw new IllegalStateException("Invalid: " + id + ":" + key);
            if (map.containsKey(id))
                throw new IllegalStateException("Duplication: " + id + ":" + key);

            map.put(id, key);
        }

        bm.stop();
    }

    static void save(Trie trie, File file){
        try {
            bm.start("[Save]");
            trie.save(file);
            bm.stop();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static Trie load(File file){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        Trie trie = null;
        try {
            bm.start("[Load Trie]");
            trie = builder.load(file);
            bm.stop();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return trie;
    }

    static void lookup(Trie trie, List<String> keys, boolean show){
        bm.start("[Lookup]");
        for(String key : keys){
            int id = trie.lookup(key);
            if (! show)
                continue;
            System.out.print(key);
            System.out.print("\t");
            System.out.println(id);
        }
        bm.stop();
    }

    static void validation(Trie trie, List<String> train, List<String> test){
        bm.start("[Validation]");
        Map<String,Integer> train_map = new HashMap<String, Integer>();

        for(String key : train){
            int id = trie.lookup(key);
            if (id == Trie.UNKNOWN_ID)
                throw new IllegalStateException("Invalid key-id pair in training keys: " + key + ", " + id);
            train_map.put(key, id);
        }

        boolean error = false;
        for(String key : test){
            int id = trie.lookup(key);
            if ((train_map.containsKey(key) && id == Trie.UNKNOWN_ID) ||
                    (! train_map.containsKey(key) && id > 0)){
                System.out.println("Invalid key-id pair in test keys.: " + key + ", " + id);
                error = true;
            }
        }
        if (error)
            throw new IllegalStateException("Some invalid keys has been found.");

        bm.stop();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 3 || (! args[0].startsWith("-b") && ! args[0].startsWith("-s") && !args[0].startsWith("-c")) ){
            System.err.println("usage: com.github.dyamah.sjdat.tools.MkTrie -[b|s|c] trie keys");
            System.exit(1);
        }

        if (args[0].startsWith("-b")){
            System.err.println("<< Build Mode >>");
            bm.showHeader();
            List<String> keys = readKeys(new File(args[2]));
            Trie trie = buildTrie(keys);
            checkTrie(trie, keys);
            save(trie, new File(args[1]));
            bm.showTotal();
            System.err.println();
            System.err.println("<< Trie Info. >>");
            System.err.println("\t#keys      : " + trie.numberOfKeys());
            System.err.println("\t#nodes     : " + trie.numberOfNodes());
            System.err.println("\t#free nodes: " + trie.numberOfFreeNodes());
        }

        if (args[0].startsWith("-s")){
            System.err.println("<< Search Mode >>");
            bm.showHeader();
            Trie trie =load(new File(args[1]));
            List<String> keys = readKeys(new File(args[2]));
            lookup(trie, keys, false);
            bm.showTotal();
        }

        if (args[0].startsWith("-c")){
            if (args.length < 4){
                System.err.println("usage: com.github.dyamah.sjdat.tools.MkTrie -c trie train test");
                System.exit(1);
            }
            System.err.println("<< Check Mode >>");
            bm.showHeader();
            List<String> train = readKeys(new File(args[2]));
            Trie trie = buildTrie(train);
            checkTrie(trie, train);
            save(trie, new File(args[1]));
            trie =load(new File(args[1]));
            List<String> test = readKeys(new File(args[3]));
            validation(trie, train, test);
            bm.showTotal();
        }

    }

}
