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

import com.github.wuchunhuan.gprpc_java.utils.ErrInfo;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class ClientRpcController implements RpcController {

    public enum ProcessEnd {
        RPC_CLIENT(0),RPC_SERVER(1),NULL_END(2);

        private int value = 0;
        ProcessEnd(int value) {
            this.value = value;
        }

        public static ProcessEnd valueOf(int value) {
            switch (value) {
                case 0:
                    return RPC_CLIENT;
                case 1:
                    return RPC_SERVER;
                default:
                    return NULL_END;
            }
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum MsgType {
        REQUEST(0),RESPONSE(1),NULL_TYPE(2);

        private int value = 0;
        MsgType(int value) {
            this.value = value;
        }

        public static MsgType valueOf(int value) {
            switch (value) {
                case 0:
                    return REQUEST;
                case 1:
                    return RESPONSE;
                default:
                    return NULL_TYPE;
            }
        }

        public int getValue() {
            return this.value;
        }
    }

    private ProcessEnd processEnd;
    private MsgType msgType;
    private boolean failed;
    private int errCode;
    private String failedReason;
    private String failedDetail;
    private String remoteAddress;
    private int callId;
    private String callTag;
    private boolean compress;
    private boolean encrypt;
    private int timeout;


    public ClientRpcController() {
        reset();
    }

    public void reset() {
        processEnd = ProcessEnd.NULL_END;
        msgType = MsgType.NULL_TYPE;
        failed = false;
        failedReason = "";
        failedDetail = "";
        errCode = 0;
        callId = -1;
        compress = false;
        encrypt = false;
        timeout = 0;
    }

    public boolean failed() {
        return failed;
    }

    public void setFailed(String reason) {
        failed = true;
        failedReason = reason;
    }

    public String errorText() {
        return failedReason;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public void setFailed(int errCode) {
        setErrCode(errCode);
        setFailed(ErrInfo.getErrStr(errCode));
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getCallTag() {
        return callTag;
    }

    public void setCallTag(String callTag) {
        this.callTag = callTag;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public ProcessEnd getProcessEnd() {
        return processEnd;
    }

    public void setProcessEnd(ProcessEnd processEnd) {
        this.processEnd = processEnd;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void startCancel() {

    }

    public boolean isCanceled() {
        return false;
    }

    public void notifyOnCancel(RpcCallback<Object> callback) {

    }
}
