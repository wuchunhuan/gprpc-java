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

import com.github.wuchunhuan.gprpc_java.protocol.GpProtocol;
import com.github.wuchunhuan.gprpc_java.protocol.Gprpc;
import com.github.wuchunhuan.gprpc_java.protocol.ProtocolProcessor;
import com.google.protobuf.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class RpcClientChannel implements RpcChannel, BlockingRpcChannel {
    private static Logger LOG = LoggerFactory.getLogger(RpcClientChannel.class);

    private RpcClient rpcClient;
    private String address;
    private int port;

    public RpcClientChannel(RpcClient client, String address, int port) {
        this.rpcClient = client;
        this.address = address;
        this.port = port;
    }
    public Message callBlockingMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, Message responsePrototype) throws ServiceException {
        ClientRpcController clientRpcController = (ClientRpcController) controller;
        Future future = doRpc(method, clientRpcController, request, responsePrototype, null);
        if (future == null) {
            return null;
        }
        try {
            int timeOut = clientRpcController.getTimeout();
            Message response;
            if (timeOut > 0) {
                response = (Message)future.get(timeOut, TimeUnit.MILLISECONDS);
            } else {
                response = (Message)future.get();
            }
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void callMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, Message responsePrototype, RpcCallback<Message> done) {
        ClientRpcController clientRpcController = (ClientRpcController) controller;
        doRpc(method, clientRpcController, request, responsePrototype, done);
    }

    private RpcFuture doRpc(Descriptors.MethodDescriptor method, ClientRpcController controller, Message request, Message responsePrototype, RpcCallback<Message> done) {
        ProtocolProcessor protocolProcessor = GpProtocol.getInstance();
        Gprpc.rpc_data data = protocolProcessor.newRequest(method, controller, request);
        final int callId = controller.getCallId();
        controller.setRemoteAddress(address);

        ChannelFuture connectFuture = rpcClient.getBootstrap().connect(new InetSocketAddress(address, port)).awaitUninterruptibly();
        if ( !connectFuture.isSuccess() ) {
            LOG.warn("Connect to {}:{} failed due to {}",
                    address, port, connectFuture.cause().getMessage());
            controller.setFailed(-3);
//            rpcFuture.run(null);
            if (done != null) {
                done.run(null);
            }
            return null;
        }

        Channel channel;
        channel = connectFuture.channel();

        int timeout = controller.getTimeout();
        ScheduledFuture scheduledFuture = rpcClient.getTimeoutTimer().schedule(new Runnable() {
            public void run() {
                RpcFuture rpcFuture = rpcClient.removeRpcFuture(callId);
                if (rpcFuture != null) {
                    rpcFuture.getClientRpcController().setFailed(-4);
                    rpcFuture.run(null);
                }
            }
        }, timeout, TimeUnit.MILLISECONDS);
//        RpcFuture rpcFuture = new RpcFuture(scheduledFuture, callId, request, responsePrototype.getClass(), done, controller, channel);
//        RpcFuture rpcFuture = new RpcFuture(scheduledFuture, callId, request, method.getOutputType().getClass(), (AsyncRpcCallback<Message>)done, controller, channel);
        RpcFuture<Message> rpcFuture = new RpcFuture<Message>(scheduledFuture, callId, request, responsePrototype, done, controller, channel);
        rpcClient.addRpcFuture(callId, rpcFuture);

        channel.writeAndFlush(data);
        return rpcFuture;
    }
}

