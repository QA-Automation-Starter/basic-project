/*
 * Copyright 2023 Adrian Herscu
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

package it.pkg.scenarios.tutorial8;

import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.elasticsearch.*;
import lombok.*;
import lombok.extern.jackson.*;

// NOTE duplicated from qa-jgiven-elasticsearch module
// -- maybe should do something else here?
public class ElasticSearchTest
    extends
    AbstractElasticSearchTest<ElasticSearchTest.AnObject, ElasticSearchTest.AnObject> {

    @Builder
    @Jacksonized
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class AnObject {
        public static final AnObject DUMMY = AnObject.builder()
            .id("dummy")
            .value("kuku")
            .build();

        public final String          id;
        public final String          value;
    }

    protected ElasticSearchTest() {
        super(TestConfiguration.class);
    }

    @Test
    public void shouldIndexDocument() {
        given().indexed_by("some-objects")
            .and().storing(AnObject.class)
            .and().elastic_search(configuration()
                .elasticSearchClient());

        when()
            .adding_single_document(AnObject.DUMMY, AnObject::getId);

        then()
            .the_document("dummy", is(AnObject.DUMMY));
    }

    @Test
    void shouldFindDocument() {
        given().indexed_by("some-objects").and().storing(AnObject.class).and()
            .elastic_search(configuration()
                .elasticSearchClient());

        when()
            .adding_single_document(AnObject.DUMMY, AnObject::getId);

        then()
            .the_index(q -> q.match(m -> m
                .field("value")
                .query("kuku")),
                hasSpecificItemsInAnyOrder(AnObject.DUMMY));
    }
}
