/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates.
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
 */
package io.helidon.common.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the {@link FormParams} interface.
 */
class FormParamsImpl extends ReadOnlyParameters implements FormParams {

    /*
     * For form params represented in text/plain (uncommon), newlines appear between name=value
     * assignments. When urlencoded, ampersands separate the name=value assignments.
     */
    private static final Map<MediaType, Pattern> PATTERNS = Map.of(
            MediaType.APPLICATION_FORM_URLENCODED, preparePattern("&"),
            MediaType.TEXT_PLAIN, preparePattern("\n"));

    private FormParamsImpl(Map<String, List<String>> params) {
        super(params);
    }

    FormParamsImpl(FormParams.Builder builder) {
        super(builder.params());
    }

    private static Pattern preparePattern(String assignmentSeparator) {
        return Pattern.compile(String.format("([^=]+)=([^%1$s]+)%1$s?", assignmentSeparator));
    }

    static FormParams create(String paramAssignments, MediaType mediaType) {
        final Map<String, List<String>> params = new HashMap<>();
        Matcher m = PATTERNS.get(mediaType).matcher(paramAssignments);
        while (m.find()) {
            final String key = m.group(1);
            final String value = m.group(2);
            params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return new FormParamsImpl(params);
    }

}
