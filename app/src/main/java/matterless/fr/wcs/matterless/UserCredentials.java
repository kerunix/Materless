package matterless.fr.wcs.matterless;

/**
 * Created by apprenti on 30/03/17.
 */

public class UserCredentials {

    private String email;
    private String password;
    private String token;
    private String imageUrl;
    private String userName;
    private String userID;

    public UserCredentials(String userID, String email, String password, String token, String imageUrl, String userName){

        this.userID = userID;
        this.email = email;
        this.password = password;
        this.token = token;
        this.imageUrl = imageUrl;
        this.userName = userName;
    }

    public UserCredentials (String[] stringArray){

        this.userID = stringArray[0];
        this.email = stringArray[1];
        this.password = stringArray[2];
        this.token = stringArray[3];
        this.imageUrl = stringArray[4];
        this.userName = stringArray[5];


    }

    public String oneString(){
        return this.userID+ "|"+this.email+ "|"+this.password+"|"+this.getToken()+"|"+this.imageUrl+"|"+this.userName;
    }



    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }
}
