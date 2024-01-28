package org.automatics.qa.tests;

import static io.restassured.RestAssured.given;

import java.util.Random;

import org.automatics.qa.testfwk.DefaultTestNGTest;
import org.automatics.qa.testfwk.base.TestProperties;
import org.automatics.qa.testfwk.config.RuntimeConfig;
import org.automatics.qa.testfwk.core.StaticFactory;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class GetAllPetsTest extends DefaultTestNGTest {
	
	@Test
	public void scenario() {
		String petStatus = testDataJson.get("status").getAsString();
		
		log.info("Status to be searched for=" + petStatus);
		
		RestAssured.baseURI=RuntimeConfig.getPropertyByName(TestProperties.BASE_URL);
		
		Response responseObject = given().contentType(ContentType.JSON)
				.param("status",petStatus)
				.get("/findByStatus").then().extract().response();
		
		int statusCode = responseObject.getStatusCode();
		String responseBody = responseObject.getBody().asString();
		log.info("Status=" + statusCode);
		log.info("Status Line=" + responseObject.getStatusLine());
		log.info("Body=" + responseBody);
		
		softassert.assertEquals(statusCode, 200);
		
		JsonElement responseJson = new JsonParser().parse(responseBody);
		
		log.info("response json = " + responseJson);
		
		JsonArray responseJsonArray = responseJson.getAsJsonArray();
		int numOfElements = responseJsonArray.size();
		log.info("number of elements=" + numOfElements);
		
		int randomPetCounter = new Random().nextInt(10) +1;
		log.info("Random index=" + randomPetCounter);
		log.info("Value of " + randomPetCounter + " = " + responseJsonArray.get(randomPetCounter));
		
		StaticFactory.petInfo = responseJsonArray.get(randomPetCounter);
		softassert.assertAll();
	}

}
