package org.automatics.qa.tests;

import org.automatics.qa.testfwk.DefaultTestNGTest;
import org.automatics.qa.testfwk.base.TestProperties;
import org.automatics.qa.testfwk.config.RuntimeConfig;
import org.automatics.qa.testfwk.core.StaticFactory;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UpdatePetTest extends DefaultTestNGTest {
	
	@Test
	public void scenario() {
		JsonObject currentPetInfo = StaticFactory.petInfo.getAsJsonObject();
		
		String currentPetName=currentPetInfo.get("name").getAsString();
		log.info("current pet name=" + currentPetName);
		
		String currentPetId = currentPetInfo.getAsJsonObject().get("id").getAsString();
		String modifiedPetName="modified"+ currentPetId;
		
		currentPetInfo.remove("name");
		currentPetInfo.addProperty("name", modifiedPetName);
		
		log.info("updated Pet json=" + currentPetInfo);
		
		StaticFactory.petInfo = currentPetInfo;
		
		RestAssured.baseURI = RuntimeConfig.getPropertyByName(TestProperties.BASE_URL);
		Response responseObject = given().body(currentPetInfo)
				.contentType(ContentType.JSON)
				.put().then().extract().response();
		
		int statusCode = responseObject.getStatusCode();
		log.info("Status=" + statusCode);
		
		softassert.assertEquals(statusCode, 200);
		JsonObject responseJson = new JsonParser().parse(responseObject.asString()).getAsJsonObject();
		
		String updatedName = responseJson.get("name").getAsString();
		log.info("updatedName="+updatedName);
		
		softassert.assertEquals(updatedName, currentPetInfo.get("name").getAsString());
		
		softassert.assertAll();
	}

}
