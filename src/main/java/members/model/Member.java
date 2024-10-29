package members.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class Member {
    @BsonId
    private ObjectId _id;
    
    @BsonProperty("name")
    private String name;
    
    @BsonProperty("email")
    private String email;
    
    @BsonProperty("phone_number")
    private String phoneNumber;
    
    // Required no-args constructor for POJO mapping
    public Member() {}

    // Add toString for logging
    @Override
    public String toString() {
        return "Member{" +
            "_id=" + _id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            '}';
    }
    
    // Getters and Setters
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
}
