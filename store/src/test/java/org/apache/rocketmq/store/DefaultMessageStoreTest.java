package org.apache.rocketmq.store;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.rocketmq.common.BrokerConfig;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.MessageAccessor;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.store.config.FlushDiskType;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.apache.rocketmq.store.config.StorePathConfigHelper;
import org.apache.rocketmq.store.stats.BrokerStatsManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageStoreTest {
    private final String StoreMessage = "Once, there was a chance for me!";
    private int QUEUE_TOTAL = 100;
    private AtomicInteger QueueId = new AtomicInteger(0);
    private SocketAddress BornHost;
    private SocketAddress StoreHost;
    private byte[] MessageBody;
    private MessageStore messageStore;

    @Before
    public void init() throws Exception {
        StoreHost = new InetSocketAddress(InetAddress.getLocalHost(), 8123);
        //生产消息的地址
        BornHost = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0);
        /**
         * 构建同步刷盘方式
         */
        messageStore = buildMessageStore();
        /**
         * 构建异步刷盘方式
         */
//        messageStore = buildMessageStore2();
        boolean load = messageStore.load();
        assertTrue(load);
        messageStore.start();
    }

    @Test(expected = OverlappingFileLockException.class)
    public void test_repeat_restart() throws Exception {
        QUEUE_TOTAL = 1;
        MessageBody = StoreMessage.getBytes();

        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
        messageStoreConfig.setMapedFileSizeCommitLog(1024 * 8);
        messageStoreConfig.setMapedFileSizeConsumeQueue(1024 * 4);
        messageStoreConfig.setMaxHashSlotNum(100);
        messageStoreConfig.setMaxIndexNum(100 * 10);
        MessageStore master = new DefaultMessageStore(messageStoreConfig, null, new MyMessageArrivingListener(), new BrokerConfig());

        boolean load = master.load();
        assertTrue(load);

        try {
            master.start();
            master.start();
        } finally {
            master.shutdown();
            master.destroy();
        }
    }

    @After
    public void destroy() {
        messageStore.shutdown();
        messageStore.destroy();

        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
        File file = new File(messageStoreConfig.getStorePathRootDir());
        UtilAll.deleteFile(file);
    }

    private MessageStore buildMessageStore() throws Exception {
        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
       /* messageStoreConfig.setMapedFileSizeCommitLog(1024 * 1024 * 10);
        messageStoreConfig.setMapedFileSizeConsumeQueue(1024 * 1024 * 10);
        messageStoreConfig.setMaxHashSlotNum(10000);
        messageStoreConfig.setMaxIndexNum(100 * 100);*/
        messageStoreConfig.setMapedFileSizeCommitLog(1024 * 10);//一个文件大小是10k
        messageStoreConfig.setMapedFileSizeConsumeQueue(256);//一个consumer文件大小 256k 就切换一个
        messageStoreConfig.setMaxHashSlotNum(10);
        messageStoreConfig.setMaxIndexNum(200);
        messageStoreConfig.setFlushDiskType(FlushDiskType.SYNC_FLUSH);
        messageStoreConfig.setFlushIntervalConsumeQueue(1);//刷盘间隔 comsumequeue
        //默认落盘
        return new DefaultMessageStore(messageStoreConfig, new BrokerStatsManager("simpleTest"), new MyMessageArrivingListener(), new BrokerConfig());
    }

    /**
     * 构建异步刷盘设置 TODO
     * @return
     * @throws Exception
     */
    private MessageStore buildMessageStore2() throws Exception {
        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
       /* messageStoreConfig.setMapedFileSizeCommitLog(1024 * 1024 * 10);
        messageStoreConfig.setMapedFileSizeConsumeQueue(1024 * 1024 * 10);
        messageStoreConfig.setMaxHashSlotNum(10000);
        messageStoreConfig.setMaxIndexNum(100 * 100);*/
        messageStoreConfig.setMapedFileSizeCommitLog(1024 * 10);//一个文件大小是10k
        messageStoreConfig.setMapedFileSizeConsumeQueue(256);//一个consumer文件大小 256k 就切换一个
        messageStoreConfig.setMaxHashSlotNum(10);
        messageStoreConfig.setMaxIndexNum(200);
        messageStoreConfig.setFlushDiskType(FlushDiskType.ASYNC_FLUSH);
        /**
         * 是否启用堆外内存池-- TODO 异步模式的可以开启
         */
       // messageStoreConfig.setTransientStorePoolEnable(true);//堆外内存池设置
        messageStoreConfig.setFlushCommitLogTimed(true);//是否定时调度刷盘。默认是false 实时刷盘
        messageStoreConfig.setFlushIntervalConsumeQueue(1);//刷盘间隔 comsumequeue
        //默认落盘
        return new DefaultMessageStore(messageStoreConfig, new BrokerStatsManager("simpleTest"), new MyMessageArrivingListener(), new BrokerConfig());
    }

    /**
     * 写消息和读消息
     */
    @Test
    public void testWriteAndRead() throws Exception{
       /* long totalMsgs = 10;
        QUEUE_TOTAL = 1;*/
        long totalMsgs = 1000;//1 1000
        QUEUE_TOTAL = 8;
        MessageBody = StoreMessage.getBytes();
        for (long i = 0; i < totalMsgs; i++) {
            messageStore.putMessage(buildMessage());
        }

        Thread.sleep(2000L);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        for (long i = 0; i < totalMsgs; i++) {
            GetMessageResult result = messageStore.getMessage("GROUP_A", "TOPIC_A", 0, i, 1024 * 1024, null);
            assertThat(result).isNotNull();
            result.release();
        }
        verifyThatMasterIsFunctional(totalMsgs, messageStore);
    }

    @Test
    public void should_look_message_successfully_when_offset_is_first() {
        final int totalCount = 10;
        int queueId = new Random().nextInt(10);
        String topic = "FooBar";
        int firstOffset = 0;
        AppendMessageResult[] appendMessageResultArray = putMessages(totalCount, topic, queueId);
        AppendMessageResult firstResult = appendMessageResultArray[0];

        MessageExt messageExt = messageStore.lookMessageByOffset(firstResult.getWroteOffset());
        MessageExt messageExt1 = getDefaultMessageStore().lookMessageByOffset(firstResult.getWroteOffset(), firstResult.getWroteBytes());

        assertThat(new String(messageExt.getBody())).isEqualTo(buildMessageBodyByOffset(StoreMessage, firstOffset));
        assertThat(new String(messageExt1.getBody())).isEqualTo(buildMessageBodyByOffset(StoreMessage, firstOffset));
    }

    @Test
    public void should_look_message_successfully_when_offset_is_last() {
        final int totalCount = 10;
        int queueId = new Random().nextInt(10);
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResultArray = putMessages(totalCount, topic, queueId);
        int lastIndex = totalCount - 1;
        AppendMessageResult lastResult = appendMessageResultArray[lastIndex];

        MessageExt messageExt = getDefaultMessageStore().lookMessageByOffset(lastResult.getWroteOffset(), lastResult.getWroteBytes());

        assertThat(new String(messageExt.getBody())).isEqualTo(buildMessageBodyByOffset(StoreMessage, lastIndex));
    }

    @Test
    public void should_look_message_failed_and_return_null_when_offset_is_out_of_bound() {
        final int totalCount = 10;
        int queueId = new Random().nextInt(10);
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResultArray = putMessages(totalCount, topic, queueId);
        long lastOffset = getMaxOffset(appendMessageResultArray);

        MessageExt messageExt = getDefaultMessageStore().lookMessageByOffset(lastOffset);

        assertThat(messageExt).isNull();
    }

    @Test
    public void should_get_consume_queue_offset_successfully_when_incomming_by_timestamp() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResults = putMessages(totalCount, topic, queueId, true);
        //Thread.sleep(10);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        ConsumeQueue consumeQueue = getDefaultMessageStore().findConsumeQueue(topic, queueId);
        for (AppendMessageResult appendMessageResult : appendMessageResults) {
            long offset = messageStore.getOffsetInQueueByTime(topic, queueId, appendMessageResult.getStoreTimestamp());
            SelectMappedBufferResult indexBuffer = consumeQueue.getIndexBuffer(offset);
            assertThat(indexBuffer.getByteBuffer().getLong()).isEqualTo(appendMessageResult.getWroteOffset());
            assertThat(indexBuffer.getByteBuffer().getInt()).isEqualTo(appendMessageResult.getWroteBytes());
            indexBuffer.release();
        }
    }

    @Test
    public void should_get_consume_queue_offset_successfully_when_timestamp_is_skewing() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResults = putMessages(totalCount, topic, queueId, true);
        //Thread.sleep(10);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        int skewing = 2;

        ConsumeQueue consumeQueue = getDefaultMessageStore().findConsumeQueue(topic, queueId);
        for (AppendMessageResult appendMessageResult : appendMessageResults) {
            long offset = messageStore.getOffsetInQueueByTime(topic, queueId, appendMessageResult.getStoreTimestamp() + skewing);
            long offset2 = messageStore.getOffsetInQueueByTime(topic, queueId, appendMessageResult.getStoreTimestamp() - skewing);
            SelectMappedBufferResult indexBuffer = consumeQueue.getIndexBuffer(offset);
            SelectMappedBufferResult indexBuffer2 = consumeQueue.getIndexBuffer(offset2);
            assertThat(indexBuffer.getByteBuffer().getLong()).isEqualTo(appendMessageResult.getWroteOffset());
            assertThat(indexBuffer.getByteBuffer().getInt()).isEqualTo(appendMessageResult.getWroteBytes());
            assertThat(indexBuffer2.getByteBuffer().getLong()).isEqualTo(appendMessageResult.getWroteOffset());
            assertThat(indexBuffer2.getByteBuffer().getInt()).isEqualTo(appendMessageResult.getWroteBytes());
            indexBuffer.release();
            indexBuffer2.release();
        }
    }

    @Test
    public void should_get_min_of_max_consume_queue_offset_when_timestamp_s_skewing_is_large() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResults = putMessages(totalCount, topic, queueId, true);
        //Thread.sleep(10);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        int skewing = 20000;

        ConsumeQueue consumeQueue = getDefaultMessageStore().findConsumeQueue(topic, queueId);
        for (AppendMessageResult appendMessageResult : appendMessageResults) {
            long offset = messageStore.getOffsetInQueueByTime(topic, queueId, appendMessageResult.getStoreTimestamp() + skewing);
            long offset2 = messageStore.getOffsetInQueueByTime(topic, queueId, appendMessageResult.getStoreTimestamp() - skewing);
            SelectMappedBufferResult indexBuffer = consumeQueue.getIndexBuffer(offset);
            SelectMappedBufferResult indexBuffer2 = consumeQueue.getIndexBuffer(offset2);
            assertThat(indexBuffer.getByteBuffer().getLong()).isEqualTo(appendMessageResults[totalCount - 1].getWroteOffset());
            assertThat(indexBuffer.getByteBuffer().getInt()).isEqualTo(appendMessageResults[totalCount - 1].getWroteBytes());
            assertThat(indexBuffer2.getByteBuffer().getLong()).isEqualTo(appendMessageResults[0].getWroteOffset());
            assertThat(indexBuffer2.getByteBuffer().getInt()).isEqualTo(appendMessageResults[0].getWroteBytes());

            indexBuffer.release();
            indexBuffer2.release();
        }
    }

    @Test
    public void should_return_zero_when_consume_queue_not_found() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        int wrongQueueId = 1;
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResults = putMessages(totalCount, topic, queueId, false);
        //Thread.sleep(10);

        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        long offset = messageStore.getOffsetInQueueByTime(topic, wrongQueueId, appendMessageResults[0].getStoreTimestamp());

        assertThat(offset).isEqualTo(0);
    }

    @Test
    public void should_return_negative_one_when_invoke_getMessageStoreTimeStamp_if_consume_queue_not_found() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        int wrongQueueId = 1;
        String topic = "FooBar";
        putMessages(totalCount, topic, queueId, false);
        //Thread.sleep(10);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        long messageStoreTimeStamp = messageStore.getMessageStoreTimeStamp(topic, wrongQueueId, 0);

        assertThat(messageStoreTimeStamp).isEqualTo(-1);
    }

    @Test
    public void should_return_negative_one_when_invoke_getMessageStoreTimeStamp_if_consumeQueueOffset_not_exist() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        int wrongQueueId = 1;
        String topic = "FooBar";
        putMessages(totalCount, topic, queueId, true);
        //Thread.sleep(10);

        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        long messageStoreTimeStamp = messageStore.getMessageStoreTimeStamp(topic, wrongQueueId, -1);

        assertThat(messageStoreTimeStamp).isEqualTo(-1);
    }


    @Test
    public void should_get_message_store_timestamp_successfully_when_incomming_by_topic_queueId_and_consumeQueueOffset() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResults = putMessages(totalCount, topic, queueId, false);
        //Thread.sleep(10);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        ConsumeQueue consumeQueue = getDefaultMessageStore().findConsumeQueue(topic, queueId);
        int minOffsetInQueue = (int)consumeQueue.getMinOffsetInQueue();
        for (int i = minOffsetInQueue; i < consumeQueue.getMaxOffsetInQueue(); i++) {
            long messageStoreTimeStamp = messageStore.getMessageStoreTimeStamp(topic, queueId, i);
            assertThat(messageStoreTimeStamp).isEqualTo(appendMessageResults[i].getStoreTimestamp());
        }
    }

    @Test
    public void should_return_negative_one_when_invoke_getStoreTime_if_incomming_param_is_null() {
        long storeTime = getStoreTime(null);

        assertThat(storeTime).isEqualTo(-1);
    }

    @Test
    public void should_get_store_time_successfully_when_invoke_getStoreTime_if_everything_is_ok() throws InterruptedException {
        final int totalCount = 10;
        int queueId = 0;
        String topic = "FooBar";
        AppendMessageResult[] appendMessageResults = putMessages(totalCount, topic, queueId, false);
        //Thread.sleep(10);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        ConsumeQueue consumeQueue = messageStore.getConsumeQueue(topic, queueId);

        for (int i = 0; i < totalCount; i++) {
            SelectMappedBufferResult indexBuffer = consumeQueue.getIndexBuffer(i);
            long storeTime = getStoreTime(indexBuffer);
            assertThat(storeTime).isEqualTo(appendMessageResults[i].getStoreTimestamp());
            indexBuffer.release();
        }
    }

    @Test
    public void should_return_negative_one_when_invoke_getStoreTime_if_phyOffset_is_less_than_commitLog_s_minOffset() {
        long phyOffset = -10;
        int size = 138;
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.putLong(phyOffset);
        byteBuffer.putInt(size);
        byteBuffer.flip();
        MappedFile mappedFile = mock(MappedFile.class);
        SelectMappedBufferResult result = new SelectMappedBufferResult(0, byteBuffer, size, mappedFile);

        long storeTime = getStoreTime(result);
        result.release();

        assertThat(storeTime).isEqualTo(-1);
    }

    private DefaultMessageStore getDefaultMessageStore() {
        return (DefaultMessageStore)this.messageStore;
    }

    private AppendMessageResult[] putMessages(int totalCount, String topic, int queueId) {
        return putMessages(totalCount, topic, queueId, false);
    }

    private AppendMessageResult[] putMessages(int totalCount, String topic, int queueId, boolean interval) {
        AppendMessageResult[] appendMessageResultArray = new AppendMessageResult[totalCount];
        for (int i = 0; i < totalCount; i++) {
            String messageBody = buildMessageBodyByOffset(StoreMessage, i);
            MessageExtBrokerInner msgInner = buildMessage(messageBody.getBytes(), topic);
            msgInner.setQueueId(queueId);
            PutMessageResult result = messageStore.putMessage(msgInner);
            appendMessageResultArray[i] = result.getAppendMessageResult();
            assertThat(result.getPutMessageStatus()).isEqualTo(PutMessageStatus.PUT_OK);
            if (interval) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw  new RuntimeException("Thread sleep ERROR");
                }
            }
        }
        return appendMessageResultArray;
    }

    private long getMaxOffset(AppendMessageResult[] appendMessageResultArray) {
        if (appendMessageResultArray == null) {
            return 0;
        }
        AppendMessageResult last = appendMessageResultArray[appendMessageResultArray.length - 1];
        return last.getWroteOffset() + last.getWroteBytes();
    }

    private String buildMessageBodyByOffset(String message, long i) {
        return String.format("%s offset %d", message, i);
    }

    private long getStoreTime(SelectMappedBufferResult result) {
        try {
            Method getStoreTime = getDefaultMessageStore().getClass().getDeclaredMethod("getStoreTime", SelectMappedBufferResult.class);
            getStoreTime.setAccessible(true);
            return (long)getStoreTime.invoke(getDefaultMessageStore(), result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建消息
     * @param messageBody
     * @param topic
     * @return
     */
    private MessageExtBrokerInner buildMessage(byte[] messageBody, String topic) {
        MessageExtBrokerInner msg = new MessageExtBrokerInner();
        msg.setTopic(topic);
        msg.setTags("TAG1");
        msg.setKeys("Hello");
        msg.setBody(messageBody);
        msg.setKeys(String.valueOf(System.currentTimeMillis()));
        msg.setQueueId(Math.abs(QueueId.getAndIncrement()) % QUEUE_TOTAL);
        msg.setSysFlag(0);
        msg.setBornTimestamp(System.currentTimeMillis());
        msg.setStoreHost(StoreHost);
        msg.setBornHost(BornHost);

//        BornHost/**/int keyIndex = KeyMock.getA
        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_KEYS,Integer.toString(0));
        //只有设置了此。properties才生效
        msg.setPropertiesString(MessageDecoder.messageProperties2String(msg.getProperties()));
        return msg;
    }

    private MessageExtBrokerInner  buildMessage() {
        return buildMessage(MessageBody, "FooBar");
    }

    private void verifyThatMasterIsFunctional(long totalMsgs, MessageStore master) {
        for (long i = 0; i < totalMsgs; i++) {
            master.putMessage(buildMessage());
        }

        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        for (long i = 0; i < totalMsgs; i++) {
            GetMessageResult result = master.getMessage("GROUP_A", "TOPIC_A", 0, i, 1024 * 1024, null);
            assertThat(result).isNotNull();
            result.release();

        }
    }

    @Test
    public void testPullSize() throws Exception {
        String topic = "pullSizeTopic";

        for (int i = 0; i < 32; i++) {
            MessageExtBrokerInner messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }
        // wait for consume queue build
        // the sleep time should be great than consume queue flush interval
        //Thread.sleep(100);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        String group = "simple";
        GetMessageResult getMessageResult32 = messageStore.getMessage(group, topic, 0, 0, 32, null);
        assertThat(getMessageResult32.getMessageBufferList().size()).isEqualTo(32);
        getMessageResult32.release();

        GetMessageResult getMessageResult20 = messageStore.getMessage(group, topic, 0, 0, 20, null);
        assertThat(getMessageResult20.getMessageBufferList().size()).isEqualTo(20);

        getMessageResult20.release();
        GetMessageResult getMessageResult45 = messageStore.getMessage(group, topic, 0, 0, 10, null);
        assertThat(getMessageResult45.getMessageBufferList().size()).isEqualTo(10);
        getMessageResult45.release();

    }

    @Test
    public void testRecoverBack() throws Exception {
        String topic = "recoverTopic";
        MessageBody = StoreMessage.getBytes();
        for (int i = 0; i < 100; i++) {
            MessageExtBrokerInner messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }

        // Thread.sleep(100);//wait for build consumer queue
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        long maxPhyOffset = messageStore.getMaxPhyOffset();
        long maxCqOffset = messageStore.getMaxOffsetInQueue(topic, 0);

        //1.just reboot
        messageStore.shutdown();
        messageStore = buildMessageStore();
        boolean load = messageStore.load();
        assertTrue(load);
        messageStore.start();
        assertTrue(maxPhyOffset == messageStore.getMaxPhyOffset());
        assertTrue(maxCqOffset == messageStore.getMaxOffsetInQueue(topic, 0));

        //2.damage commitlog and reboot normal
        for (int i = 0; i < 100; i++) {
            MessageExtBrokerInner messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }
        //Thread.sleep(100);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        long secondLastPhyOffset = messageStore.getMaxPhyOffset();
        long secondLastCqOffset = messageStore.getMaxOffsetInQueue(topic, 0);

        MessageExtBrokerInner messageExtBrokerInner = buildMessage();
        messageExtBrokerInner.setTopic(topic);
        messageExtBrokerInner.setQueueId(0);
        messageStore.putMessage(messageExtBrokerInner);

        messageStore.shutdown();

        //damage last message
        damageCommitlog(secondLastPhyOffset);

        //reboot
        messageStore = buildMessageStore();
        load = messageStore.load();
        assertTrue(load);
        messageStore.start();
        assertTrue(secondLastPhyOffset == messageStore.getMaxPhyOffset());
        assertTrue(secondLastCqOffset == messageStore.getMaxOffsetInQueue(topic, 0));

        //3.damage commitlog and reboot abnormal
        for (int i = 0; i < 100; i++) {
            messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }
        //Thread.sleep(100);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        secondLastPhyOffset = messageStore.getMaxPhyOffset();
        secondLastCqOffset = messageStore.getMaxOffsetInQueue(topic, 0);

        messageExtBrokerInner = buildMessage();
        messageExtBrokerInner.setTopic(topic);
        messageExtBrokerInner.setQueueId(0);
        messageStore.putMessage(messageExtBrokerInner);
        messageStore.shutdown();

        //damage last message
        damageCommitlog(secondLastPhyOffset);
        //add abort file
        String fileName = StorePathConfigHelper.getAbortFile(((DefaultMessageStore) messageStore).getMessageStoreConfig().getStorePathRootDir());
        File file = new File(fileName);
        MappedFile.ensureDirOK(file.getParent());
        file.createNewFile();

        messageStore = buildMessageStore();
        load = messageStore.load();
        assertTrue(load);
        messageStore.start();
        assertTrue(secondLastPhyOffset == messageStore.getMaxPhyOffset());
        assertTrue(secondLastCqOffset == messageStore.getMaxOffsetInQueue(topic, 0));

        //message write again
        for (int i = 0; i < 100; i++) {
            messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }
    }
    /**
     * 测试消息恢复
     * @throws Exception
     */
    @Test
    public void testRecover() throws Exception {
        String topic = "recoverTopic";
        MessageBody = StoreMessage.getBytes();
        for (int i = 0; i < 100; i++) {
            MessageExtBrokerInner messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageExtBrokerInner.setWaitStoreMsgOK(false);
            messageStore.putMessage(messageExtBrokerInner);
        }

       // Thread.sleep(100);//wait for build consumer queue
        //等待cosume 和 index构建完成
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);

        long maxPhyOffset = messageStore.getMaxPhyOffset();
        long maxCqOffset = messageStore.getMaxOffsetInQueue(topic, 0);

        //1.just reboot
        /**
         *
         */
        messageStore.shutdown();
        messageStore = buildMessageStore();
        //正常恢复
        boolean load = messageStore.load();
        assertTrue(load);
        messageStore.start();
        assertTrue(maxPhyOffset == messageStore.getMaxPhyOffset());
        assertTrue(maxCqOffset == messageStore.getMaxOffsetInQueue(topic, 0));

        //2.damage commitlog and reboot normal  又写入
        for (int i = 0; i < 100; i++) {
            MessageExtBrokerInner messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }
        //Thread.sleep(100);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        long secondLastPhyOffset = messageStore.getMaxPhyOffset();
        long secondLastCqOffset = messageStore.getMaxOffsetInQueue(topic, 0);

        MessageExtBrokerInner messageExtBrokerInner = buildMessage();
        messageExtBrokerInner.setTopic(topic);
        messageExtBrokerInner.setQueueId(0);
        messageExtBrokerInner.setWaitStoreMsgOK(false);
        messageStore.putMessage(messageExtBrokerInner);

        Thread.sleep(1000);
        //
      /*  messageStore.shutdown();

        //damage last message  破坏最后一个消息
        damageCommitlog(secondLastPhyOffset);

        //reboot
        messageStore = buildMessageStore();

        load = messageStore.load();
        assertTrue(load);
        messageStore.start();
        assertTrue(secondLastPhyOffset == messageStore.getMaxPhyOffset());
        assertTrue(secondLastCqOffset == messageStore.getMaxOffsetInQueue(topic, 0));

        //3.damage commitlog and reboot abnormal
        for (int i = 0; i < 100; i++) {
            messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }
        //Thread.sleep(100);
        StoreTestUtil.waitCommitLogReput((DefaultMessageStore) messageStore);
        secondLastPhyOffset = messageStore.getMaxPhyOffset();
        secondLastCqOffset = messageStore.getMaxOffsetInQueue(topic, 0);

        messageExtBrokerInner = buildMessage();
        messageExtBrokerInner.setTopic(topic);
        messageExtBrokerInner.setQueueId(0);
        messageStore.putMessage(messageExtBrokerInner);
        messageStore.shutdown();*/
        long LastPhyOffset = messageStore.getMaxPhyOffset();
        long LastCqOffset = messageStore.getMaxOffsetInQueue(topic, 0);
        //damage last message
        damageCommitlog(secondLastPhyOffset);

        messageStore.shutdown();
        //add abort file
        String fileName = StorePathConfigHelper.getAbortFile(((DefaultMessageStore) messageStore).getMessageStoreConfig().getStorePathRootDir());
        File file = new File(fileName);
        MappedFile.ensureDirOK(file.getParent());
        file.createNewFile();

        messageStore = buildMessageStore();
        //真正恢复的地方
        load = messageStore.load();
        assertTrue(load);
        messageStore.start();
        assertTrue(secondLastPhyOffset == messageStore.getMaxPhyOffset());
        assertTrue(secondLastCqOffset == messageStore.getMaxOffsetInQueue(topic, 0));

        //message write again
        for (int i = 0; i < 100; i++) {
            messageExtBrokerInner = buildMessage();
            messageExtBrokerInner.setTopic(topic);
            messageExtBrokerInner.setQueueId(0);
            messageStore.putMessage(messageExtBrokerInner);
        }
    }

    private void damageCommitlog(long offset) throws Exception {
        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
        File file = new File(messageStoreConfig.getStorePathCommitLog() + File.separator + "00000000000000000000");

        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024 * 10);

        int bodyLen = mappedByteBuffer.getInt((int) offset + 84);
        int topicLenIndex = (int) offset + 84 + bodyLen + 4;
        mappedByteBuffer.position(topicLenIndex);
        mappedByteBuffer.putInt(0);
        mappedByteBuffer.putInt(0);
        mappedByteBuffer.putInt(0);
        mappedByteBuffer.putInt(0);

        mappedByteBuffer.force();
        fileChannel.force(true);
        fileChannel.close();
    }

    private class MyMessageArrivingListener implements MessageArrivingListener {
        @Override
        public void arriving(String topic, int queueId, long logicOffset, long tagsCode, long msgStoreTime,
                             byte[] filterBitMap, Map<String, String> properties) {
        }
    }
}
