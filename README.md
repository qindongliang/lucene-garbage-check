# lucene-garbage-check
##项目简介
使用lucene内存索引，根据规则，识别垃圾数据

##应用场景
识别垃圾数据，在一些大数据项目中的ETL清洗时，非常常见，比如通过关键词<br/>
（1）过滤垃圾邮件<br/>
（2）识别色情网站<br/>
（3）筛选海量简历招聘信息<br/>
（4）智能机器人问答测试<br/>
........<br/>
各个公司的业务规则都不一样，那么识别的算法和算法也不一样，这里提供一种思路，来高效快速的根据关键词规则识别垃圾数据。
<br/>
下面看下需求：
##案例分析
业务定义一些主关键词若干少则几百个，多则几千个上万个，例如：
```java
公司
机车厂
化纤厂
建设局
实业集团
中心店
桑拿中心
托管中心
交警队
调查所
稽查局
农牧局
```
然后又定义一些辅助关键词若干：
```java
原告
被告
委托代理人
当事人
申请人
上诉人
```

ok，关键词有了，下面看下业务规则 ， 规定如下：

任意辅助关键词组合主关键词都命中的情况下，并且词组间距不大于20者，即为合法数据。

嗯，没听懂？ 那么来看个例子，一段文本如下：
```java
上诉人北京金建出租汽车有限公司因机动车交通事故责任纠纷一案
```
使用IK细粒度分词后可能是这样的：
```java
上诉人|上诉|人|北京|金|建出|出租汽车|出租|汽车|有限公司|有限|有|限|公司|因|机动车|机动|车|交通事故|交通|通事|事故责任|事故|责任|纠纷|一案|
```
根据规则，辅助词库与主词库都命中，而且中间的词组间距不超过20的，为合法数据，<br/>
本例子中：<br/>
辅助关键词：上诉人<br/>
    主关键词：  公司<br/>
都出现，中间词组是12个，所以符合业务规则，即为合法数据。<br/>

假设，改变原来的文本的公司为集团，再次测试：
```java
上诉人北京金建出租汽车有限集团因机动车交通事故责任纠纷一案
```
使用IK(改造过后的 [支持lucene-solr5.x的ik](https://github.com/qindongliang/lucene-ik)的链接。)细粒度分词后可能是这样的：
```java
上诉人|上诉|人|北京|金|建出|出租汽车|出租|汽车|有限集团|有限|有|限|集团|因|机动车|机动|车|交通事故|交通|通事|事故责任|事故|责任|纠纷|一案|
```
这次因为辅助关键词库命中了，但是主关键词库没有命中，所以会被当成垃圾数据。

##性能分析

上面是帮助理解业务的一个例子，下面再分析下，性能问题，假设主关键词有500个，辅助关键词有10个，那么任意
两两组合的可能就是500\*10=5000个规则条件，也就是意味着需要最坏情况下，需要匹配5000次才能识别一篇垃圾数据，当然如果你参与识别垃圾的文本不是一个字段，而是二个字段，一个是标题，一个是内容，那么最后真正的匹配次数是5000\*2=10000词匹配，如果再加上距离条件，那么查询的复杂度将会大幅度增加，这个时候，如果我们使用正则匹配
效率可想而知，使用正则每次全文扫描定位，耗时非常之慢，这时候我们假设有一种快捷的hash算法，来提升性能，毫无疑问，类似的倒排索引将会是解决这种问题的神器。

因为只需要构建一次临时索引，不落地磁盘，不与IO打交道，仅仅在内存和cpu之间参与计算匹配，而且规则方式非常灵活，可以有更多的规则制定进来，特别是关键词匹配这块，lucene索引非常完美的解决了这个问题。当然如此这种计算，非常耗CPU，对内存的占用不是非常高，因为一条数据，处理完之后，他占用的资源，会被释放。
#####场景一
在线情况下：平均几十毫秒左右就能识别一条数据，已经接近实时了
#####场景二
离线情况下：在集成到hadoop或者Spark这种分布式的集群里面，也是非常给力的，因为通常情况下spark和hadoop比较耗IO和磁盘而加入这种运算将会大大提升集群的资源使用效率。

##后续
本项目只是给出了一个根据关键词识别的例子，这个项目拿到你们本地也许并不能立刻使用，但是相似的业务，但是它提供了一种思路，大部分情况下，改动少许代码，即可适应大部分类似的业务。

### QQ搜索技术交流群：206247899   公众号：我是攻城师（woshigcs） 如有问题，可在后台留言咨询
### [我的iteye博客](http://qindongliang.iteye.com/)  个人QQ：951514291

