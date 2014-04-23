package com.github.dyamah.sjdat.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.dyamah.sjdat.Trie;
import com.github.dyamah.sjdat.TrieBuilder;
import com.github.dyamah.sjdat.impl.DoubleArrayTrieImpl;

public class mktrie {

    static BenchMark bm = new BenchMark();

    @SuppressWarnings("resource")
    static List<String> readKeys(File file){
        BufferedReader reader = null;
        List<String> keys = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            keys = new ArrayList<String>();

            bm.start("[Read]");
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

        bm.start("[Build]");
        Trie trie = builder.build(keys);
        bm.stop();


        return trie;
    }
    static void checkTrie(Trie trie, List<String> keys){
        Map<Integer, String> map = new TreeMap<Integer,String>();

        bm.start("[Check]");
        for(String key: keys){
            int id = trie.lookup(key);

            if (map.containsKey(id) || id < 0){
                System.err.println("Duplication: " + id + ":" + key);
            }
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

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2){
            System.err.println("usage: com.github.dyamah.sjdat.tools.mktrie input output");
            System.exit(1);
        }
        bm.showHeader();
        List<String> keys = readKeys(new File(args[0]));
        Trie trie = buildTrie(keys);
        checkTrie(trie, keys);
        save(trie, new File(args[1]));
        bm.showTotal();
        System.err.println();
        System.err.println("Trie Info.");
        System.err.println("\t#keys      : " + trie.numberOfKeys());
        System.err.println("\t#nodes     : " + trie.numberOfNodes());
        System.err.println("\t#free nodes: " + trie.numberOfFreeNodes());
    }

}
