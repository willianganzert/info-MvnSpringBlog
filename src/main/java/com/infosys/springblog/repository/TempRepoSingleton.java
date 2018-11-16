package com.infosys.springblog.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.springblog.config.ApplicationProperties;
import com.infosys.springblog.domain.DomainInterface;
import com.infosys.springblog.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import java.io.*;
import java.util.*;

public class TempRepoSingleton {
    private final static String FILE_DB = "D:/blog_db.json";
    private static TempRepoSingleton tempRepoSingleton;
    private Map<Class,Map<Long, ? super Object>> mapDB = new HashMap<>();
    private volatile Map<Class,Long> mapDBIDS = new HashMap<>();
    @Autowired
    private ApplicationProperties applicationProperties;

    private TempRepoSingleton(){
        try {
            getUpdateDB();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static TempRepoSingleton getInstance(){
        if(tempRepoSingleton == null){
            tempRepoSingleton = new TempRepoSingleton();
            createDefaultUsers();
        }
        return tempRepoSingleton;
    }

    private static void createDefaultUsers() {
        if(!new File(FILE_DB).exists()) {
            UserRepository userRepository = new UserRepository();
            userRepository.persist(new User("willian.lopes@info.com", "willian", "willian"));
            userRepository.persist(new User("kurt.moriber@info.com", "kurt", "kurt"));
            userRepository.persist(new User("ian.kowza@info.com", "ian", "ian"));
            userRepository.persist(new User("jimi.george@info.com", "jimi", "jimi"));
        }
    }

    public <T> List<T> findAll(Class<T> tab){
        try {
            getUpdateDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List list = new ArrayList();
        if(mapDB.containsKey(tab)){
            list.addAll(mapDB.get(tab).values());
        }
        return list;
    }

    public <T> Optional<T> findOne(Class<T> tab, Long id){
        try {
            getUpdateDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Optional one = Optional.empty();
        if(mapDB.containsKey(tab)){
            if(mapDB.get(tab).containsKey(id)){
                return Optional.of((T)mapDB.get(tab).get(id));
            }
        }
        return one;
    }
    public <T extends DomainInterface> T persist(Class<T> tab, T one){
        try {
            getUpdateDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mapDB.containsKey(tab)){
            if(one.getId() == null){
                one.setId(getLastId(tab));
            }
            mapDB.get(tab).put(one.getId(),one);
        }
        else{
            Long id_ = getLastId(tab);
            one.setId(id_);
            mapDB.put(tab, new TreeMap<>());
            mapDB.get(tab).put(id_,one);
        }
        updateDB();
        return one;
    }

    public <T extends DomainInterface> T delete(Class<T> tab, Long id){
        try {
            getUpdateDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mapDB.containsKey(tab) && mapDB.get(tab).containsKey(id)){
            return (T) mapDB.get(tab).remove(id);
        }
        updateDB();
        return null;
    }

    private synchronized Long getLastId(Class tab){
        Long id = 1L;
        if(mapDBIDS.containsKey(tab)){
            id = mapDBIDS.get(tab)+1L;
        }
        mapDBIDS.put(tab,id);
        return id;
    }

    public static void main(String[] args) {
        TempRepoSingleton.getInstance().updateDB();
    }
    private void getUpdateDB() throws IOException {
        File file = new File(FILE_DB);
        Reader fis = new FileReader(file);
        Map<Class,Map<Long, ? super Object>> mapDB2 = new HashMap<>();
        Map<Class,Long> mapDBIDS = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = Json.createParser(fis);
         while (parser.hasNext()) {
             JsonParser.Event e = parser.next();
             if (e == JsonParser.Event.START_OBJECT) {
                 JsonObject jsonObject = parser.getObject();
                 Set<Map.Entry<String, JsonValue>> entries =  jsonObject.entrySet();
                 for(Map.Entry<String, JsonValue> entry : entries){
                     try {
                         Class clazz = Class.forName(entry.getKey());
                         mapDB2.put(clazz,new HashMap<>());
                         mapDBIDS.put(clazz,0L);
                         JsonObject jsonValues = entry.getValue().asJsonObject();
                         Set<Map.Entry<String, JsonValue>> entries2 =  jsonValues.entrySet();
                         for(Map.Entry<String, JsonValue> entry2 : entries2){
                             DomainInterface di;
                             try {
                                 di = (DomainInterface) mapper.readValue(entry2.getValue().toString(), clazz);
                                 mapDB2.get(clazz).put(di.getId(),di);
                                 if(di.getId() > mapDBIDS.get(clazz)) {
                                     mapDBIDS.put(clazz, di.getId());
                                 }
                                 System.out.println(di);
                             } catch (IOException e1) {
                                 e1.printStackTrace();
                             }
                         }
                     } catch (ClassNotFoundException e1) {
                         e1.printStackTrace();
                     }
                 }
             }
         }
         this.mapDB = mapDB2;
         this.mapDBIDS = mapDBIDS;
    }
    private void updateDB(){
        File file = new File(FILE_DB);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, mapDB);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
