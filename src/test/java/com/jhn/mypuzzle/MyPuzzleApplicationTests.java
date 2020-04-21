package com.jhn.mypuzzle;

import com.google.common.base.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultiset;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;

import javax.jms.*;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


class MyPuzzleApplicationTests {

    @Test
    void swapByXor() {
        int[] arr = {3, 10};
        int[] afterSwap = swap(arr, 0, 1);
        for (int i : afterSwap) {
            System.out.println(i);
        }
    }

    //两数异或结果再和一个数进行异或，得到另外一个数的结果
    int[] swap(int[] arr, int index1, int index2) {
        arr[index1] = arr[index1] ^ arr[index2];
        arr[index2] = arr[index1] ^ arr[index2];
        arr[index1] = arr[index1] ^ arr[index2];
        return arr;
    }

    @Test
    void testRandomArray(){
       int[] list = randomArray(100, 8);
        System.out.println(Arrays.toString(list));
    }

    /**
     *
     * @param size 生成随机数组的大小
     * @param maxNum 生成随机数组最大大小
     */
    int[] randomArray(int size, int maxNum){
        int[] list = new int[size];
        for (int i = 0; i < size; i++) {
            int randomNum = (int)(Math.random() * (maxNum + 1));
            list[i] = randomNum;
        }
        return list;
    }

    @Test
    void testBuboSort(){
        /*
        对数器：产生随机数并按照暴力方式进行排序，再使用算法进行重新排序，再进行对比。
         */
        final int caseTimes = 500000;
        for (int i = 0; i < caseTimes; i++) {
            int[] randomArray = randomArray(100, 8000);
            int[] sortArray = Arrays.copyOf(randomArray, randomArray.length);
            Arrays.sort(sortArray);
            int[] buboSortArray = buboSort(randomArray);

            for (int j = 0; j < buboSortArray.length; j++) {
                assert sortArray[j] == buboSortArray[j];
            }
        }
    }

    int[] buboSort(int[] list){
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < i; j++) {
                if (list[i]<list[j]){
                    swap(list,i,j);
                }
            }
        }
        return list;
    }

    @Test
    void testGuava(){
        //Optional 同jdk
        Optional<Integer> possible = Optional.of(5);
        System.out.println(possible.isPresent());
        System.out.println(possible.get());
        //Preconditions 参数校验
        Preconditions.checkArgument(1==1);
        Objects.equal(null,1);
        Objects.hashCode(new Integer(1));
        //新集合
        HashMultiset<Integer> multiset = HashMultiset.create();
        multiset.add(1);
        multiset.add(1);
        multiset.add(2);
        multiset.add(3);
        Set<Integer> integers = multiset.elementSet();
        for (Integer integer : integers) {
            System.out.println(integer);
        }
        System.out.println(multiset.count(1));

        //连接器、分割器
        Joiner joiner = Joiner.on("; ").skipNulls();
        System.out.println(joiner.join("Harry", null, "Ron", "Hermione"));
        System.out.println(Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split("foo,bar,,   qux,"));

        System.out.println(MoreObjects.toStringHelper(this)
                .add("x", 1)
                .toString());
    }

    @Test
    void testGuavaCache(){
        LoadingCache<String, String> ID = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, String>() {
                            @Override
                            public String load(String key) {
                                return createExpensiveString(key);
                            }

                            private String createExpensiveString(String key) {
                                return UUID.randomUUID().toString() + "-" + key;
                            }
                        });
        try {
            ID.get("1");
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testActiveMqSendMessage() throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                "tcp://localhost:61616");
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue testQueue = session.createQueue("testQueue");
        MessageProducer producer = session.createProducer(testQueue);
        TextMessage textMessage = session.createTextMessage("test");
        producer.send(textMessage);
        connection.close();

    }

    @Test
    void testActiveMqGetMessage() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                "tcp://localhost:61616");
        Connection connection = null;
        Session session = null;
        try {
            connection = activeMQConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue testQueue = session.createQueue("testQueue");
            MessageConsumer consumer = session.createConsumer(testQueue);
            while (true){
                TextMessage message = (TextMessage) consumer.receive();
                System.out.println("message:" + message.getText());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
