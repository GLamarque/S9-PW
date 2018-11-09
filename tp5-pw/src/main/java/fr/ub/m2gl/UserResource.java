package fr.ub.m2gl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Path("/")
public class UserResource {
    @PUT
    @Path("user/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User modifyUser(@PathParam("id")String ID, User modifications){
        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("myBase");
            MongoCollection<Document> collection = db.getCollection("myCollection");

            Document docToModify = collection.find(eq("id", ID)).first();
            User userToModify = new User(ID, docToModify.getString("firstname"), docToModify.getString("lastname"));
            if (modifications.getFirstname() != null) {
                userToModify.setFirstname(modifications.getFirstname());
            }
            if (modifications.getLastname() != null) {
                userToModify.setLastname(modifications.getLastname());
            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(userToModify);
            Document doc = Document.parse(jsonString);

            collection.replaceOne(eq("id", ID), doc);

            return userToModify;
        } catch (Exception e) {
            return modifications;
        }
    }

    @DELETE
    @Path("user/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id")String ID){
        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("myBase");
            MongoCollection<Document> collection = db.getCollection("myCollection");
            collection.deleteOne(eq("id", ID)).getDeletedCount();
            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(404).build();
        }
    }

    @GET
    @Path("user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id")String ID){
        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("myBase");
            MongoCollection<Document> collection = db.getCollection("myCollection");
            Document user = collection.find(eq("id", ID)).first();

            return new User(user.getString("id"), user.getString("firstname"), user.getString("lastname"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("users")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<User> listAllUsers(){
        ArrayList<User> users = new ArrayList<User>();

        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("myBase");
            MongoCollection<Document> collection = db.getCollection("myCollection");

            List<Document> usersList = collection.find().into(new ArrayList<Document>());
            for (Document user :
                    usersList) {
                users.add(new User(user.getString("id"), user.getString("firstname"), user.getString("lastname")));
            }
            return users;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @POST
    @Path("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User tmpUser){
        String result = "";
        User newUser = new User(tmpUser.getFirstname(), tmpUser.getLastname());
        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("myBase");
            MongoCollection<Document> collection = db.getCollection("myCollection");

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(newUser);
            Document doc = Document.parse(jsonString);
            collection.insertOne(doc);
            result += "User " + newUser.getFirstname() + " " + newUser.getLastname() + " added successfully.";
            return Response.status(201).encoding(result).build();
        } catch (Exception e) {
            return Response.status(404).entity(e.toString()).build();
        }
    }
}
