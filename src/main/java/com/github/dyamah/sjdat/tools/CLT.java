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

public class CLT {
    static class StopWatch {
        long start_ ;
        long stop_ ;
        StopWatch(){
           start_ = 0;
           stop_ = 0;
        }
        void start(){
            start_ = System.currentTimeMillis();
        }

        void stop(){
            stop_ =  System.currentTimeMillis() ;
        }

        void show(String arg){
            System.err.println(arg + ": "  + (stop_ - start_) + " [ms]");
        }
    }

    @SuppressWarnings("resource")
    static List<String> readKeys(File file){
        BufferedReader reader = null;
        List<String> keys = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            keys = new ArrayList<String>();
            StopWatch sw = new StopWatch();
            sw.start();
            String line = null;
            while((line = reader.readLine()) != null)
                keys.add(line);

            sw.stop();
            sw.show("Reading time");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return keys;
    }

    static Trie buildTrie(List<String> keys){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        StopWatch sw = new StopWatch();
        sw.start();
        Trie trie = builder.build(keys);
        sw.stop();
        sw.show("Build time");
        System.err.println("#keys: " + trie.numberOfKeys());
        System.err.println("#nodes: " + trie.numberOfNodes());
        System.err.println("#free: " + trie.numberOfFreeNodes());
        return trie;
    }
    static void checkTrie(Trie trie, List<String> keys){
        Map<Integer, String> map = new TreeMap<Integer,String>();

        boolean error = false;
        for(String key: keys){
            int id = trie.lookup(key);

            if (map.containsKey(id) || id < 0){
                error = true;
                System.err.println("Duplication: " + id + ":" + key);
            }
        }
        if (map.size() == keys.size())
            System.err.println("There is no error.");
    }

    static void save(Trie trie, File file){
        try {
            StopWatch sw = new StopWatch();
            sw.start();
            trie.save(file);
            sw.stop();
            sw.show("Saving time");
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
            System.err.println("usage: com.github.dyamah.sjdat.tools.CLT input output");
            System.exit(1);
        }
        List<String> keys = readKeys(new File(args[0]));
        Trie trie = buildTrie(keys);
        checkTrie(trie, keys);
        save(trie, new File(args[1]));

    }

}
