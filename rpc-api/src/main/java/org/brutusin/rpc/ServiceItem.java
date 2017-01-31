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

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class ServiceItem extends ComponentItem {

    private boolean framework;
    private boolean dynamicInputSchema;

    public boolean isFramework() {
        return framework;
    }

    public void setFramework(boolean framework) {
        this.framework = framework;
    }

    public final boolean isDynamicInputSchema() {
        return dynamicInputSchema;
    }

    public final void setDynamicInputSchema(boolean dynamicInputSchema) {
        this.dynamicInputSchema = dynamicInputSchema;
    }
}
