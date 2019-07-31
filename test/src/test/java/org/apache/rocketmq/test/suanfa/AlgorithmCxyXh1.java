package org.apache.rocketmq.test.suanfa;

import org.junit.Test;

import java.math.BigDecimal;

public class AlgorithmCxyXh1 {

    /**
     * 八皇后问题。通过递归回溯 进行实现-本质上是一种枚举
     * 8*8 国际象棋上，摆放8个皇后，不能相互攻击。任意两个皇后 不能在 同一行 同一列 和同一对角线上
     * 首先从第一行开始， 摆放一个皇后，然后其 所在行列，斜对角数据都置为1
     * 在第一个没有锁定的格子上摆放皇后
     * 然后第二行，找没有锁死的格子，如果不满足条件，则往右移动一格。
     * 如果还是不满足，则回溯到上一行，往右移动一个格子
     * -- 找出第一种正确的摆放方式，深度优先
     * -- 找出所有的摆放方式，广度优先
     *
     */
     public void bhhTest(){

     }

    /**
     * 当有皇后放至时 对应元素值为1
     */
     public class queue8{
         static final int MAX_NUNM = 8;
         int[][] chessBoard = new int[MAX_NUNM][MAX_NUNM];


         boolean check(int x,int y){
             for(int i=0;i< y;i++){
                 if(chessBoard[x][i] == 1){
                     return false;
                 }
                 //因为是一行一行 往下填充的。所以只能往上面的行进行检查
                 //检查左上角
                 if(x-1-i >= 0 && chessBoard[x-1-i][y-1-i] == 1){
                     return false;
                 }
                 if(x+1+i < MAX_NUNM && chessBoard[x+1+i][y-1-i] == 1){
                     return  false;
                 }
             }
             return true;
         }

         boolean settleQueue(int y){
             if(y == MAX_NUNM){
                 return true;
             }
             for(int i = 0;i< MAX_NUNM;i++){
                 for(int x=0;x<MAX_NUNM;x++){//一旦回溯到已经操作过的行，原来设置为1的地方都要清理为 0
                     chessBoard[x][y] = 0;

                 }
                 if(check(i,y)){
                     chessBoard[i][y] = 1;
                     //递归如果返回true，说明下层已经找到解法，无需继续循环
                     if(settleQueue(y+1)){
                         return true;
                     }
                 }
             }
             return false;
         }

     }

     //八皇后。设置起点可以不设置起点。默认从0开始

    //找出所有八皇后
    //设置x 起始位置


    /**
     * 字典算法
     * 给点一个数字。通过换位算法找出离改整数最近大于自身的换位数
     * 输入12345  输出 12354
     * 输入12354  输出 12435
     * 一个数字 的最大数 是对数字中的单个数进行排序，顺序是最小值，逆序是最大值
     * 例如：13452 按数排序 12345 最小值  54321最大值
     *
     * 因为是为了查找里 自身数最近且大于。所以这里高位数首先不变。从低位数开始交换
     *
     *
     */
    public void zdffTest(){

    }

    void zdff(int num){
        if(num <= 10){
            return;
        }
        String str ="";

    }

    /**
     * 抢红包算法
     *1 所有人抢到的红包总数-不能多也不能少
     * 2每个人至少抢到1分钱
     * 3保证每个人抢到红包的概率是相同的
     * 方法1：二倍均值法
     * 红包金额(0,M/N * 2)
     */
    public void qhbSf(){

    }
}
