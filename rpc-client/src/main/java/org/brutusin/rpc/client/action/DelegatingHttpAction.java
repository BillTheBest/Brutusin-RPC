/*
 * Copyright 2016 Ignacio del Valle Alles idelvall@brutusin.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brutusin.rpc.client.action;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.brutusin.json.ParseException;
import org.brutusin.json.spi.JsonCodec;
import org.brutusin.json.spi.JsonNode;
import org.brutusin.json.spi.JsonSchema;
import org.brutusin.rpc.client.http.HttpEndpoint;
import org.brutusin.rpc.client.http.HttpResponse;
import org.brutusin.rpc.http.Cacheable;
import org.brutusin.rpc.http.HttpAction;
import org.brutusin.rpc.http.HttpServiceItem;
import org.brutusin.rpc.http.StreamResult;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class DelegatingHttpAction extends HttpAction<JsonNode, Object> {

    private final HttpEndpoint endpoint;
    private final String targetId;

    private volatile HttpServiceItem si;
    private volatile JsonSchema inputSchema;
    private volatile JsonSchema outputSchema;
    private volatile boolean loaded;

    public DelegatingHttpAction(HttpEndpoint endpoint, String targetId) {
        this.endpoint = endpoint;
        this.targetId = targetId;
    }

    @Override
    public boolean isActive() {
        if (!loaded) {
            synchronized (this) {
                if (!loaded) {
                    try {
                        getServiceItem();
                        loaded = true;
                    } catch (Exception e) {
                        loaded = false;
                    }
                }
            }
        }
        return loaded;
    }

    @Override
    public Object execute(JsonNode input) throws Exception {
        HttpResponse httpResponse = this.endpoint.exec(targetId, input, null);
        if (httpResponse.isIsBinary()) {
            StreamResult sr = new StreamResult(httpResponse.getInputStream());
            if (si.isSafe()) {
                return new Cacheable(sr, httpResponse.getCachingInfo());
            } else {
                return sr;
            }
        } else {
            if (si.isSafe()) {
            return new Cacheable(httpResponse.getRpcResponse(), httpResponse.getCachingInfo()); } else {
                return httpResponse.getRpcResponse();
            }
        }
    }

    @Override
    public JsonSchema getInputSchema() {
        if (inputSchema == null) {
            synchronized (this) {
                if (inputSchema == null) {
                    try {
                        inputSchema = querySchema(true);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        return inputSchema;
    }

    @Override
    public Map<String, JsonSchema> getDynamicInputSchemas(String[] fieldNames, JsonNode data) {
        try {
            HttpResponse exec = endpoint.exec("rpc.http.schema-provider", JsonCodec.getInstance().parse(
                    "{\"id\":\"" + targetId + "\","
                    + "\"fieldNames\":" + JsonCodec.getInstance().transform(fieldNames) + ","
                    + "\"input\":" + JsonCodec.getInstance().transform(data) + "}"), null);
            JsonNode result = exec.getRpcResponse().getResult();
            Map<String, JsonSchema> ret = new HashMap<String, JsonSchema>();
            Iterator<String> properties = result.getProperties();
            while (properties.hasNext()) {
                String prop = properties.next();
                ret.put(prop, JsonCodec.getInstance().parseSchema(result.get(prop).toString()));
            }
            return ret;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public JsonSchema getOutputSchema() {
        if (outputSchema == null) {
            synchronized (this) {
                if (outputSchema == null) {
                    try {
                        outputSchema = querySchema(false);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        return outputSchema;
    }

    @Override
    public boolean isSafe() {
        return getServiceItem().isSafe();
    }

    @Override
    public boolean isIdempotent() {
        return getServiceItem().isIdempotent();
    }

    @Override
    public String getDescription() {
        return getServiceItem().getDescription();
    }

    @Override
    public URL getSourceCode() {
        return getServiceItem().getSourceCode();
    }

    @Override
    public boolean isBinaryResponse() {
        return getServiceItem().isBinaryResponse();
    }

    @Override
    public boolean isUpload() {
        return getServiceItem().isUpload();
    }

    private HttpServiceItem getServiceItem() {
        if (si == null) {
            synchronized (this) {
                if (si == null) {
                    try {
                        si = endpoint.getServices().get(targetId);
                        if (si == null) {
                            throw new Error("Service '" + targetId + "' not found in " + endpoint.getEndpoint());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        return si;
    }

    private JsonSchema querySchema(boolean input) throws IOException {
        try {
            HttpResponse exec = endpoint.exec("rpc.http.schema", JsonCodec.getInstance().parse("{\"mode\":\"" + (input ? "I" : "O") + "\",\"id\":\"" + targetId + "\"}"), null);
            JsonNode result = exec.getRpcResponse().getResult();
            return JsonCodec.getInstance().parseSchema(result.toString());
        } catch (ParseException ex) {
            throw new Error(ex);
        }
    }

}
