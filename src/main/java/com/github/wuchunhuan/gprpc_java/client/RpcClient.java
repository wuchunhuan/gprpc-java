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

import com.github.wuchunhuan.gprpc_java.protocol.RpcRequestEncoder;
import com.github.wuchunhuan.gprpc_java.protocol.RpcResponseDecoder;
import com.github.wuchunhuan.gprpc_java.utils.CustomThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RpcClient {
    private static final Logger LOG = LoggerFactory.getLogger(RpcClient.class);
    private Bootstrap bootstrap;
    private ScheduledExecutorService timeoutTimer;
    private ConcurrentMap<Integer, RpcFuture> pendingRpc;

    public RpcClient() {

    }

    public void init(int threadNum) {
        pendingRpc = new ConcurrentHashMap<Integer, RpcFuture>();
        timeoutTimer = Executors.newScheduledThreadPool(1, new CustomThreadFactory("Timeout-thread"));

        EventLoopGroup group = new NioEventLoopGroup(
                threadNum,
                new CustomThreadFactory("client-io-thread"));

        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcRequestEncoder());
                ch.pipeline().addLast(new RpcResponseDecoder());
                ch.pipeline().addLast(new RpcClientHandler(RpcClient.this));
            }
        };

        bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.group(group).channel(NioSocketChannel.class).handler(initializer);
    }

    public void stop() {
        if (bootstrap.config().group() != null) {
            bootstrap.config().group().shutdownGracefully();
        }
        if (timeoutTimer != null) {
            timeoutTimer.shutdown();
        }
    }

    public void addRpcFuture(int callId, RpcFuture future) {
        pendingRpc.put(callId, future);
    }

    public RpcFuture getRpcFuture(int callId) {
        return pendingRpc.get(callId);
    }

    public RpcFuture removeRpcFuture(int callId) {
        return pendingRpc.remove(callId);
    }

    public ScheduledExecutorService getTimeoutTimer() {
        return timeoutTimer;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }
}
