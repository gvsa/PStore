package org.automatics.qa.tests;

import org.automatics.qa.testfwk.DefaultTestNGTest;
import org.automatics.qa.testfwk.base.TestProperties;
import org.automatics.qa.testfwk.config.RuntimeConfig;
import org.automatics.qa.testfwk.core.StaticFactory;
import org.testng.annotations.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class GetPetTest extends DefaultTestNGTest {
	
	@Test
	public void scenario() {
		JsonElement currentPetInfo = StaticFactory.petInfo;
		
		String currentPetId = currentPetInfo.getAsJsonObject().get("id").getAsString();
		
		RestAssured.baseURI = RuntimeConfig.getPropertyByName(TestProperties.BASE_URL);
		
		Response currentPetResponse = given().get("/"+currentPetId).then().extract().response();
		
		int statusCode = currentPetResponse.getStatusCode();
		softassert.assertEquals(statusCode, 200);
		
		String responseBody = currentPetResponse.getBody().asString();
		log.info("obtained pet info=" + responseBody);
		
		JsonElement responseJson = new JsonParser().parse(responseBody);
		log.info("response json = " + responseJson);
		
		String responseId = responseJson.getAsJsonObject().get("id").getAsString();
		
		softassert.assertEquals(responseId, currentPetId);
		
		softassert.assertAll();
	}
 
}
