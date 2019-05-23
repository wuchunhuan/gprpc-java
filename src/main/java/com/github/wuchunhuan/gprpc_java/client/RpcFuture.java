/*
 *
 * Copyright © 2019 Patrick Wu(Wu chunhuan)/wuchunhuan@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.wuchunhuan.gprpc_java.client;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

@SuppressWarnings("unchecked")
public class RpcFuture<T> implements Future {
    private static final Logger LOG = LoggerFactory.getLogger(RpcFuture.class);
    private CountDownLatch latch;
    private ScheduledFuture scheduledFuture;
    private int callId;
    private Object request;
//    private Class<T> responseClass;
    private Message responsePrototype;
//    private AsyncRpcCallback<T> callback;
    private RpcCallback callback;
    private ClientRpcController clientRpcController;
    private Object response;
    private Channel channel;
//    private Throwable error;
    private boolean isDone;

    public RpcFuture(ScheduledFuture scheduledFuture,
                     int callId,
                     Object request,
//                     Class<T> responseClass,
                     Message responsePrototype,
                     RpcCallback<T> callback,
//                     AsyncRpcCallback<T> callback,
                     ClientRpcController clientRpcController,
                     Channel channel) {
        this.scheduledFuture = scheduledFuture;
        this.callId = callId;
        this.request = request;
//        this.responseClass = responseClass;
        this.responsePrototype = responsePrototype;
        this.callback = callback;
        this.clientRpcController = clientRpcController;
        this.channel = channel;
        this.latch = new CountDownLatch(1);
    }

    public void run(T response) {
        this.response = response;
        scheduledFuture.cancel(true);
        latch.countDown();
        if (callback != null) {
            callback.run(response);
//            callback.call(clientRpcController, (T)response);
        }
        if (channel.isOpen()) {
            channel.close();
        }
        isDone = true;
    }

    public ClientRpcController getClientRpcController() {
        return clientRpcController;
    }

    /*public Class<T> getResponseClass() {
        return responseClass;
    }*/

    public Message getResponsePrototype() {
        return responsePrototype;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return isDone;
    }

    public T get() throws InterruptedException, ExecutionException {
        latch.await();
        return (T)response;
    }

    public T get(long timeout, TimeUnit unit) {
        try {
            if (latch.await(timeout, unit)) {
                return (T)response;
            } else {
                LOG.warn("Rpc call time out!");
                return null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
