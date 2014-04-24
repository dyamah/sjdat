package com.github.dyamah.sjdat.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
    private Tail tails_;

    /** コードマップ */
    private int[] codemap_;

    private DoubleArrayTrieImpl(){
        nodes_ = null;
        tails_ = null ;
        codemap_ = null;
    }

    @Override
    public int lookup(String key){
        if (key == null || key.isEmpty())
            return UNKNOWN_ID;

        CodeSequence cs = thl.get();
        cs.set(key, codemap_);

        int src = ROOT ;
        int pos = 0 ;

        while (pos < cs.size_){
            int dist = Node.BASE(nodes_[src]) + cs.sequence_[pos];
            if (dist < nodes_.length){
                if (src == Node.CHECK(nodes_[dist])){
                    pos ++ ;
                    src = dist;
                    continue;
                }
            }
            int t = Node.TAIL(nodes_[src]);
            if (t >= 0 && tails_.match(t, pos, cs.size_, cs.sequence_))
                return src;
            return UNKNOWN_ID;
        }
        if (Node.TERMINAL(nodes_[src]))
            return src;

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
            size = tails_.sequence_.length;
            out.writeInt(size);
            out.writeInt(tails_.begin_);
            for(int i = 0 ; i < size; i++)
                out.writeInt(tails_.sequence_[i]);
            out.writeInt(codemap_.length);
            for(int i = 0 ; i < codemap_.length; i++)
                out.writeInt(codemap_[i]);

        } finally {
            if (out != null)
                out.close();
        }

    }

    @Override
    public int numberOfKeys() {
        int m = 0;
        for(int i = 2; i < nodes_.length; i++){
            if (Node.TERMINAL(nodes_[i]))
                m++ ;
        }
        return m + tails_.numberOfKeys();
    }


    @Override
    public int numberOfNodes() {
        return nodes_.length + tails_.sequence_.length;
    }

    @Override
    public int numberOfFreeNodes() {
        int m = 0 ;
        for(int i = 2; i < nodes_.length; i++){
            if (Node.FREE(nodes_[i]))
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
        private void set(String key, int[] codemap){
            assert(key != null);
            assert(!key.isEmpty());
            int size = key.length();
            if (size >= sequence_.length)
                sequence_ = new int[size];
            size_ = size ;
            for(int i = 0; i < key.length(); i++){
                int k = 0x0000FFFF & key.charAt(i);
                int c = 0;
                if (k < codemap.length)
                    c = codemap[k];
                sequence_[i] = c;
            }
        }
    }

    /**
     * TAIL配列のクラス
     * @author Hiroyasu Yamada
     *
     */
    static private class Tail{
        private static final int DEFAULT_CAPACITY = 1024;
        /** TAIL保存領域 */
        private int[] sequence_ ;

        /** 追加可能な位置の開始位置 */
        private int begin_ ;

        private Tail(int size){
            if (size < DEFAULT_CAPACITY)
                size = DEFAULT_CAPACITY;
            sequence_ = new int[size];
            begin_ = 0;
        }
        private int numberOfKeys(){
            int m = 0;
            for(int i = 0 ; i < sequence_.length; i++)
                if (sequence_[i] < 0)
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
            assert(begin_ + len <= sequence_.length);
            int b = begin_ ;
            for(int i = begin ; i < end; i++)
                sequence_[begin_++] = sequence[i];
            sequence_[begin_++] = -1;
            return b ;
        }

        /**
         * TAIL部分がマッチするかどうか調べる
         * @param head 調べるTAILの先頭位置
         * @param begin コード列の開始位置
         * @param end コード列の終了位置
         * @param sequence コード列
         * @return マッチしたらtrue、それ以外はfalse
         */
        boolean match(int head, int begin, int end, int[] sequence ){
            assert(head >= 0);
            assert(begin < end);
            assert(sequence != null);
            assert(begin >= 0);

            for(int i = begin; i < end; i++){
                if (sequence[i] != sequence_[head])
                    return false;
                head++;
            }
            if (sequence_[head] < 0)
                return true;
            return false;
        }
    }

    /**
     * 静的ダブル配列の構築クラス
     * @author Hiroyasu Yamada
     *
     */
    private static class Builder implements TrieBuilder {
        /** ノード */
        private long[] nodes_ ;

        /** コード列の配列 */
        private CodeSequence[] keys_ ;

        /** キーからコード列へのマップ */
        private int[] codemap_ ;

        /** TAIL配列 **/
        private Tail tail_ ;

        /** TAIL配列のサイズ **/
        private int tail_size_ ;

        /** 最初の空きノードの位置 **/
        private int free_node_ ;

        private Builder(){
            nodes_ = null;
            keys_  = null;
            tail_size_ = 0;
            tail_ = null;
        }

        @Override
        public Trie build(List<String> keys){
            if (keys == null)
                throw new IllegalArgumentException("The list of keys is null.");
            if (checkInvalidKeys(keys))
                throw new IllegalArgumentException("The list of keys has not been sorted yet, some duplications have been found, or some keys are empty.");

            createCodeMap(keys); //  キーを捜査して必要なノード数、コードマップを作成する

            int i = 0;
            keys_ = new CodeSequence[keys.size()];
            for(String key : keys){
                CodeSequence cs = new CodeSequence(key.length());
                cs.set(key, codemap_);
                keys_[i++] = cs;
            }

            initNodes(0);
            if (keys_.length > 0)
                build(ROOT, 0, keys_.length, 0);
            DoubleArrayTrieImpl trie = new DoubleArrayTrieImpl();
            trie.nodes_ = nodes_ ;
            trie.tails_ = tail_ ;
            trie.codemap_ = codemap_ ;
            return trie;
        }

        /**
         * ノードの列を初期化する
         * @param begin 初期化するノードの開始位置
         */
        private void initNodes(int begin){
            assert(nodes_.length >= 2);
            if (begin < 1){
                Node root = new Node();
                root.base(1);
                root.check(0);
                nodes_[0] = root.encode();
                nodes_[1] = root.encode();
                begin = 2;
                free_node_ = 2;
            }
            Node node = new Node();
            for(int i = begin; i < nodes_.length; i++){
                int p = 0;
                if (Node.FREE(nodes_[i-1]))
                    p = i-1;
                node.updateFreeSpaceLink(p, i+1);
                nodes_[i] = node.encode();
            }
        }

        /**
         * ダブル配列に格納するキーリストに不正なキーがないかどうか調べる。
         * 不正：未ソート、重複したキーがある、または空のキーがある場合
         * @param keys 調べるキーのリスト
         * @return 不正なキーがあればtrue、なければfalse
         */
        private boolean checkInvalidKeys(List<String> keys){
            int m = keys.size() ;
            for(int i = 0 ; i < m ; i++){
                String a = keys.get(i);
                if (a == null || a.isEmpty())
                    return true;
                if (i == m - 1)
                    continue;
                String b = keys.get(i+1);
                if (a.compareTo(b) >=0 )
                    return true;
            }
            return false;
        }

        /**
         * ダブル配列を構築する
         * @param src　親ノードのインデックス
         * @param begin コード列の開始位置
         * @param end コード列の終了位置
         * @param n コード列のインデックス
         */
        private void build(int src, int begin, int end, int n){
            if (end - begin == 1){ // 分岐がないのでTAIL配列の更新処理を行う
                addTail(src, begin, n);
                return ;
            }

            List<Integer> children = new ArrayList<Integer>(); // 子ノードのインデックスを格納するリスト
            List<Integer> ranges   = new ArrayList<Integer>(); // 同じ子ノードを持つキーの範囲を保持するリスト　　

            //同一のラベルで遷移するキー列の範囲を調べる

            int i = begin ;
            if (n >= keys_[begin].size_){
                Node node = new Node();
                i = begin + 1 ;
                node.decode(nodes_[src]);
                assert(! node.isFree());
                node.terminate();
                nodes_[src] = node.encode();
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

            int base = updates(src, children); // 子ノードを格納する空きスペースを探し、そのときのベース値を求める
            assert(base > 0);

            // 子ノード以下を深さ有線で再帰的に登録していく
            for(int k = 0; k < children.size(); k++){
                int ch = children.get(k);
                int dist = base + ch ;
                build(dist, ranges.get(k), ranges.get(k+1), n+1);
            }
        }

        /**
         * TAIL配列に要素を追加する。
         * @param src　親ノードのインデックス
         * @param begin　TAILに追加するコード列のインデックス
         * @param n　コード列中、TAILに追加する開始位置
         */
        private void addTail(int src, int begin, int n){
            Node node = new Node(nodes_[src]);
            assert(! node.isFree());
            if (n < keys_[begin].size_){
                int b = tail_.add(keys_[begin].sequence_, n, keys_[begin].size_);
                node.tail(b);
            } else {
                //追加するものがなければ終端処理
                node.terminate();
            }
            nodes_[src] = node.encode();
        }

        private int updates(int src, List<Integer> children){
            int base = searchFreeSpace(children);
            Node node = new Node(nodes_[src]);
            node.base(base);
            nodes_[src] = node.encode();
            for(int k = 0; k < children.size(); k++){
                int dist = base + children.get(k);
                node.decode(nodes_[dist]);
                assert(node.isFree());
                if (dist == free_node_)
                    free_node_ = node.next();

                updateFreeSpaceLink(node.prev(), node.next());
                node.check(src);
                nodes_[dist] = node.encode();
            }
            return base;
        }

        /**
         * 空きノードのlinked list の状態を更新する
         * @param prev　前の空きノードのインデックス
         * @param next 次の空きノードのインデックス
         */
        private void updateFreeSpaceLink(int prev, int next){
            assert(prev < next);
            if (prev > 1){
                Node p = new Node(nodes_[prev]);
                assert(p.isFree());
                p.updateFreeSpaceLink(p.prev(), next);
                nodes_[prev] = p.encode();
            }
            if (next < nodes_.length){
                Node n = new Node(nodes_[next]);
                assert(n.isFree());
                n.updateFreeSpaceLink(prev, n.next());
                nodes_[next] = n.encode();
            }
        }

        /**
         * 子ノードすべてが格納できる空きノードとそのときのBASE値を求める
         * @param children 子ノードのリスト
         * @return　子ノードすべてを格納可能なBASE値
         */
        private int searchFreeSpace(List<Integer> children){

            int start = free_node_ ;

            int num_children = children.size();
            int fc = children.get(0);
            int lc = children.get(num_children-1);

            while (start - fc < 1)
                start = Node.NEXT(nodes_[start]);

            boolean find = false;
            int base = start - fc;
            while(! find ){
                find = true;
                base = start - fc;
                resize(base + lc + 1); // ノードが足りなくなったら拡張する
                for(int i = 1; i < num_children; i++){
                    if (Node.FREE(nodes_[base + children.get(i)]))
                        continue;
                    find = false ;
                    start = Node.NEXT(nodes_[start]);
                    break ;
                }
            }
            return base;
        }

        /**
         * キーリストを深さ優先で探索し、コードマップ、必要ノード数の概算、必要TAIL配列のサイズを求める
         * @param keys キーリスト　
         */
        private void createCodeMap(List<String> keys){
            Map<Character, Integer> map = new TreeMap<Character, Integer>();

            int num_nodes = traverseKeys(keys, 0, keys.size(), 0, map);

            int max = -1;
            for(Entry<Character, Integer> e : map.entrySet()){
                int c = 0x0000FFFF  & e.getKey();
                if (c > max)
                    max = c ;
            }
            int[] codemap = new int[max+1];


            for(Entry<Character, Integer> e : map.entrySet()){
                int c = 0x0000FFFF  & e.getKey();
                codemap[c] = e.getValue();
                // codemap[c] = i++;
            }
            codemap_ = codemap ;
            nodes_ = new long[num_nodes + 2];
            tail_ = new Tail(tail_size_);
        }

        private int traverseKeys(List<String> keys, int begin, int end, int n, Map<Character, Integer> codemap){
            if (keys.size() == 0)
                return 0 ;
            if (end - begin == 1){
                int m = keys.get(begin).length() - n;
                if (m > 0){
                    tail_size_ +=  m+1;
                    String key = keys.get(begin);
                    for(int i = n; i < key.length(); i++){
                        char ch = key.charAt(i);
                        if (! codemap.containsKey(ch)){
                            codemap.put(ch, codemap.size()+1);
                        }
                    }

                }
                return m;
            }
            List<Integer> ranges   = new ArrayList<Integer>();

            int i = begin ;
            if (n >= keys.get(begin).length())
                i = begin + 1 ;

            ranges.add(i);

            while(i < end){
                String key = keys.get(i);
                char ch = key.charAt(n);
                int k = i+1;
                for(; k < end; k++){
                    if (keys.get(k).charAt(n) != ch)
                        break ;
                }
                ranges.add(k);
                i = k ;
            }
            int num  = ranges.size() - 1;
            for(int k = 0; k < ranges.size()-1; k++){
                char ch = keys.get(ranges.get(k)).charAt(n);
                if (! codemap.containsKey(ch)){
                    codemap.put(ch, codemap.size()+1);
                }
                num +=  traverseKeys(keys, ranges.get(k), ranges.get(k+1), n+1, codemap);
            }

            return num ;
        }

        /**
         * 必要に応じてダブル配列の格納場所を拡張する
         * @param size 必要なサイズ
         */
        private void resize(int size){
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

                size = in.readInt();
                Tail tails = new Tail(size);
                tails.begin_ = in.readInt();
                tails.sequence_ = new int[size];
                for(int i = 0 ; i < size; i++)
                    tails.sequence_[i] = in.readInt();
                size = in.readInt();
                int[] codemap = new int[size];
                for(int i = 0 ; i < size; i++)
                    codemap[i] = in.readInt();

                trie = new DoubleArrayTrieImpl();
                trie.nodes_ = nodes ;
                trie.tails_ = tails ;
                trie.codemap_ = codemap ;

            } finally {
                if (in != null)
                    in.close();
            }
            return trie ;
        }
    }
}
