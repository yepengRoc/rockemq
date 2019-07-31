package org.apache.rocketmq.test.lamdaSty;

import javassist.compiler.ast.ASTList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaSty {
    /**
     *lambda是一个匿名函数
     * lambda的主体可以是一个表达式，也可以是一段代码-使用大括号（{}）将代码括起来
     * lambda的参数
     *  无参--（）
     *  只有一个参数，不用写括号直接写参数名
     *  lambda中的参数类型可以有编译器推断得出。--当时有时候也可以显式的声明参数类型
     * 表达式类型，依赖上下文环境，编译器是根据上下文环境进行类型推断
     *
     * lambda表达式都是静态类型的。
     * lambda表达式中使用的值，可以不声明final，但是其实就是final，多次改变值，然后再lambda表达式中引用会报错
     *
     */
    @Test
    public void sty1(){

    }
    /**
     * Predicate<T> T boolean 这张唱片已经发行了吗
     * Consumer<T> T void 输出一个值
     * Function<T,R> T R 获得 Artist 对象的名字
     * Supplier<T> None T 工厂方法
     * UnaryOperator<T> T T 逻辑非 （!）
     * BinaryOperator<T> (T, T) T 求两个数的乘积 （*）
     */

    /**
     * titile：类型推断
     *函数接口指仅具有【【单个】】抽象方法的接口，用来表示 Lambda 表达式的类型。
     */
    @Test
    public void sty2(){
        Predicate<Integer> atLeast5 = x -> x > 5;

        BinaryOperator<Long> add = (x,y) -> x+y;

        Function<Integer,String> apply = x -> x+"";
    }

    /**
     * stream 流
     * filter这样只描述stream，最终不产生新集合的方法叫惰性求值方法
     * count 最终从strea产生值的方式叫及早求值方法
     *
     * 如何区分惰性和及早
     *  如果返回的是stream，则是惰性（类似于build建造者模式）；返回其它则是及早
     */
    @Test
    public void sty3(){
        List<String> arr = new ArrayList<>();
        arr.add("a");
        arr.add("b");
        arr.add("a");
        /**
         * 通过stream流实现集合的内部迭代。iterator是外部迭代
         */
        long count = arr.stream().filter(str -> str.equals("a")).count();
        System.out.println(count);
        /**
         * 由于惰性求值，所以不会产生 syso 输出
         */
        arr.stream().filter(str ->{
                    System.out.println(str);
            return str.equals("a");
                });
        /**
         * 由于count终止了stream，所以有输出
         */
        arr.stream().filter(str ->{
            System.out.println(str);
            return str.equals("a");
        }).count();

    }

    /**
     * Stream 常用操作
     */
    @Test
    public void normalOpt(){
        /**
         * list操作  通过stream创建一个list
         */
          List<String> list = Stream.of("a","b","c").collect(Collectors.toList());

        /**
         *map操作
         * 将一种类型的值转换为另一种类型的值
         */
        List<String> listA = Stream.of("a","b","c").map(str -> str.toUpperCase()).collect(Collectors.toList());

        /**
         * filter操作
         */
        List<String> listB = Stream.of("a","b","c").filter(str -> str.equals("a")).collect(Collectors.toList());

        /**
         * flatMap操作。使用stream替换值-生成新的stream替换原来的stream，将多个stream合并成一个stream
         */
        List<Integer> list1 = Stream.of(1,2).collect(Collectors.toList());
        List<Integer> list2 = Stream.of(3,4).collect(Collectors.toList());
        List<Integer> listC = Stream.of(list1,list2).flatMap(num -> num.stream()).collect(Collectors.toList());

        /**
         * 最大 最小值
         */
        List<Track> listTrack = Stream.of(new Track("tom",23),new Track("kim",15),new Track("baka",24)).collect(Collectors.toList());
        /**
         * 排序规则
         */
        listTrack.stream().min(Comparator.comparing(track -> track.getLen())).get();//获取len最小的对象

        /**
         * reduce 模式  将前一步的结果累计到后一步，一直到整个计算结束
         */
        /**
         * 从下标0开始， 让所有数累加。从0开始，首先取两个数相加，结果作为下一步相加的第一个数。
         */
        int count = Stream.of(1,2,3).reduce(0,(a,b) -> a + b);

        /**
         * 整合操作
         * 思路相同-- 这里
         * 首先拿到所有表演者-》拿到乐队-》拿到乐队国家-》组装成一个list
         * 表演者.filter(bl -> bl.startWith('the')).map(bl -> bl.国家).collect(Collectors.toList());
         * stream很好的封装了内部的实现结构，通过只暴露一个stream接口，在使用时不改变原有的集合对象
         */
      //  Stream.of(Object, Object).filter(obj -> obj.name.startWith("the")).map(obj -> obj.getGJ).collect(Collectors.toList());

        /**
         * 从表演者中获取专辑，找出其中所有长度大于1的曲目。并将这些曲目保保存下来
         * 专辑 -> 专辑中大于60的曲目
         * 表演者.flatMap(表演者 -》 获取每个表演者的专辑).filter(专辑 -》 专辑播放大于60).map(专辑 -》 专辑名称).collect(Collectors.toList());
         * flatmap() 将所有专辑合并到一个流里
         * filter 过滤大于60的曲目
         * map 拿到曲目名字
         *
         */
        //选定一张专辑，找出其中所有长度大于1的曲目。并将这些曲目保保存下来

        /**
         *
         */

    }

    class Track{
        private String name;

        private int len;
        Track(String name,int len){
            this.name = name;
            this.len = len;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }
    }

    /**
     *高阶函数
     *  接受一个函数作为参数，然后返回一个函数
     *  局部变量在既成事实上必须是 final 的
     */
    @Test
    public void highterOrderFun(){
        //计算一个字符串中小写字符的个数
//        "adbcDDDF".equals();
        System.out.println(Stream.of("adbcDDDF".toCharArray()).filter(x -> "abcd".contains(x.toString())).collect(Collectors.toList()).size());
        Stream.of("adbcDDDF".toCharArray()).filter(x -> "abcd".indexOf(x.toString()) > -1).collect(Collectors.toList()).size();
    }

    /**
     * 聚合函数
     * 整形在内存中占 4字节 整形对象占16字节
     * 基本类型做了特殊处理，命名上有明确规范
     *  如果返回是基本类型，则基本类型前加To作为方法名ToLongFunction
     *  如果参数是基本类型，则不加前缀
     *  如果使用高阶函数，则操作后+To+基本类型。例：mapToLong
     * 每一个基本类型都有对应的stream，以基本类型了名为前缀，例LongStream、
     * mapToLong 返回一个stream，其中map接收一个LongUnaryOperator函数，将一个长整形映射为另一个长整形
     * mapToObj 可以从一个类型Stream得到另一个Stream 例：Stream<Long>
     *
     * 统计曲目长度
     * summaryStatistics方法
     * 对象.mapToInt(track -> track.getLength()).summartyStatistics
     *  这里通过mapToInt 返回一个IntStream对象，里面有一个方法summaryStatistics 对应IntSummaryStatistics
     *  对应的有LongStream DoubleStream 都有此方法，此方法返回的对象有 min max average sum等方法
     */
    @Test
    public void combinFunc(){
//        Logger logger = new Logger();
//        logger.debug(() -> "日志：");

    }

    /**
     * 重载解析
     * Lambda表达式作为参数，其类型由目标类型推到得出，遵循规则：
     *  如果只有一个可能的目标类型，由响应函数接口里的参数类型推导得出
     *  如果有多个可能的目标类型，由最具体的类型推导得出
     *  如果有多个可能的目标类型且最具体的类型不明确，则需要人为指定类型
     *
     *  为提高stream对象的可操作性而引入的新接口，需要有lambda表达式实现。他们存在的意义在于将代码作为数据打包起来。通过
     * @FunctionalInterface进行标识。  一般一个接口中的只有一个方法，当用做lambda表达式时，也应该加此注解，此注解可以在编译时
     * 就提示出错误。会检查一个接口是否符合函数接口的标准。如果给类 枚举 或者接口中不止包含一个抽象方法。 则会报错
     *
     * 默认方法。在jdk向前兼容的过程中，如果原来的接口类加了新方法，则后面在实现的过程中，必须重写此新方法。
     *  default 定义，则使编译器不报错，默认使用此方法，当然实现类也可以自己再实现一套，调用的时候则调用实现类实现的方法。
     *
     *  如果子实现类，实现的两个父类中有两个相同的默认实现方法，编译器会报错，因为不知道要执行那个
     *  interface a
     *      default void method
     *  interface b
     *      default void method
     *  class c implements a ,b
     *      public void method(){
     *          a.super.method();//自己实现一套，或者指定调用的是哪个父类的方法
     *      }
     *  三定律进行确认：
     *      1类胜于接口
     *      2 子类胜于父类
     *      3如果上面两条不适用，子类要么实现该方法，要么将方法声明为抽象方法
     *
     *
     *
     */
    @Test
    public void overLoadAnalyse(){

    }

    /**
     * reduce
     * 如果reduce操作的时候，没有传入值，则返回一个optional对象
     *  此对象设计：使开发进行空检查；将null文档化，方便代码维护
     */
    @Test
    public void reduceBc(){
        Optional<String> a = Optional.of("a");
        System.out.println(a.get());

        Optional empt = Optional.empty();//创建一个空对象

        Optional nullOpt = Optional.ofNullable(null);//将null转换为一个optional对象
        /**
         * isPresent用来检查optional是否有值
         */
        System.out.println(empt.isPresent());//
        System.out.println(a.isPresent());
        empt.orElse("b");//如果为空返回b
        empt.orElseGet(()->"c");//如果为空返回c
    }

    /**
     * 高级用法
     */
    @Test
    public void gjUseMethon(){
        /**
         * artist -> artist.getName()
         * Artist :: getName//方法引用
         * 以上两者等同
         * 标准格式：ClassName::methodName 类名称：：方法名
         * 凡是使用lambda的地方都可以使用方法引用
         * -- 构造函数也可以
         * （name,nation）-> new Artist(name,nation)
         *  方法引用写法：Artist::new
         *  方法引用自动支持多个参数
         */
        /**
         * stream 并行处理的时候，并不能保证元素是按顺序处理的。如果需要按顺序进行处理，需要使用forEachOrdered方法
         */
        /**
         * 收集器（通用，从流生成复杂值的结构）。通过收集器将一种数结构转换为另外一种数据结构。
         *  将收集器传给Collectors。就可以使用
         */
        /**
         * stream.collect(toCollection(TreeSet::new))
         */
        /**
         * 查找成员最大的乐队
         * artists.collect(maxBy(comparing(artist -> artist.getMembers().count())))
         * minBy-查找最小
         * averaginInt(lambda表达式) -- 查找平均数
         *
         */
        /**
         * 数据分块。将一个流按照特征，分成多个对应的流
         * partitioningBy 接收一个流，将其分成两个部分
         * 例：将艺术家分成乐队和独唱两部分
         * Map<Boolean,List<Artist>  artists.collect(partitioningBy(Artist::isSolo))
         *      等同于 arttists.collect(partitioningBy(artist -> artist.isSolo()))
         *
         * 数据分块 groupBy 使用主唱对专辑分组
         * Map<Artist,List<Artist>  artists.collect(groupBy(artist -> artist.get主唱()))  等同 Artist::get主唱
         *
         * 分类器是一个function对象
         *
         */
        /**
         * 字符串操作 。将所有艺术家的名字放到一个字符数组里
         * artist.stream().map(Artist::getName).collect(Collects.join(",","[","]"))
         *
         * 计算一个艺术家的专辑数量.组合收集器，这里先根据专辑进行分组，然后通过counting方法返回分组后专辑的数量
         * artist.collect(groupBy(artist->artist.专辑(),counting()))
         * 计算每个艺术家的专辑名字，放在一个字符数组中。将分类的数据通过mapping映射成自己想要的数据格式返回
         * artist.collect(groupBy(artis->artist.get专辑（），mapping(artis->artis.getName(),toList())))
         *
         * counting mapping 称为下游收集器。主收集器生成结果，下游收集器生成部分结果配方。可以通过查看api来查看
         * 有哪些下游收集器。这些方法其实是和stream上的方法是等价的
         */
        /**
         * 重构和定制收集器
         * 艺术家的名字放到一个字符数组里
         * String rst = artis.stream().map(artist -> artist.getName()).reduce(new StringCombiner(",","[","]"),StringCombiner::add,StringCombiner::merge).toString();
         * String rst = sc.toString();
         *
         *
         */




    }
    class StringCombiner{

        StringBuilder build = new StringBuilder();

        String preFix = "";
        String delim = "";
        String suffix = "";

        StringCombiner(String preFix,String delim,String suffix){
            this.preFix = preFix;
            this.delim = delim;
            this.suffix = suffix;
        }

        public StringCombiner add(String ele){
            if(areAtStart()){
                build.append(preFix);
            }else{
                build.append(delim);
            }
            build.append(ele);
            return this;
        }

        public StringCombiner merge(StringCombiner other){
            build.append(other.build);
            return this;
        }

        public boolean areAtStart(){
            return true;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    /**
     * 第一个泛型：待收集元素的类型
     * 第二个泛型：累加器的类型StrignCombiner
     * 第三个泛型：最终结果类型String
     */
    class StringCollector implements Collector<String,StringCombiner,String>{
        /**
         * 工厂方法，用来创建容器
         * @return
         */
        @Override
        public Supplier<StringCombiner> supplier() {
            String delim = "";
            String prefix = "";
            String suffix = "";
            return () -> new StringCombiner(delim,prefix,suffix);
        }

        /**
         * accumulator 是一个函数，它将当前元素叠加到收集器
         * @return
         */
        @Override
        public BiConsumer<StringCombiner, String> accumulator() {
            return StringCombiner::add;
        }

        /**
         * 类似于reduce方法，如果有两个容器需要合并(将多个收集器的数据合并到一个容器里)
         * @return
         */
        @Override
        public java.util.function.BinaryOperator<StringCombiner> combiner() {
            return StringCombiner::merge;
        }

        /**
         * 收集器finisher.对结果进行转换。转换成想要的格式
         * @return
         */
        @Override
        public java.util.function.Function<StringCombiner, String> finisher() {
            return StringCombiner::toString;
        }

        /**
         * 特征是描述收集器的对象。
         * @return
         */
        @Override
        public Set<Characteristics> characteristics() {
            return null;
        }
        /**
         * 有一个java.util.StringJoiner和这里自定义的收集器功能类似。这里只做示例展示
         * 这里的收集收集的是一个可变对象（即每次都要新创建新值），有些场景收集的是不变对象。
         * 如：处理的对象是list，处理完后还是list ，则不需要对finisher方法进行改造，因为不需要对容器做任何操作
         * 这时的finisher是identity函数：返回传入参数的值。这样收集器就展示了IDENTITY_FINISH的特征，需要使用
         * characteristics进行声明
         */

        //一般为自己的领域类定制
    }

    /**
     * 一些细节
     */
    @Test
    public  void someDetail(){
        Map<String, String> cacheMap = new HashMap<>();
        String name = "";
        /**
         * 如果利用指定的key可以获取到数据，则返回获取到的数据。
         * 如果获取不到，则返回默认的数据
         */
        /*
        cacheMap.computeIfAbsent(name,this::"默认值-或从数据库获取");
        cacheMap.compute(name,this::"");//如果希望值不存在的时候不计算，则使用这种形式
        */
        /**
         * map的遍历方式
         */
        Map<String, Integer> exaMap = new HashMap<>();
        exaMap.put("a", 2);
        exaMap.put("b", 3);
        Map<String, Integer> newMap = new HashMap<>();
        exaMap.forEach((key,value) -> {newMap.put(key,value-1);});
    }


    /**
     * 并发是指可以给多个任务分配时间片
     * 并行是指可同时处理（将一个任务进行分解，不同部分同时进行处理）
     */

    public void parallelOpt(){//
        //artis.parallelStream().flatMap(),mapToInt(),sum()

        /**
         * 蒙特卡洛模拟法并行化模拟  92页 回头再看看 TODO
         * IntStream.range(0, N) ?
         * .parallel() ?
         * .mapToObj(twoDiceThrows()) ?
         * .collect(groupingBy(side -> side, ?
         * summingDouble(n -> fraction))); ?
         */
        /**
         * 并行工作的规则和限制
         * 调用 reduce 方法，初始值可以为任意值，为了让其在并行化时能工作正常，初值必须
         * 为组合函数的恒等值。拿恒等值和其他值做 reduce 操作时，其他值保持不变。比如，使用
         * reduce 操作求和，组合函数为 (acc, element) -> acc + element ，则其初值必须为 0， 因
         * 为任何数字加 0，值不变。
         * reduce 操作的另一个限制是组合操作必须符合结合律。这意味着只要序列的值不变，组
         * 合操作的顺序不重要。
         *
         *  parallel 方法能轻易将流转换为并行流。还有一个叫 sequential 的方法。
         *  不能同时处于两种模式，要么是并行的，要么是串行的。如果同时调用了 parallel
         * 和 sequential 方法，最后调用的那个方法起效。
         * parallel 和sequential方法不能同时生效，最后调用的方法会覆盖前面调用的方法
         */

        /**
         * 并行和串行需要考虑的因素：
         * 1数据量大小-拆分组合存在时间消耗
         * 2机器核心数-如果核心数为1 没有拆分必要
         * 3基本数据类型比装箱类型处理的快
         * 4流处理花费的时间比越大，性能提升越多  流处理占用时间/（拆分占用时间+流处理占用时间+合并占用时间）
         *
         * 底层，通过 fork/join来实现的。fork递归式分解问题，join合并结果
         *
         * 分解的时候跟数据结构有关，数组类的数据结果比较好处理
         */

        /**
         * 计算简单滑动平均数  96页  TODO
         */


    }
    /**
     * 测试用例
     */
    @Test
    public void testCase(){
        /**
         * log 用法
         */
/*        Logger log = new Logger();
        log.debug(() -> "日志");*/

        ThreadLocal<Album> thisAlbum = new ThreadLocal<Album>(){
            @Override
            protected Album initialValue() {//初始化的时候去找一次数据库
                //这里可以改为去数据库查找
                return super.initialValue();
            }
        };
        //通过withInitial 可以传入一个supplier对象。 代码的重用与组合
        ThreadLocal.withInitial(() -> "去数据库查找");//等效于以上写法




    }

    class Album{

    }


    /**
     * 不要编写重复代码
     */
    public void norepeatCode(){
        //albums.stream().mapToLong(album -> album.getTracks.mapToLong(track->track.getLength()).sum()).sum()
        //albums.stream().mapToLong(album -> album.getMusic().count()).sum()
        //albums.stream().mapToLong(album -> album.getTracks.getTracks().count()).sum()
        // 其中的lamabda是重复内容。ToLongFuction<Album> func 接收一类lambda
        /**
         * long countFeature(ToLongFuction<Album> func ){
         *     albums.stream().mapToLong(func).sum();
         * }
         * long countTracks(){
         *     countFeature(album -> album.getTracks())
         * }
         * long countRunTime(){
         *     countFeature(album->album.getTracks().mapToLong(track-> track.getLength()).sum())
         * }
         * long countMusic(){
         *     countFenture(album ->album.getMusic())
         * }
         */


    }

    /**
     * 因为lambda方法匿名，所以不好测试。在对lambda表达式测试的时候，需要将表达式转换为实际的方法引用进行测试
     * 使用测试替身进行测试
     * 使用Mockito 进行测试
     * 使用peek方法进行测试
     *
     */
    public void debugProgram(){

        /**
         * 使用peek查看中间阶段产生的值
         * album.getMusicians().filter(artist->artist.getName().startWith("The")).map(artist->getNationality())
         * .peek(nation -> log.info("nation:" + nation)).collect(Collectors.<String>toSet());
         *
         * 也可以在peek中加入断点
         */

    }

    /**
     * 设计模式
     */
    public void  designPattern(){
        /**
         * 命令行模式 -根据传入参数，调用对应方法的通用模式
         * 策略模式-在运行时改变软件的算法行为
         */

    }







}
