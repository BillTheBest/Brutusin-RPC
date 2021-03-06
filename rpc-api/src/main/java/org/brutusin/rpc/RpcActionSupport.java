/*
 * Copyright 2015 brutusin.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brutusin.rpc;

import org.brutusin.rpc.http.*;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.brutusin.rpc.websocket.Topic;
import org.brutusin.rpc.websocket.WebsocketAction;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public abstract class RpcActionSupport {

    protected static final ThreadLocal<RpcActionSupport> CONTEXTS = new ThreadLocal();

    public static RpcActionSupport getInstance() {
        return CONTEXTS.get();
    }
    
    public abstract ApplicationContext getSpringContext();

    public abstract Map<String, HttpAction> getHttpServices();

    public abstract Map<String, WebsocketAction> getWebSocketServices();

    public abstract Map<String, Topic> getTopics();

    public abstract Principal getUserPrincipal();

    public abstract boolean isUserInRole(String role);

    public abstract Set<String> getUserRoles();

    public abstract HttpSession getHttpSession();

}
