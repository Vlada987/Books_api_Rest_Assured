package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import endpoints.Methods;
import io.restassured.response.Response;
import pojo.Name;
import pojo.Order;
import pojo.User;

public class TestClass {
	

Faker faker=new Faker();	

//@Test(priority=1)
public void Test_01_Get_Status() {

Response resp=Methods.getStatus();
int statusCode=resp.getStatusCode();
String status=resp.jsonPath().get("status").toString();

Assert.assertEquals(statusCode, 200);
Assert.assertEquals(status, "OK");
}

//@Test(priority=2)
public void Test_02_Get_List_of_Books() {
	
Response resp=Methods.getListOfBooks();
int statusCode=resp.getStatusCode();
String firstBookName=resp.jsonPath().get("name[0]").toString();

Assert.assertEquals(statusCode, 200);
Assert.assertEquals(firstBookName, "The Russian");
}

//@Test(priority=3)
public void Test_03_Get_Book_with_ID() {
	
Response resp=Methods.getBookWithID(1);
int statusCode=resp.getStatusCode();
String author=resp.jsonPath().get("author").toString();

Assert.assertEquals(statusCode, 200);
Assert.assertTrue(author.contains("Patterson"));
}


//@Test(priority=4)
public void Test_04_Get_Book_with_Invalid_ID() {
	
int bookId=500;
Response resp=Methods.getBookWithID(bookId);
int statusCode=resp.getStatusCode();
String error=resp.jsonPath().get("error").toString();

Assert.assertEquals(statusCode, 404);
Assert.assertTrue(error.equals("No book with id "+String.valueOf(bookId)));
}

//@Test(priority=5)
public void Test_05_Get_books_By_Type() {
	
Response resp= Methods.getBooksByType("fiction");
int statusCode=resp.getStatusCode();
Boolean bookType=false;

int size=Methods.getSizeOfArray(resp, "name");
for(int a=0;a<size;a++) {
String type=resp.jsonPath().get("type["+a+"]").toString();
if(type.equals("fiction")) {
bookType=true;
}
else {
bookType=false;	
}
}
Assert.assertEquals(statusCode, 200);
Assert.assertEquals(bookType, true);
}


//@Test(priority=6)
public void Test_06_Get_limmited_list() {
	
Response resp= Methods.getBooksWithLimit(3);
resp.then().log().all();
}


//@Test(priority=7)
public void Test_07_Authentication() {
	
User user=new User();
Faker faker=new Faker();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
Response resp= Methods.authenticateUser(user);
int statusCode=resp.getStatusCode();
String token=resp.jsonPath().get("accessToken").toString();

Assert.assertEquals(statusCode, 201);
Assert.assertTrue(!token.equals(null));

}

//@Test(priority=8)
public void Test_08_Authentication_Failed_WithSameUser() {
	
User user=new User();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
Response resp= Methods.authenticateUser(user);
Response resp2= Methods.authenticateUser(user);
int statusCode=resp2.getStatusCode();
String error=resp2.jsonPath().get("error").toString();

Assert.assertEquals(statusCode, 409);
Assert.assertTrue(error.contains("already registered"));

}

//@Test(priority=9)
public void Test_09_Order_A_Book() {
	
User user=new User();
Order order=new Order();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
order.setBookId(1);
order.setCustomerName("pera");
Response resp= Methods.orderAbook(order, user);
Boolean created=resp.jsonPath().get("created");
int statusCode=resp.getStatusCode();

Assert.assertEquals(statusCode, 201);
Assert.assertEquals(created, true);

}


//@Test(priority=10)
public void Test_10_Order_A_NonAvailable_Book() {
	
//Getting a non-available book ID
int non_available_BookID =0;
Response resp_bookList=Methods.getListOfBooks();
int size=Methods.getSizeOfArray(resp_bookList, "name");
for(int a=0;a<size;a++) {
Boolean status=resp_bookList.jsonPath().get("available["+a+"]");
if(status==false) {
non_available_BookID=resp_bookList.jsonPath().get("id["+a+"]");
break;
}
}
User user=new User();
Order order=new Order();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
order.setBookId(non_available_BookID);
order.setCustomerName("john");
Response resp= Methods.orderAbook(order, user);
String error=resp.jsonPath().get("error").toString();
int statusCode=resp.getStatusCode();

Assert.assertEquals(statusCode, 404);
Assert.assertTrue(error.contains("This book is not in stock."));
}


//@Test(priority=11)
public void Test_11_Order_And_GettingOrder() {
	
User user=new User();
Order order=new Order();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
order.setBookId(1);
order.setCustomerName("sean");
Methods.orderAbook(order, user);
Response resp=Methods.getAllOrders();
int statusCode=resp.getStatusCode();
String customerName=resp.jsonPath().get("customerName").toString();

Assert.assertEquals(statusCode, 200);
Assert.assertTrue(customerName.contains("sean"));

}

//@Test(priority=12)
public void Test_12_Update_Order() {
	
User user=new User();
Order order=new Order();
Name name=new Name();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
order.setBookId(1);
order.setCustomerName("sean");
Methods.orderAbook(order, user);
Response resp=Methods.getAllOrders();
String orderIDX=resp.jsonPath().get("id").toString();
String orderID=orderIDX.substring(1,orderIDX.length()-1); 
name.setCustomerName("James");
Response patch_resp=Methods.updateOrder(name, orderID);
int statusCode=patch_resp.getStatusCode();

Assert.assertEquals(statusCode, 204);
}

//@Test(priority=13)
public void Test_13_update_And_GetOrder_After() {
	
User user=new User();
Order order=new Order();
Name name=new Name();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
order.setBookId(1);
order.setCustomerName("sean");
Methods.orderAbook(order, user);
Response resp=Methods.getAllOrders();
String orderIDX=resp.jsonPath().get("id").toString();
String orderID=orderIDX.substring(1,orderIDX.length()-1); 
name.setCustomerName("Miichael");
Methods.updateOrder(name, orderID);
Response resp2=Methods.getSingleOrder(orderID);
int statusCode=resp2.getStatusCode();
String updated_name=resp2.jsonPath().get("customerName").toString();

Assert.assertEquals(statusCode, 200);
Assert.assertTrue(updated_name.contains("Miichael"));
}


//@Test(priority=14)
public void Test_14_delete_Order() {
	
User user=new User();
Order order=new Order();
Name name=new Name();
user.setClientName(faker.name().firstName());
user.setClientEmail(faker.internet().emailAddress());
order.setBookId(1);
order.setCustomerName("sean");
Methods.orderAbook(order, user);
Response resp=Methods.getAllOrders();
String orderIDX=resp.jsonPath().get("id").toString();
String orderID=orderIDX.substring(1,orderIDX.length()-1); 
Response resp2=Methods.deleteOrder(orderID);
int statusCode=resp.getStatusCode();

Assert.assertEquals(statusCode, 204);





}














}
