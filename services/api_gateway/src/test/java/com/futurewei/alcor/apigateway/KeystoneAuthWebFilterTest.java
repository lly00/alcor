/*
Copyright 2019 The Alcor Authors.

Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

package com.futurewei.alcor.apigateway;

import com.futurewei.alcor.apigateway.filter.KeystoneAuthWebFilter;
import com.futurewei.alcor.apigateway.subnet.SubnetWebHandlers;
import com.futurewei.alcor.apigateway.client.KeystoneClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"httpbin=http://localhost:${wiremock.server.port}",
                "keystone.enable=true", "neutron.url_prefix=/v2.0"})
@AutoConfigureWireMock(port = 0)
public class KeystoneAuthWebFilterTest {

    private static final String TEST_TOKEN = "gaaaaaBex0xWssdfsadfDSSDFSDF";
    private static final String TEST_PROJECT_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final String TEST_ERROR_TOKEN = "testerrortoken";


    @Autowired
    private WebTestClient webClient;

    @Autowired
    private KeystoneAuthWebFilter keystoneAuthWebFilter;

    @MockBean
    private KeystoneClient keystoneClient;

    @MockBean
    private SubnetWebHandlers subnetWebHandlers;

    @Before
    public void setUp(){
        ReflectionTestUtils.setField(keystoneAuthWebFilter, "keystoneClient", keystoneClient);
        when(keystoneClient.verifyToken(TEST_TOKEN)).thenReturn(TEST_PROJECT_ID);
        when(keystoneClient.verifyToken(TEST_ERROR_TOKEN)).thenReturn("");

        Mono<ServerResponse> response = ServerResponse.ok().body
                (BodyInserters.fromObject("[{\"network_id\":\"bbaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\"}]"));
        when(subnetWebHandlers.getSubnets(ArgumentMatchers.any(ServerRequest.class))).thenReturn(response);
        Mono<ServerResponse> responseError = ServerResponse.status(500).build();
        when(subnetWebHandlers.createSubnet(ArgumentMatchers.any(ServerRequest.class))).thenReturn(responseError);
    }

    @Test
    public void testNormal(){

        webClient
                .get().uri("/v2.0/subnets")
                .header("X-Auth-Token", TEST_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].network_id").isEqualTo("bbaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    }

    @Test
    public void testNotFound(){
        webClient
                .get().uri("/v2.0/test/not/found")
                .header("X-Auth-Token", TEST_TOKEN)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testNoToken(){
        webClient
                .get().uri("/v2.0/subnets")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testErrorToken(){
        webClient
                .get().uri("/v2.0/subnets")
                .header("X-Auth-Token", TEST_ERROR_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testServerError(){
        webClient
                .post().uri("/v2.0/subnets")
                .header("X-Auth-Token", TEST_TOKEN)
                .header("Content-Type", "application/json")
                .body(BodyInserters.fromObject("{\"vpcId\": \"bbaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\"}"))
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
