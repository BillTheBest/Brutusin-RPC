/*
 * Copyright 2015 Ignacio del Valle Alles idelvall@brutusin.org.
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
package org.brutusin.rpc;

import java.net.URL;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class ComponentItem {

    private String id;
    private boolean active;
    private String description;
    private boolean dynamicInputSchema;
    private URL sourceCode;

    public final boolean isDynamicInputSchema() {
        return dynamicInputSchema;
    }

    public final void setDynamicInputSchema(boolean dynamicInputSchema) {
        this.dynamicInputSchema = dynamicInputSchema;
    }

    public URL getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(URL sourceCode) {
        this.sourceCode = sourceCode;
    }

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
