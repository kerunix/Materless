package matterless.fr.wcs.matterless;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apprenti on 16/03/17.
 */

public class UsersLogin {
    @SerializedName("login_id")
    private String email;
    @SerializedName("password")
    private String password;

    public UsersLogin(String email, String password) {

        this.email = email;
        this.password = password;
    }

}
