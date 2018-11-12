package com.infosys.springblog.repository;

import com.infosys.springblog.domain.DomainInterface;

import java.util.*;

public class TempRepoSingleton {
    private static TempRepoSingleton tempRepoSingleton;
    private Map<Class,Map<Long, ? super Object>> mapDB = new HashMap<>();
    private volatile Map<Class,Long> mapDBIDS = new HashMap<>();
    private TempRepoSingleton(){}


    public static TempRepoSingleton getInstance(){
        if(tempRepoSingleton == null){
            tempRepoSingleton = new TempRepoSingleton();
        }
        return tempRepoSingleton;
    }

    public <T> List<T> findAll(Class<T> tab){
        List list = new ArrayList();
        if(mapDB.containsKey(tab)){
            list.addAll(mapDB.get(tab).values());
        }
        return list;
    }

    public <T> Optional<T> findOne(Class<T> tab, Long id){
        Optional one = Optional.empty();
        if(mapDB.containsKey(tab)){
            if(mapDB.get(tab).containsKey(id)){
                return Optional.of((T)mapDB.get(tab).get(id));
            }
        }
        return one;
    }
    public <T extends DomainInterface> T persist(Class<T> tab, T one){
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
        return one;
    }

    public <T extends DomainInterface> T delete(Class<T> tab, Long id){
        if(mapDB.containsKey(tab) && mapDB.get(tab).containsKey(id)){
            return (T) mapDB.get(tab).remove(id);
        }
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

}
