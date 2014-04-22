package com.github.dyamah.sjdat.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.dyamah.sjdat.Trie;
import com.github.dyamah.sjdat.TrieBuilder;

public class DoubleArrayTrieImpl implements Trie {
    /** トライルートのインデックス */
    private static final int ROOT = 1;

    /** 検索用コード列のデフォルトサイズ */
    private static final int DEFAULT_KEY_SIZE = 512;

    /**
     * 検索用コード列のスレッドローカル変数
     */
    private static ThreadLocal<CodeSequence> thl = new ThreadLocal<CodeSequence>() {
        @Override
        protected synchronized CodeSequence initialValue(){
            return new CodeSequence(DEFAULT_KEY_SIZE) ;
        }
    };

    /**
     * Trie構築器を生成する
     * @return Trie 構築器　
     */
    public static Builder createBuilder(){
        return new Builder();
    }

    /** ノード配列 */
    private long[] nodes_ ;

    /** TAIL配列 */
    private Tails tails_;

    private DoubleArrayTrieImpl(){
        nodes_ = null;
        tails_ = null ;
    }

    @Override
    public int lookup(String key){
        if (key == null || key.isEmpty())
            return UNKNOWN_ID;

        CodeSequence cs = thl.get();
        cs.set(key);

        int s = ROOT ;
        int pos = 0 ;
        Node src = Node.create();
        Node dist = Node.create();
        src.decode(nodes_[s]);
        while (pos < cs.size_){
            int base = src.base();
            int d = base + cs.sequence_[pos];
            if (d < nodes_.length){
                dist.decode(nodes_[d]);
                if (s == dist.check()){
                    pos ++ ;
                    src.decode(nodes_[d]) ;
                    s = d;
                    continue;
                }
            }
            int t = src.tail();
            Node.release(src);
            Node.release(dist);
            if (t >= 0 && tails_.match(t, pos, cs.size_, cs.sequence_))
                return s;
            return UNKNOWN_ID;
        }
        if (src.isTerminal()){
            Node.release(src);
            Node.release(dist);
            return s;
        }
        Node.release(src);
        Node.release(dist);
        return UNKNOWN_ID;
    }

    @Override
    public void save(File file) throws IOException {
        if (file == null)
            throw new IllegalArgumentException("The file is null.");

        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(file.getPath()));
            int size = nodes_.length;
            out.writeInt(size);
            for(int i = 0 ; i < size; i++)
                out.writeLong(nodes_[i]);
            size = tails_.tails_.length;
            out.writeInt(size);
            out.writeInt(tails_.begin_);
            for(int i = 0 ; i < size; i++)
                out.writeInt(tails_.tails_[i]);
        } finally {
            if (out != null)
                out.close();
        }

    }

    @Override
    public int numberOfKeys() {
        int m = 0;
        Node node = Node.create();
        for(int i = 2; i < nodes_.length; i++){
            node.decode(nodes_[i]);
            if (node.isTerminal())
                m++ ;
        }
        Node.release(node);
        return m + tails_.numberOfKeys();
    }


    @Override
    public int numberOfNodes() {
        return nodes_.length + tails_.tails_.length;
    }

    @Override
    public int numberOfFreeNodes() {
        int m = 0 ;
        for(int i = 2; i < nodes_.length; i++){
            Node node = Node.create(nodes_[i]);
            if (node.isFree())
                m++;
        }
        return m;
    }

    /**
     * 検索用コード列
     * @author Hiroyasu Yamada
     *
     */
    private static class CodeSequence {

        /** コード列 */
        private int[] sequence_ ;

        /** コード列のサイズ */
        private int size_ ;

        /**
         * 指定キャパシティーでコンストラクト
         * @param capacity コード列の初期キャパシティ
         */
        private CodeSequence(int capacity){
            assert(capacity >= 0);
            sequence_ = new int[capacity];
            size_ = 0;
        }

        /**
         * キーを変換しコード列を設定する
         * @param key キー
         */
        private void set(String key){
            assert(key != null);
            assert(!key.isEmpty());
            int size = key.length();
            if (size >= sequence_.length)
                sequence_ = new int[size];
            size_ = size ;
            for(int i = 0; i < size; i++){
                sequence_[i] = 0x0000FFFF & key.charAt(i);
            }
        }
    }

    /**
     * TAIL配列のクラス
     * @author Hiroyasu Yamada
     *
     */
    static private class Tails{
        private static final int DEFAULT_CAPACITY = 1024;
        /** TAIL保存領域 */
        private int[] tails_ ;

        /** 追加可能な位置の開始位置 */
        private int begin_ ;

        private Tails(){
            tails_ = new int[DEFAULT_CAPACITY];
            begin_ = 0;
        }
        private int numberOfKeys(){
            int m = 0;
            for(int i = 0 ; i < tails_.length; i++)
                if (tails_[i] < 0)
                    m++;
            return m ;
        }
        /**
         * コード列をTAIL配列に追加する
         * @param sequence 追加するコード列
         * @param begin 追加するコード列の開始位置
         * @param end 追加するコード列の終了位置
         * @return 追加したコード列のTAIL配列上での開始位置
         */
        private int add(int[] sequence, int begin,  int end){
            assert(begin < end);
            assert(sequence != null);
            int len = end - begin + 1;
            if (begin_ + len > tails_.length){
                int[] x = new int[begin_ + len];
                System.arraycopy(tails_, 0, x, 0, tails_.length);
                tails_ = x ;
            }
            int b = begin_ ;
            for(int i = begin ; i < end; i++)
                tails_[begin_++] = sequence[i];
            tails_[begin_++] = -1;
            return b ;
        }
        /**
         * TAIL部分がマッチするかどうか調べる
         * @param head 調べるTAILの先頭位置
         * @param begin コード列の開始位置
         * @param end コード列の終了位置
         * @param sequence コード列
         * @return マッチしたらコード列固有のID,マッチしなかったらUNKNOWN_ID(-1)を返す
         */
        boolean match(int head, int begin, int end, int[] sequence ){
            assert(head >= 0);
            assert(begin < end);
            assert(sequence != null);
            assert(begin >= 0);

            for(int i = begin; i < end; i++){
                if (sequence[i] != tails_[head])
                    return false;
                head++;
            }
            if (tails_[head] < 0)
                return true;
            return false;
        }
    }



    private static class Builder implements TrieBuilder {
        private long[] nodes_ ;
        private CodeSequence[] keys_ ;
        private int size_ ;
        private Tails tails_ ;

        private Builder(){
            nodes_ = null;
            keys_  = null;
            size_  = 0;
            tails_ = new Tails();
        }

        public Trie build(List<String> keys){
            if (keys == null)
                throw new IllegalArgumentException("The list of keys is null.");
            if (! checkKeys(keys))
                throw new IllegalArgumentException("The list of keys has not been sorted yet, or some duplications have been found.");

            int n = 0;
            for(String key : keys)
                if (! key.isEmpty())
                    n++;

            keys_ = new CodeSequence[n];
            int m = 2;
            int i = 0;
            for(String key : keys){
                if (key.isEmpty())
                    continue;
                CodeSequence cs = new CodeSequence(key.length());
                cs.set(key);
                keys_[i++] = cs;
                m += cs.size_;
            }
            nodes_ = new long[m];
            initNodes(0);
            if (keys_.length > 0)
                build(ROOT, 0, keys_.length, 0);
            DoubleArrayTrieImpl trie = new DoubleArrayTrieImpl();
            trie.nodes_ = nodes_ ;
            trie.tails_ = tails_ ;
            return trie;
        }

        private void initNodes(int begin){
            assert(nodes_.length >= 2);
            if (begin < 1){
                Node root = Node.create();
                root.base(1);
                root.check(0);
                nodes_[0] = root.encode();
                nodes_[1] = root.encode();
                size_ = 2 ;
                begin = 2;
                Node.release(root);
            }
            Node node = Node.create();
            for(int i = begin; i < nodes_.length; i++){
                Node prev = Node.create(nodes_[i-1]);
                int p = 0;
                if (prev.isFree())
                    p = i-1;
                node.updateFreeSpaceLink(p, i+1);
                nodes_[i] = node.encode();
                Node.release(prev);
            }
            Node.release(node);
        }

        private boolean checkKeys(List<String> keys){
            int m = keys.size() - 1;
            for(int i = 0 ; i < m ; i++){
                String a = keys.get(i);
                String b = keys.get(i+1);
                if (a.compareTo(b) >=0 )
                    return false;
            }
            return true;
        }


        private void build(int src, int begin, int end, int n){
            if (end - begin == 1){ // 分岐がないのでTAIL配列の更新処理を行う
                addTail(src, begin, n);
                return ;
            }

            List<Integer> children = new ArrayList<Integer>(); // 子ノードのインデックスを格納するリスト
            List<Integer> ranges   = new ArrayList<Integer>(); // 同じ子ノードを持つキーの範囲を保持するリスト　　

            int i = begin ;
            if (n >= keys_[begin].size_){
                i = begin + 1 ;
                Node node = Node.create(nodes_[src]);
                assert(! node.isFree());
                node.terminate();
                nodes_[src] = node.encode();
                Node.release(node);
            }
            ranges.add(i);
            while(i < end){
                int ch = keys_[i].sequence_[n];
                children.add(ch);
                int k = i+1;
                for(; k < end; k++){
                    if (keys_[k].sequence_[n] != ch)
                        break ;
                }
                ranges.add(k);
                i = k ;
            }
            assert(children.size() > 0);

            int base = updates(src, children);

            for(int k = 0; k < children.size(); k++){
                int ch = children.get(k);
                int dist = base + ch ;
                build(dist, ranges.get(k), ranges.get(k+1), n+1);
            }
        }

        private void addTail(int src, int begin, int n){
            Node node  = Node.create(nodes_[src]);
            assert(! node.isFree());
            if (n < keys_[begin].size_){
                int b = tails_.add(keys_[begin].sequence_, n, keys_[begin].size_);
                node.tail(b);
            } else {
                node.terminate();
            }
            nodes_[src] = node.encode();
            Node.release(node);
        }

        int updates(int src, List<Integer> children){
            int base = searchFreeSpace(1, children);
            Node node = Node.create(nodes_[src]);
            node.base(base);
            nodes_[src] = node.encode();
            for(int k = 0; k < children.size(); k++){
                int dist = base + children.get(k);
                node.decode(nodes_[dist]);
                assert(node.isFree());
                updateFreeSpaceLink(node.prev(), node.next());
                node.check(src);
                nodes_[dist] = node.encode();
            }
            Node.release(node);
            return base;
        }

        private void updateFreeSpaceLink(int prev, int next){
            assert(prev < next);
            assert(prev > 1);
            assert(next > 1);
            if (prev > 2){
                Node p = Node.create(nodes_[prev]);
                assert(p.isFree());
                p.updateFreeSpaceLink(p.prev(), next);
                nodes_[prev] = p.encode();
                Node.release(p);
            }
            if (next < nodes_.length){
                Node n = Node.create(nodes_[next]);
                assert(n.isFree());
                n.updateFreeSpaceLink(prev, n.next());
                nodes_[next] = n.encode();
                Node.release(n);
            }
        }

        private int searchFreeSpace(int base, List<Integer> children){
            Node node  = Node.create();
            Node x     = Node.create();
            int child = children.get(0);

            while(base + child < nodes_.length){
                node.decode(nodes_[base + child]);
                if (node.isFree())
                    break ;
                base ++;
            }
            boolean find = false;
            assert(node.isFree());
            while(! find ){
                find = true;
                for(int i = 0; i < children.size(); i++){
                    child = children.get(i);
                    resize(base + children.get(children.size()-1) + 1);
                    x.decode(nodes_[base + child]);
                    if (x.isFree())
                        continue;
                    find = false ;

                    base += node.next();
                    node.decode(nodes_[node.next()]);
                    assert(node.isFree());
                    break ;
                }
                if (find){
                    Node.release(node);
                    Node.release(x);
                    return base;
                }
            }
            Node.release(node);
            Node.release(x);
            return base;
        }

        void resize(int size){
            if (size < nodes_.length)
                return ;
            int b = nodes_.length;
            long[] nodes = new long[(int)(size*1.5)+1];
            System.arraycopy(nodes_, 0, nodes, 0, nodes_.length);
            nodes_ = nodes ;
            initNodes(b);
        }

        @Override
        public Trie load(File file) throws IOException {
            if (file == null)
                throw new IllegalArgumentException("The file is null.");

            ObjectInputStream in = null;
            DoubleArrayTrieImpl trie = null;
            try {
                in = new ObjectInputStream(new FileInputStream(file.getPath()));
                int size = in.readInt();
                long[] nodes = new long[size];
                for(int i = 0 ; i < size; i++)
                    nodes[i] = in.readLong();

                Tails tails = new Tails();
                size = in.readInt();
                tails.begin_ = in.readInt();
                tails.tails_ = new int[size];
                for(int i = 0 ; i < size; i++)
                    tails.tails_[i] = in.readInt();
                trie = new DoubleArrayTrieImpl();
                trie.nodes_ = nodes ;
                trie.tails_ = tails ;
            } finally {
                if (in != null)
                    in.close();
            }
            return trie ;
        }
    }
}
