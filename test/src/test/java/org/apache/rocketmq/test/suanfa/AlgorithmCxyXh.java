package org.apache.rocketmq.test.suanfa;

import org.junit.Test;

import java.math.BigDecimal;

public class AlgorithmCxyXh {

    /**
     * TODO -以后了解
     * AES算法 对称加密算法
     *  需要使用秘钥进行加密和解密。平时说的是 支持 128 196 256位加密
     *  256更复杂，更安全。加密轮数不同
     *
     * MD5 SHA 哈希摘要算法。这种算法不可逆-即加密结果不可以还原成明文
     */
    @Test
    public void Aes(){

    }

    /**
     * a*算法
     * OpentList CloseList
     * F=G(成本，已经走了多少步)+H（离目标还有多远）
     */
    public void AStar(){

    }

    /**
     * hash一致性算
     * mysql单表500w记录没有问题。 如果水平分表，则单库30个表比较合适
     * id 取摸 hash。对分库的局限-数据太大，则原来的基础上继续分表，则会打乱原来的hash
     * 一致性hash目前主要用于 分布式缓存。把各个节点想成一个hash环。redis中的slot可以想象成环
     * 存入的数据，在两个槽之间，默认属于顺时针方向的节点，如果加入新节点，则原来的数据归属可能发生了变化。查询的时候，如果没有查询到原来的节点
     * 则顺时针找下一个节点，作为当前数据的归属
     * 有可能 hash分布不均匀，可以通过虚拟节点，进行映射。 把一个节点后 加不同的后缀（统一后缀规则，）作为当前节点的虚拟节点。使数据均匀的分布在各节点间
     */
    public void hashYz(){

    }

    /**
     * 如何判断链表有环
     * 思考，
     * 1如果有环- next的时候，可以next的次数会超过链表自身的长度。
     * 2一个数会重复出现，在next的时候
     * 3next 链表长度时，判断 当前节点是否最后一个节点，如果是的话，则没有环，不是的话则有环
     * 程序员小灰策略：
     * 建立两个指针，A B .A一次移动一个节点，B一次移动两个节点。如果重复，则有环
     *
     * 引申：可以判断两个链表是否有交点。某一刻他们的next肯定相等。并求出交点。交点是第一个相同节点
     * 思路：链表A 链表B  startPosition = Max(lenA,lenB) - Min(lenA,lenB)
     * 让长的链表从位置 startPosition 开始往下遍历，短的链表从头开始遍历。如果next相等。则说明相交，此next是相交节点
     *
     */
    public void judageLinkListHashCircle(){

    }

    /**
     * 求两个数的最大公约数
     * 暴力算法：选两个数中的最小数，然后最小数除以2=结果c，然后从1遍历到数c 。找最后第一个都能除尽的数
     * 辗转相除法- 欧几里得算法
     * 两个正整数a和b（a>b），它们的最大公约数等于a除以b的余数c和b之间的最大公约数
     *
     * 九章算术
     * 两个正整数a和b（a>b），它们的最大公约数等于a-b的差值c和较小数b的最大公约数。
     */
    @Test
    public void zzxcf(){
        System.out.println(greatestCommonDivisor1(25,15));
    }

    /**
     * 最大公约数-第一种方式 暴力算法
     */
    public int greatestCommonDivisor1(int  a,int b){
        if(a <=0 || b <= 0){
            return 0;
        }
        if(a == b){
            return a;
        }
        int maxNum = Math.max(a, b);// a > b ? a : b
        int minNum = Math.min(a, b);// a > b ? b: a
        if(maxNum % minNum == 0){
            return minNum;
        }
        int midNum = minNum >> 1;
        int gcd = 0;
         for(int i = 1;i <= midNum;i++){
             if(a % i == 0 && b % i == 0){
                 gcd = i;
             }
         }
        return gcd;
    }

    /**
     * 欧几里得算法 取两个数中的大数。大数除小数的余数。求余数和小数之间的公约数。依次类推
     * @param a
     * @param b
     * @return
     */
    public int greatestCommonDivisor2(int  a,int b){
        if(a <=0 || b <= 0){
            return 0;
        }
        if(a == b){
            return a;
        }
        int maxNum = Math.max(a, b);// a > b ? a : b
        int minNum = Math.min(a, b);// a > b ? b: a
        int modNum = maxNum % minNum;
        if(modNum == 0){
            return minNum;
        }
        return greatestCommonDivisor2(modNum,minNum);
    }

    /**
     * 九章算术。取两个数中的大数。大数减小数的 剩余数。求剩余数和小数之间的公约数。一次类推
     * @param a
     * @param b
     * @return
     */
    public int greatestCommonDivisor3(int a,int b){
        if(a <=0 || b <= 0){
            return 0;
        }
        if(a == b){
            return a;
        }
//        int maxNum = Math.max(a, b);// a > b ? a : b
        int minNum = Math.min(a, b);// a > b ? b: a
        int minusNum = Math.abs(a - b);
        if(minusNum == 0){
            return minusNum;
        }
        return greatestCommonDivisor3(minusNum,minNum);
    }

    /**
     * 前三种算法的综合
     * 小数除以2。大小数求摸
     * 如果 a b 都为 偶数，则最大公约数 ，可转化为 2 * gcb(a >> 1,b >> 1)
     * 一奇，一偶
     * 两奇，大奇减小奇 则肯定出现偶数
     */
    public int greatestCommonDivisor4(int a,int b){
        if(a <=0 || b <= 0){
            return 0;
        }
        if(a == b){
            return a;
        }
        /*if(!(a&1) && !(b&1)){
            return greatestCommonDivisor4(a >> 1, b >> 1) << 1;
        }else if(!(a&1) && (b&1)){
            return greatestCommonDivisor4(a >> 1, b);
        }else if((a&1) && !(b&1)){
            return greatestCommonDivisor4(a , b >> 2);
        }else{
            return greatestCommonDivisor4(Math.min(a,b) , Math.abs(a - b));
        }*/
        return 0;

    }

    /**
     * 找出缺失的数
     * 无序数组 存储了 1到100的数，但是数组里只有99个数。找出缺失的数
     * 思路一：新建一个数组，遍历后按照数字值对应的索引进行放数。然后遍历 数组中空着的一个数
     * 思路2：遍历数组，把对应数值放到 对应数值减 1的索引处，索引处的值调换过来。最多50次调换。发现空着的索引就知道缺谁了-- 不好使
     * 思路3:1+...100 然后减去数组中的所有数相合，就知道差谁了
     *
     * 题目扩展：
     * 数组存储的数 是1-100 ,有99个数出现了偶次，1个数出现了奇次，求出现奇次的数
     * 思路一：新建一个数组，遍历后按照数字值对应的索引进行累计加1。然后遍历 数组 ，索引处放着奇数的 索引+1就是要求的数
     * 思路2：异或运算 时，相同的为0 ，不同的为1. 偶数此出现的最后都变成0了，奇数此运算的被留下来了
     *
     * 题目扩展：
     * 数组存储的数 是1-100 ,有98个数出现了偶次，2个数出现了奇次，求出现奇次的数。
     * 把两个出现奇次的数分到不同的组，然后进行运算。两个奇数不相等。所以 最后一个二进制数肯定是1 和 0 异或的结果
     */
    @Test
    public void findLostNum(){
        System.out.println(Integer.toBinaryString(23));

    }

    /**
     * 在一个栈，中getMin put pop 放时间复杂度o(1)
     */
    public void stackFinMin(){

    }

    /**
     * 判断一个数是2的乘方。根据二进制进行判断。如果首位是1 其它为0肯定是2的乘方 。2 n次方 & 2 n次方减1 等于0
     * 判断一个数二进制中有几个1, 思路：数字&1 true则为奇数，然后右移1位，继续 &1操作，一直到
     */
    public void judgeOneNumIsTwoMulti(){

    }

    /**
     * 一个无需数组，求排序后任意两个相邻元素的最大差值（注意相邻）
     * 只要找出最大值和最小值即可
     *  定义两个数 一个记录最小值一个记录最大值
     *
     *  思路1：
     *  遍历数组
     *  先求出最大值max  最小值min
     *  max - min + 1 建立此数组 newArr
     *  然后再次遍历数组，把数组中的元素 按 arr[i] - min 放入数组，然后统计 数组中最大连续空着的位置，则两边的数为 差值最大的相邻数
     *
     *  思路2：
     *  思路入如果差值太大，元素太少。则浪费数组空间。利用桶排序
     *  划分 (max - min)/arrLen = d 则是分片大小
     *
     */
    public void noSortIntArrMaxMinusTest(){

    }

    /**
     * 题目理解错了，只能求出最大 和最小的差值
     * @param arr
     * @return
     */
    public int noSortIntArrMaxMinus(int[] arr){
        if(arr == null || arr.length < 1){
            return 0;
        }
        int len = arr.length;
        int min = arr[0];
        int max = arr[0];
        for(int i=1 ;i < len ;i--){
            if(arr[i] < min){
                min = arr[i];
            }else if(arr[i] > max){
                max = arr[i];
            }
        }
        return max - min;
    }

    /**
     * 动态规划
     * 分阶段解决决策问题的数学思想
     * 动态规划的三个概念：
     *  最优子结构
     *  边界
     *  状态转移公式
     *
     */
    public void dtghTest(){

    }

    /**
     * 有一座高度为10的阶梯，从下往上走，每次可以走1级或者2级。求一共有多少中走法
     * F（10） = F（9） + F（8）  F（9）和F（8）称为最优子结构 具体值的具体求法
     *这里 F（1） 和F（2）是明确的值 称为边界
     * F（1）=1
     * F（2）=2
     * F（3）= F（1）+F（2）
     * F（n）=F(n-1) + F(n-2) --称为状态转移方程
     *
     * 上面的分析称为问题建模
     * 后面还有求解阶段
     * */
    public void dtgh1Test(){

    }
    public int dtgh1Solve1(int level){
        if(level < 1){
            return 0;
        }else if(level == 1){
            return 1;
        }else if(level == 2){
            return 2;
        }else{
            return dtgh1Solve1(level -1) + dtgh1Solve1(level - 2);
        }
    }

    /**
     * 备忘录算法- 把已经算的值缓存起来 。使用map 或者arr都可以
     * @param level
     * @return
     */
    public int dtgh1Solve11(int level,int[] arr){
        if(level < 1){
            return 0;
        }else if(level == 1){
            return 1;
        }else if(level == 2){
            return 2;
        }else{
            if(arr[level] > 0){
                return arr[level];
            }else{
                int value = dtgh1Solve1(level -1) + dtgh1Solve1(level - 2);
                arr[level-1] = value;
                return value;
            }
        }
    }
    /**
     * 备忘录算法- 把已经算的值缓存起来 。使用map 或者arr都可以
     * 只需要保留之前两个状态 便可推导出之后的状态。不需要保留所有状态
     * @param level
     * @return
     */
    public int dtgh1Solve12(int level,int[] arr){
        if(level < 1){
            return 0;
        }else if(level == 1){
            arr[0] = 1;
            return 1;
        }else if(level == 2){
            arr[1] = 2;
            return 2;
        }
       /* else{
            int value = arr[level-1-1] + arr[level-2-1];
            arr[0] = arr[1];
            arr[1] = value;
            return value;
        }*/

        int a =1;
        int b = 2;
        int tmp = 0;
        for(int i=3;i <=level;i++){
            tmp = a+b;
            a=b;
            b=tmp;
        }
        return tmp;
    }

    /**
     * 国王挖矿问题
     * 5座金矿，10个工人。矿要么全挖，要么不挖。
     * 500-5 200-3 300-4 350-3 400-5
     * 应该怎样选矿
     */
    public void dtghSolve2(){

    }

    /**
     * 跳跃表- 有序链表。找到有序链表。对有序链表中的关键节点建立索引。可以在索引的基础上再次建立索引
     * 若干添加元素，通过索引找到大概要插入的位置。然后通过抛硬币的方式，确定，此元素是否需要添加到上级索引中，
     * 添加后再通过抛硬币方式确定此元素是否需要添加到上上级索引中
     * 删除元素的时候。确定删除位置，进行删除，如果刚好删除的元素是索引，则删除索引
     * redis中的sorted-set 用了跳跃表的思想
     */
    public void tybTest(){

    }


    @Test
    public void test1(){
      System.out.println(new BigDecimal(1).negate());//取负数
        System.out.println(new BigDecimal(-1).negate());
    }

}
