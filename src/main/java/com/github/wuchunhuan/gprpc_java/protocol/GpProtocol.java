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
import com.github.wuchunhuan.gprpc_java.client.RpcFuture;
import com.github.wuchunhuan.gprpc_java.utils.IdGenerator;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GpProtocol implements ProtocolProcessor{
    private static final Logger LOG = LoggerFactory.getLogger(GpProtocol.class);
    private static final int HEADER_LEN = 8;
    private static final byte[] magic = {'_', 'R', 'P', 'C'};
    private static GpProtocol ourInstance = new GpProtocol();

    public static GpProtocol getInstance() {
        return ourInstance;
    }

    private GpProtocol() {
    }

    public void encodeRequest(ChannelHandlerContext ctx, Object object, List<Object> out) throws Exception {
        Gprpc.rpc_data pData = (Gprpc.rpc_data) object;
        ByteBuf header = Unpooled.buffer(HEADER_LEN);
        byte[] dataArr = pData.toByteArray();
        header.writeBytes(magic);
        header.writeInt(dataArr.length);
//        header.writeIntLE(dataArr.length);

        ByteBuf outBuf = Unpooled.wrappedBuffer(header.array(), dataArr);
        out.add(outBuf);
    }

    public void decodeResponse(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEADER_LEN) {
            return;
        }
        in.markReaderIndex();
        byte[] magicBuff = new byte[4];
        in.readBytes(magicBuff, 0, 4);
        int dataLen = in.readInt();
//        int dataLen = in.readIntLE();
        if (in.readableBytes() < dataLen) {
            in.resetReaderIndex();
            return;
        }

        in.markReaderIndex();
        byte[] data = new byte[dataLen];
        in.readBytes(data, 0, dataLen);

        Gprpc.rpc_data pData = Gprpc.rpc_data.parseFrom(data);
        out.add(pData);
    }

    public void processResponse(RpcClient rpcClient, Object msg) throws Exception {
        Gprpc.rpc_data pData = (Gprpc.rpc_data) msg;
        int callId = pData.getCallId();
        RpcFuture rpcFuture = rpcClient.getRpcFuture(callId);
        if (rpcFuture == null) {
            return;
        }
        rpcClient.removeRpcFuture(callId);
        ClientRpcController clientRpcController = rpcFuture.getClientRpcController();
        clientRpcController.setMsgType(ClientRpcController.MsgType.RESPONSE);
        clientRpcController.setProcessEnd(ClientRpcController.ProcessEnd.RPC_CLIENT);
        if (pData.getFailed()) {
            clientRpcController.setFailed(pData.getReason());
            clientRpcController.setErrCode(pData.getErrorCode());
            rpcFuture.run(null);
        }
        clientRpcController.setEncrypt(pData.getEncrypt());
        clientRpcController.setCompress(pData.getCompress());
        //ignore compress and encrypt options for now
//        Method decodeMethod = rpcFuture.getResponseClass().getMethod("parseFrom", byte[].class);
//        Message response = (Message)decodeMethod.invoke(null, pData.getContent());
        Message response = rpcFuture.getResponsePrototype().getParserForType().parseFrom(pData.getContent());
        rpcFuture.run(response);
    }

    public Gprpc.rpc_data newRequest(Descriptors.MethodDescriptor method, ClientRpcController controller, Message request) {
        final int callId = IdGenerator.getInstance().getId();
        controller.setCallId(callId);
        controller.setMsgType(ClientRpcController.MsgType.REQUEST);
        controller.setProcessEnd(ClientRpcController.ProcessEnd.RPC_CLIENT);

        controller.setCallTag(method.getService().getFullName() + "." + method.getName() + "." + callId);
        //ignore compress and encrypt options for now
        Gprpc.rpc_data data;
        data = Gprpc.rpc_data.newBuilder().setDataType(Gprpc.rpc_data.type.REQUEST)
                .setSvcName(method.getService().getFullName())
                .setMethodName(method.getName())
                .setCallId(callId)
                .setCompress(controller.isCompress())
                .setEncrypt(controller.isEncrypt())
                .setContent(request.toByteString()).build();
        return data;
    }
}
