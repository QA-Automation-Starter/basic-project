/*
 * Copyright 2024 Adrian Herscu
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

package it.pkg.scenarios.tutorial6;

import static dev.aherscu.qa.jgiven.commons.utils.AbstractCsvDataProvider.*;
import static dev.aherscu.qa.testing.utils.StreamMatchersExtensions.*;
import static jakarta.ws.rs.core.Response.Status.Family.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.*;

import dev.aherscu.qa.jgiven.commons.model.*;
import dev.aherscu.qa.jgiven.commons.utils.*;
import dev.aherscu.qa.jgiven.rest.model.*;
import dev.aherscu.qa.jgiven.rest.tags.*;
import dev.aherscu.qa.testing.utils.rest.*;
import it.pkg.*;
import it.pkg.model.tutorial.*;
import it.pkg.steps.tutorial.*;
import jakarta.ws.rs.client.*;

@RestTest
public class SwaggerPetstore extends
    ConfigurableScenarioTest<TestConfiguration, RestScenarioType, SwaggerPetstoreFixtures<?>, SwaggerPetstoreActions<?>, SwaggerPetstoreVerifications<?>> {

    public static final class CredentialsCsvDataProvider
        extends AbstractCsvDataProvider {
        @Override
        protected Class<?> type() {
            return Credentials.class;
        }
    }

    protected SwaggerPetstore() {
        super(TestConfiguration.class);
    }

    protected Client client;

    @AfterClass
    protected void afterClassCloseRestClient() {
        client.close();
    }

    @BeforeClass
    protected void beforeClassOpenRestClient() {
        client = LoggingClientBuilder.newClient();
    }

    @Test(dataProviderClass = CredentialsCsvDataProvider.class, dataProvider = DATA)
    public void shouldLogin(final Credentials credentials) {
        given()
            .a_swagger_petstore(client)
            .with(configuration());

        when()
            .logging_in_with(credentials);

        then()
            .the_login_response(hasProperty("code", equalTo(200)))
            .and().the_response_status(is(SUCCESSFUL));
    }

    @Test
    public void shouldAddPet() {
        given()
            .a_swagger_petstore(client)
            .with(configuration());

        when()
            .adding(Pet.builder()
                .name(randomId())
                .build());

        then()
            .the_available_pets(adaptedStream(pet -> pet.name,
                hasItemsMatching(equalTo(randomId()))));
    }
}
