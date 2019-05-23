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

package com.github.wuchunhuan.gprpc_java.protocol;

import com.github.wuchunhuan.gprpc_java.client.ClientRpcController;
import com.github.wuchunhuan.gprpc_java.client.RpcClient;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface ProtocolProcessor {
    void encodeRequest(ChannelHandlerContext ctx, Object object, List<Object> out) throws Exception;

    void decodeResponse(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;

    void processResponse(RpcClient rpcClient, Object msg) throws Exception;

    Gprpc.rpc_data newRequest(Descriptors.MethodDescriptor method, ClientRpcController controller, Message request);
}
