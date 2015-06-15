/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.btm.client.manager.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawkular.btm.api.model.admin.InstrumentAction;
import org.hawkular.btm.api.model.admin.InstrumentRule;
import org.hawkular.btm.api.model.admin.Instrumentation;
import org.hawkular.btm.api.util.ServiceResolver;

/**
 * @author gbrown
 */
public class Transformer {

    private static Map<Class<? extends InstrumentAction>, InstrumentActionTransformer> transformers =
            new HashMap<Class<? extends InstrumentAction>, InstrumentActionTransformer>();

    static {
        List<InstrumentActionTransformer> trms = ServiceResolver.getServices(InstrumentActionTransformer.class);
        trms.forEach(t -> transformers.put(t.getActionType(), t));
    }

    /**
     * This method transforms the list of instrument types into a
     * ByteMan rule script.
     *
     * @param types The instrument types
     * @return The rule script
     */
    public String transform(Instrumentation types) {
        StringBuilder builder = new StringBuilder();

        for (InstrumentRule rule : types.getRules()) {
            if (builder.length() > 0) {
                builder.append("\r\n");
            }

            builder.append("RULE ");
            builder.append(rule.getRuleName());
            builder.append("\r\n");

            if (rule.getClassName() != null) {
                builder.append("CLASS ");
                builder.append(rule.getClassName());
                builder.append("\r\n");
            } else if (rule.getInterfaceName() != null) {
                builder.append("INTERFACE ");
                builder.append(rule.getInterfaceName());
                builder.append("\r\n");
            }

            builder.append("METHOD ");
            builder.append(rule.getMethodName());
            builder.append('(');

            for (int i = 0; i < rule.getParameterTypes().size(); i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append(rule.getParameterTypes().get(i));
            }

            builder.append(')');
            builder.append("\r\n");
            builder.append("AT ");
            builder.append(rule.getLocation());
            builder.append("\r\n");

            builder.append("IF ");
            if (rule.getCondition() == null) {
                builder.append("TRUE");
            } else {
                builder.append(rule.getCondition());
            }
            builder.append("\r\n");

            builder.append("DO\r\n");

            for (int i = 0; i < rule.getActions().size(); i++) {
                InstrumentAction action = rule.getActions().get(i);

                builder.append("  ");
                InstrumentActionTransformer transformer = transformers.get(action.getClass());

                if (transformer != null) {
                    builder.append(transformer.convertToRuleAction(rule.getActions().get(i)));
                    if (i < rule.getActions().size() - 1) {
                        builder.append(";");
                    }
                    builder.append("\r\n");
                } else {
                    System.err.println("Transformer for action '" + action.getClass() + "' not found");
                }
            }

            builder.append("ENDRULE\r\n\r\n");
        }

        return builder.toString();
    }
}