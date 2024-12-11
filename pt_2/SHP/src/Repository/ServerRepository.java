package Repository;

import Objects.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ServerRepository implements IServerRepository {

    String PATH_TO_USER_DATABASE = "src/cfg/Server/userdatabase.txt";
    List<User> Users;


    public ServerRepository() throws Exception {
        Users = loadUsers();
    }


    public User getUserById(String userId) {
        for (User item : Users) {
            if(item.getUserId().equals(userId)){
                return item;
            }
        }
        return null;
    }



    private List<User> loadUsers() throws Exception{
        var result = new ArrayList<User>();
        File file = new File(PATH_TO_USER_DATABASE);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;


        while ((st = br.readLine()) != null){
            if(st.charAt(0) != '#'){
                //System.out.println("linha: " + st);
                String[] elements = st.split(":");
                result.add(new User(elements[0], elements[1]));

                for(String i: elements){
                    System.out.println(i);
                }
            }
        }
        return result;
    }
}
