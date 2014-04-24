package com.github.dyamah.sjdat;

import java.io.File;
import java.io.IOException;

public interface Trie {

    public static int UNKNOWN_ID = -1;


    /**
     * キーが登録されているかどうか検索する
     * @param key 検索するキー
     * @return キーが登録されていれば 1以上の整数。未定義やnullを指定した場合は UNKNOWN_ID
     */
    public int lookup(String key);

    /**
     * 共通部分接頭辞を探索する
     * @param key 探索するキー
     * @param out 探索結果の配列。この配列がキーの文字インデックスに対応しており、out[i]が非負の時、先頭からi文字目までの共通接頭辞がtrieに登録されていたことを意味する。
     * out の長さがkey.length()より短い場合やnullの場合は IllegalArgumentExceptionをスロー
     */
    public void commonPrefixSearch(String key, int[] out);

    /**
     * Trieをファイルにセーブする
     * @param file セーブするファイル。nullの場合はIllegalArgumentExceptionをスロー
     * @throws IOException なんらかの原因で書き込みに失敗した場合
     */
    public void save(File file) throws IOException;

    /**
     * Trieに格納しているキーの数を返す
     * @return 格納しているキーの数
     */
    public int numberOfKeys();

    /**
     * Trieに使用ノード数キーの数を返す
     * @return ノード数
     */
    public int numberOfNodes();


    /**
     * 空きノードの数を返す
     * @return 空きノードの数
     */
    public int numberOfFreeNodes();



}
