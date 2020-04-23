/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common.filter;

import java.net.URL;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;

public class FilterAPI {
    public static URL classFile(final String className) {
        final String javaSource = simpleClassName(className) + ".java";
        URL url = FilterAPI.class.getClassLoader().getResource(javaSource);
        return url;
    }

    public static String simpleClassName(final String className) {
        String simple = className;
        int index = className.lastIndexOf(".");
        if (index >= 0) {
            simple = className.substring(index + 1);
        }

        return simple;
    }

    public static SubscriptionData buildSubscriptionData(final String consumerGroup, String topic,
        String subString) throws Exception {
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setTopic(topic);
        subscriptionData.setSubString(subString);

        if (null == subString || subString.equals(SubscriptionData.SUB_ALL) || subString.length() == 0) {
            subscriptionData.setSubString(SubscriptionData.SUB_ALL);
        } else {
            /**
             * TODO
             * 发送的消息都带有tags
             * 可用用|进行分割
             * 会把tag 放入tagset中
             * 然后针对每个tag计算一个hash值，放入 codeset
             * 在broker端，使用codeset进行消息过滤，因为存在hash冲突，
             * broker过滤的消息，可能有部分不是消费端想要的，这时在消费端通过tagset再次进行一次过滤
             */
            String[] tags = subString.split("\\|\\|");
            if (tags.length > 0) {
                for (String tag : tags) {
                    if (tag.length() > 0) {
                        String trimString = tag.trim();
                        if (trimString.length() > 0) {
                            subscriptionData.getTagsSet().add(trimString);
                            subscriptionData.getCodeSet().add(trimString.hashCode());
                        }
                    }
                }
            } else {
                throw new Exception("subString split error");
            }
        }

        return subscriptionData;
    }

    public static SubscriptionData build(final String topic, final String subString,
        final String type) throws Exception {
        if (ExpressionType.TAG.equals(type) || type == null) {
            return buildSubscriptionData(null, topic, subString);
        }

        if (subString == null || subString.length() < 1) {
            throw new IllegalArgumentException("Expression can't be null! " + type);
        }

        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.setTopic(topic);
        subscriptionData.setSubString(subString);
        subscriptionData.setExpressionType(type);

        return subscriptionData;
    }
}
