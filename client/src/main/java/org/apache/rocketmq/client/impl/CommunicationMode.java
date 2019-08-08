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
package org.apache.rocketmq.client.impl;

public enum CommunicationMode {
    /**
     * 生产者发送消息，同步等待直到服务器返回响应结果
     */
    SYNC,
    /**
     * 生产者发送消息，指定一个回调函数，然后不管是否响应，则立即返回。响应回来后则执行对应的回调函数。在一个新的线程中执行
     */
    ASYNC,
    /**
     *单向。生产者发送消息，没有回调，不管成功失败，直接返回
     */
    ONEWAY,
}
