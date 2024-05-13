package endpoints;
import static io.restassured.RestAssured.*;

import api.Routes;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.Name;
import pojo.Order;
import pojo.User;

public class Methods {
	
static String token;
	
	
	
public static Response getStatus() {
	
Response resp= given().get(Routes.status);
return resp;
}

public static Response getListOfBooks() {
	
Response resp= given().get(Routes.listOfBooks);
return resp;
}

public static Response getBookWithID(int id) {
	

Response resp= given().pathParam("id",id).get(Routes.getByID);
return resp;
}


public static Response getBooksByType(String type) {
	
Response resp= given().queryParam("type", type).get(Routes.listOfBooks);
return resp;
}

public static Response getBooksWithLimit(int limit) {
	
Response resp= given().queryParam("limit", limit).get(Routes.listOfBooks);
return resp;
}

public static Response authenticateUser(User user) {
	
Response resp= given().contentType("application/json") 
.accept(ContentType.JSON).body(user).post(Routes.authenticate);
return resp;
}

public static String getBearerToken(User user) {
	
Response resp= given().contentType("application/json") 
.accept(ContentType.JSON).body(user).post(Routes.authenticate);
return resp.jsonPath().get("accessToken").toString();
}

public static Response orderAbook(Order order,User user) {
	
token=Methods.getBearerToken(user);
Response resp= given().contentType("application/json") 
.header("Authorization", "Bearer " + token)
.accept(ContentType.JSON).body(order).post(Routes.orderBook);
return resp;
	
}

public static Response getAllOrders() {
	
Response resp=given().header("Authorization", "Bearer " + token)
.get(Routes.getOrders);
return resp;
}

public static Response updateOrder(Name name,String orderID) {
	
Response resp= given().pathParam("id",orderID)
.contentType("application/json") 
.header("Authorization", "Bearer " + token)
.body(name).patch(Routes.updateOrder);
return resp;
}

public static Response getSingleOrder(String orderID) {
	
Response resp= given().pathParam("id",orderID)
.header("Authorization", "Bearer " + token)
.get(Routes.getOrder);
return resp;
}

public static Response deleteOrder(String orderID) {
	
Response resp= given().pathParam("id",orderID)
.header("Authorization", "Bearer " + token)
.delete(Routes.delete);
return resp;
}




public static int getSizeOfArray(Response resp,String json) {
	
int size=0;
try {
while(true) {
if(resp.jsonPath().get(json+"["+size+"]").toString().equals(null)) {
break;	
}
size++;
}
} catch (Exception e) {
System.out.print("Exception Occur "+e);
}
return size;
}








}
