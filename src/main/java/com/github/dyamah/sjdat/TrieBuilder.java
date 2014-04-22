package com.github.dyamah.sjdat;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TrieBuilder {

    /**
     * Trieを構築する
     * @param keys Trie に登録するキーのリスト。null、 未ソート、重複があった場合はIllegalArgumentException をスローする。
     * @return 構築したTrie
     */
    public Trie build(List<String> keys);


    /**
     * セーブしたファイルからTrieをロードする
     * @param file ロードするファイル。nullの場合はIllegalArgumentExceptionをスロー
     * @return ロードしたTrie
     * @throws IOException 何らかの原因で読み込みに失敗した場合
     */
    public Trie load(File file) throws IOException;
}
