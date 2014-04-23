package com.github.dyamah.sjdat.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.dyamah.sjdat.Trie;
import com.github.dyamah.sjdat.TrieBuilder;


import junit.framework.TestCase;

public class DoubleArrayTrieImplTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateBuilder() {
        assertNotNull(DoubleArrayTrieImpl.createBuilder());
    }

    public void testLookup() {
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        keys.add("あり");
        keys.add("ありがとう");
        keys.add("ありがとうござ");
        Trie trie = builder.build(keys);

        assertEquals(3, trie.numberOfKeys());
        assertEquals(true, trie.numberOfNodes() > 2);
        assertEquals(true, trie.lookup(null) == Trie.UNKNOWN_ID);
        assertEquals(true, trie.lookup("") == Trie.UNKNOWN_ID);

        int id0 = trie.lookup("あり");
        int id1 = trie.lookup("ありがとう");
        int id2 = trie.lookup("ありがとうござ");

        assertEquals(true, id0 > 0);
        assertEquals(true, id1 > 0);
        assertEquals(true, id2 > 0);

        assertEquals(true, id0 != id1);
        assertEquals(true, id0 != id2);
        assertEquals(true, id1 != id2);

        assertEquals(true, trie.lookup("ありがとうご") == Trie.UNKNOWN_ID);

    }

    public void testSave() throws IOException {

        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        keys.add("あり");
        keys.add("ありがとう");
        keys.add("ありがとうござ");
        Trie trie = builder.build(keys);

        try {
            trie.save(null);
            fail("");
        } catch (IllegalArgumentException e ){
            assertEquals("The file is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            File file = new File("src");
            trie.save(file);
            fail("");
        } catch (IOException e ){
            e.printStackTrace();
        } catch (Exception e){
            fail("");
        }

        try {
            File file = File.createTempFile(this.getClass().getCanonicalName(), ".save.tmp");
            file.deleteOnExit();
            trie.save(file);
        } catch (Exception e){
            fail("");
        }
    }

    public void testBuilderLoad() throws IOException {
        File file = File.createTempFile(this.getClass().getCanonicalName(), ".load.tmp");
        file.deleteOnExit();
        {
            TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
            List<String> keys = new ArrayList<String>();
            keys.add("あり");
            keys.add("ありがとう");
            keys.add("ありがとうござ");
            keys.add("かりがとうござ");
            Trie trie = builder.build(keys);
            trie.save(file);
        }

        {
            TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
            Trie trie = builder.load(file);
            assertEquals(4, trie.numberOfKeys());
            assertEquals(true, trie.lookup("あり") > 0);
            assertEquals(true, trie.lookup("ありがとう") > 0);
            assertEquals(true, trie.lookup("ありがとうござ") > 0);
            assertEquals(true, trie.lookup("かりがとうござ") > 0);
        }

    }

    public void testBuild00(){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        try {
            builder.build(null);
            fail("");
        } catch (IllegalArgumentException  e){
            assertEquals("The list of keys is null.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            List<String> keys = new ArrayList<String>();
            keys.add("abac");
            keys.add("bab");
            keys.add("ab");
            keys.add("cc");
            builder.build(keys);
            fail("");
        } catch (IllegalArgumentException  e){
            assertEquals("The list of keys has not been sorted yet, some duplications have been found, or some keys are empty.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            List<String> keys = new ArrayList<String>();
            keys.add("abac");
            keys.add("bab");
            keys.add("bab");
            keys.add("cc");
            builder.build(keys);
            builder.build(null);
            fail("");
        } catch (IllegalArgumentException  e){
            assertEquals("The list of keys has not been sorted yet, some duplications have been found, or some keys are empty.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

        try {
            List<String> keys = new ArrayList<String>();
            keys.add("");
            keys.add("abc");
            keys.add("abcd");
            keys.add("cc");
            builder.build(keys);
            builder.build(null);
            fail("");
        } catch (IllegalArgumentException  e){
            assertEquals("The list of keys has not been sorted yet, some duplications have been found, or some keys are empty.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

    }

    public void testBuild01(){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        keys.add("abac");
        keys.add("bab");
        keys.add("cc");
        Trie trie = builder.build(keys);
        assertEquals( 3, trie.numberOfKeys());
        assertEquals(true, trie.numberOfNodes() >= 2);

        assertEquals( true,  trie.lookup("abac") > 0);
        assertEquals( true,  trie.lookup("bab") > 0);
        assertEquals( true,  trie.lookup("cc") > 0);
        assertEquals( true,  trie.lookup("ccc") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup(null) == Trie.UNKNOWN_ID);
    }

    public void testBuild02(){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        keys.add("abac");
        keys.add("abacc");
        keys.add("abace");
        keys.add("z");
        Trie trie = builder.build(keys);
        assertEquals( 4, trie.numberOfKeys());
        assertEquals(true, trie.numberOfNodes() >= 2);
        assertEquals( true,  trie.lookup("abac") > 0);
        assertEquals( true,  trie.lookup("bab") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("cc")  == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("ccc") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup(null) == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("abacc") > 0);
        assertEquals( true,  trie.lookup("abace") > 0);
        assertEquals( true,  trie.lookup("z") > 0);
        assertEquals( true,  trie.lookup("za") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("az") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("abaceb") == Trie.UNKNOWN_ID);
    }

    public void testBuild03(){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        keys.add("abac");
        keys.add("abacc");
        keys.add("abace");
        keys.add("abace ee");
        keys.add("z");
        Trie trie = builder.build(keys);
        assertEquals( 5, trie.numberOfKeys());
        assertEquals(true, trie.numberOfNodes() >= 2);
        assertEquals( true,  trie.lookup("abac") > 0);
        assertEquals( true,  trie.lookup("bab") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("cc")  == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("ccc") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup(null) == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("abacc") > 0);
        assertEquals( true,  trie.lookup("abace") > 0);
        assertEquals( true,  trie.lookup("z") > 0);
        assertEquals( true,  trie.lookup("za") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("az") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("abaceb") == Trie.UNKNOWN_ID);
    }

    public void testBuild04(){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        Trie trie = builder.build(keys);
        assertEquals( 0, trie.numberOfKeys());
        assertEquals( true, trie.numberOfNodes() >= 2);
        assertEquals( true,  trie.lookup("abac") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("bab") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("cc")  == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("ccc") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup(null) == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("abacc") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("abace") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("z") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("za") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("az") == Trie.UNKNOWN_ID);
        assertEquals( true,  trie.lookup("abaceb") == Trie.UNKNOWN_ID);
    }

    public void testBuild05(){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        keys.add("");

        try {
            builder.build(keys);
            fail("");
        } catch (IllegalArgumentException e ){
            assertEquals("The list of keys has not been sorted yet, some duplications have been found, or some keys are empty.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

    }

    public void testBuild06(){
        TrieBuilder builder = DoubleArrayTrieImpl.createBuilder();
        List<String> keys = new ArrayList<String>();
        keys.add("");
        keys.add("abc");
        keys.add("abde");

        try {
            builder.build(keys);
            fail("");
        } catch (IllegalArgumentException e ){
            assertEquals("The list of keys has not been sorted yet, some duplications have been found, or some keys are empty.", e.getMessage());
        } catch (Exception e){
            fail("");
        }

    }

}
