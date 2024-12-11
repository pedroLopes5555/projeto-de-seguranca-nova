package Repository;

import Objects.User;

import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository{

    String PATH_TO_USER_DATABASE = "src/cfg/Server/userdatabase.txt";
    List<User> Users;


    public Repository() {
        Users = loadUsers();
    }


    public User getUserById(String userId){
        for (User item : Users) {
            if(item.getUserId().equals(userId)){
                return item;
            }
        }
        return null;
    }



    private List<User> loadUsers(){
        var result = new ArrayList<User>(){
        };

        result.add(new User("paulinho@gmail.com", "[B@1d7acb34"));

        return result;
    }
}
