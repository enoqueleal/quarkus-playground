package br.com.playground.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PeopleControllerTest {

    private static Long createdId;

    @Test
    @Order(1)
    @DisplayName("Deve criar uma nova pessoa com sucesso")
    public void testCreatePeople() {
        Integer id = given()
                .contentType(ContentType.JSON)
                .body("{\"firstName\": \"João\", \"lastName\": \"Silva\"}")
                .when()
                .post("/people")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("João"))
                .body("lastName", equalTo("Silva"))
                .extract()
                .path("id");

        createdId = id != null ? id.longValue() : null;
        System.out.println("Pessoa criada com ID: " + createdId);
    }

    @Test
    @Order(2)
    @DisplayName("Deve listar todas as pessoas")
    public void testGetAllPeople() {
        given()
                .when()
                .get("/people")
                .then()
                .statusCode(200)
                .body("", not(empty()))
                .body("[0].id", notNullValue())
                .body("[0].firstName", notNullValue())
                .body("[0].lastName", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("Deve buscar uma pessoa por ID")
    public void testGetPeopleById() {
        given()
                .pathParam("id", createdId)
                .when()
                .get("/people/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(createdId.intValue()))
                .body("firstName", equalTo("João"))
                .body("lastName", equalTo("Silva"));
    }

    @Test
    @Order(4)
    @DisplayName("Deve retornar 404 ao buscar pessoa inexistente")
    public void testGetPeopleByIdNotFound() {
        given()
                .pathParam("id", 99999)
                .when()
                .get("/people/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    @DisplayName("Deve atualizar uma pessoa existente")
    public void testUpdatePeople() {
        given()
                .pathParam("id", createdId)
                .contentType(ContentType.JSON)
                .body("{\"firstName\": \"João\", \"lastName\": \"Santos\"}")
                .when()
                .put("/people/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(createdId.intValue()))
                .body("firstName", equalTo("João"))
                .body("lastName", equalTo("Santos"));
    }

    @Test
    @Order(6)
    @DisplayName("Deve retornar 404 ao atualizar pessoa inexistente")
    public void testUpdatePeopleNotFound() {
        given()
                .pathParam("id", 99999)
                .contentType(ContentType.JSON)
                .body("{\"firstName\": \"Teste\", \"lastName\": \"Teste\"}")
                .when()
                .put("/people/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(7)
    @DisplayName("Deve deletar uma pessoa existente")
    public void testDeletePeople() {
        given()
                .pathParam("id", createdId)
                .when()
                .delete("/people/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(8)
    @DisplayName("Deve retornar 404 ao deletar pessoa inexistente")
    public void testDeletePeopleNotFound() {
        given()
                .pathParam("id", 99999)
                .when()
                .delete("/people/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    @DisplayName("Deve criar múltiplas pessoas e listar todas")
    public void testCreateMultiplePeople() {
        // Criar primeira pessoa
        given()
                .contentType(ContentType.JSON)
                .body("{\"firstName\": \"Maria\", \"lastName\": \"Oliveira\"}")
                .when()
                .post("/people")
                .then()
                .statusCode(201)
                .body("firstName", equalTo("Maria"))
                .body("lastName", equalTo("Oliveira"));

        // Criar segunda pessoa
        given()
                .contentType(ContentType.JSON)
                .body("{\"firstName\": \"Pedro\", \"lastName\": \"Costa\"}")
                .when()
                .post("/people")
                .then()
                .statusCode(201)
                .body("firstName", equalTo("Pedro"))
                .body("lastName", equalTo("Costa"));

        // Listar todas e verificar que tem pelo menos 2 pessoas
        given()
                .when()
                .get("/people")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThanOrEqualTo(2)));
    }

    @Test
    @Order(10)
    @DisplayName("Deve validar requisição POST sem body")
    public void testCreatePeopleWithoutBody() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/people")
                .then()
                .statusCode(500);
    }

    @Test
    @Order(11)
    @DisplayName("Deve validar requisição PUT sem body")
    public void testUpdatePeopleWithoutBody() {
        given()
                .pathParam("id", 1)
                .contentType(ContentType.JSON)
                .when()
                .put("/people/{id}")
                .then()
                .statusCode(500);
    }
}