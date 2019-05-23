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

import com.github.wuchunhuan.gprpc_java.protocol.Ping;
import com.google.protobuf.ServiceException;

public class SyncClientTest {

    public static void main(String[] args) {
        RpcClient client = new RpcClient();
        client.init(8);

        Ping.ping_req request = Ping.ping_req.newBuilder().setPing("Hello").build();
//        Ping.pong_rsp response = Ping.pong_rsp.newBuilder().build();
        ClientRpcController controller = new ClientRpcController();
        controller.setTimeout(2000);
        String address = "0.0.0.0";
        int port = 8998;
        RpcClientChannel channel = new RpcClientChannel(client, address, port);

        try {
            Ping.pong_rsp response = Ping.ping_service.newBlockingStub(channel).ping(controller, request);
            if (controller.failed()) {
                System.out.println("Rpc call failed:" + controller.getErrCode() + ", " + controller.errorText());
            } else {
                System.out.println("Rpc call succeed, response: " + response.getPong());
                System.out.println("Remote address: " + controller.getRemoteAddress());
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        } finally {
            client.stop();
        }
    }
}
