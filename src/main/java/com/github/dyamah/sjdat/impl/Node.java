package com.github.dyamah.sjdat.impl;

final class Node {

    static private long FREE_MASK        = 0x4000000000000000L;
    static private long TERMINATION_MASK = 0x2000000000000000L;
    static private long TAIL_MASK        = 0x1000000000000000L;
    static private long BASE_MASK        = 0x0FFFFFFFC0000000L;
    static private long CHECK_MASK       = 0x000000003FFFFFFFL;

    /**
     * ノードが空きノードかどうか調べる
     * @param node ノードのエンコード値
     * @return 空きノードであればtrue, そうでなければfalse
     */
    static boolean FREE(long node) {
        return (node & FREE_MASK) != 0L ;
    }

    /**
     * ノードのベース値を返す
     * @param node ノードのエンコード値
     * @return 設定されていればBASE値を、未設定の場合は0を返す
     */
    static int BASE(long node){
        if ((node & FREE_MASK) == 0L)
            return (int) ((BASE_MASK & node) >> 30) ;
        return 0;
    }

    /**
     * ノードのCHECK値を返す
     * @param node ノードのエンコード値
     * @return 設定されていればCHECK値、未設定の場合は0を返す。
     */
    static int CHECK(long node){
        if ((node & FREE_MASK) == 0L)
            return (int) (CHECK_MASK & node) ;
        return 0;
    }

    /**
     * ノードが終端かどうか調べる
     * @param node ノードのエンコード値
     * @return 終端ノードであればtrue、そうでなければ false
     */
    static boolean TERMINAL(long node){
        return (node & TERMINATION_MASK) != 0L;
    }

    /**
     * ノードのTAIL配列の開始インデックスを求める
     * @param node ノードのエンコード値
     * @return TAIL配列の開始インデックス。なければ-1を返す
     */
    static int TAIL(long node){
        if ((node & TAIL_MASK) == 0L)
            return -1;
        return (int) ((BASE_MASK & node) >> 30) ;
    }

    /**
     * 次の空きノードのインデックスを返す
     * @param node ノードのエンコード値
     * @return 空きノードのインデックス。なければ-1を返す
     */
    static int NEXT(long node){
        if ((node & FREE_MASK) == 0L)
            return -1;
        return (int) (CHECK_MASK & node);
    }


    private long free_ ;
    private long termination_;
    private long tail_;
    private     int base_ ;
    private     int check_ ;

    Node(){
        free_ = FREE_MASK;
        termination_ = 0;
        tail_ = 0;
        base_ = 0;
        check_ = 0;
    }

    /**
     * コード値をデコードしたノードを生成する
     * @param code デコードするコード
     */
    Node(long code){
        this();
        decode(code);
    }

    /**
     * 現在の状態をエンコードする
     * @return エンコードした値
     */
    long encode(){
        return free_ | termination_ | tail_ | ((0L | base_) << 30) | check_ ;
    }

    /**
     * デコードした情報を内部に取り込む
     * @param n デコードするコード値
     */
    void decode(long n){
        free_        = FREE_MASK & n;
        termination_ = TERMINATION_MASK & n;
        tail_        = TAIL_MASK & n;
        base_        = (int) ((BASE_MASK & n) >> 30) ;
        check_       = (int) (CHECK_MASK & n);
    }

    /**
     * BASE値を返す。空きノードの時は0を返す。
     * @return BASE値
     */
    int base(){
        if (free_ == 0)
            return base_ ;
        return 0 ;
    }

    /**
     * BASE値を設定する。設定値が1未満ときは値が更新されない
     * @param value 設定するBASE値
     */
    void base(int value){
        if (value < 1)
            return ;
        base_ = value ;
        free_  = 0L;
    }

    /**
     * CHECK値を返す。空きノードの時は0を返す
     * @return CHECK値
     */
    int check(){
        if(free_ == 0)
            return check_;
        return 0 ;
    }

    /**
     * CHECK値を設定する。設定値が1未満のときは値が更新されない
     * @param value 設定するCHECK値
     */
    void check(int value){
        if (value < 1)
            return ;
        check_ = value;
        free_ = 0L;
    }

    /**
     * 分岐がないTAILノードかどうかを調べる
     * @return　TAILノードの場合はTAIL配列のインデックスを返す、それ以外は-1を返す
     */
    int tail(){
        if (tail_ == 0)
            return -1;
        return base_ ;
    }

    /**
     * TAIL配列上の開始位置を設定する。但し設定値が0未満の場合はなにもしない。
     * @param value TAIL配列上でのインデックス
     */
    void tail(int value){
        if (value < 0)
            return ;
        if (tail_ == 0){
            tail_ = TAIL_MASK ;
            base_ = value;
            free_ = 0L;
        }
    }

    /**
     * 終端ノードかどうか調べる
     * @return 終端ノードならtrue、そうでなければfalse
     */
    boolean isTerminal(){
        return termination_ == TERMINATION_MASK ;
    }

    /**
     * 終端ノードにする
     */
    void terminate(){
        termination_ = TERMINATION_MASK;
        free_ = 0L;
    }

    /**
     * 空きノードかどうか調べる。
     * @return 空きノードならtrue、そうでなければfalse
     */
    boolean isFree(){
        return free_ != 0;
    }

    /**
     * 次の空きノードのインデックスを返す
     * @return 次の空きノードのインデックス。なければ-1
     */
    int next(){
        if (free_ == 0L)
            return -1;
        return check_;
    }

    /**
     * 前の空きノードのインデックスを返す
     * @return 前の秋ノードのインデックス。なければ-1
     */
    int prev(){
        if (free_ == 0L)
            return -1;
        return base_;
    }

    /**
     * 空きノードへのインデックス情報を更新する。但しnextがprev以下、nextまたはprevが負の数、またはノードが空きノードでなければ更新されない。
     * @param prev 設定する前の空きノードへのインデックス
     * @param next 設定する次の空きノードへのインデックス
     */
    void updateFreeSpaceLink(int prev, int next){

        if (free_ == FREE_MASK && prev < next){
            if (prev < 0 || next < 0)
                return ;
            base_ = prev;
            check_ = next;
        }
    }
}
