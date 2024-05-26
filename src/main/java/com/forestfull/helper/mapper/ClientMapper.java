package com.forestfull.helper.mapper;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.domain.Json;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ClientMapper {

    @Select("SELECT ch.client_id, ch.type, ch.ip_address, ch.data, ch.created_time FROM client_history ch JOIN client c ON ch.client_id = c.id AND c.token = #{token}")
    List<Client.History> getHistoriesByClientToken(@Param("token") String token);

//    @Insert("INSERT INTO client_history(client_id, ip_address, data) * FROM client_history ch JOIN client c ON ch.client_id = c.id AND c.token = #{token}")
    void toRequestForSolution(String token, Json requestData);

    @Select("SELECT c.id, c.code, c.description FROM client c WHERE c.token = #{token}")
    Optional<Client> getClientByToken(@Param("token") String token);

    @Select("SELECT c.id, c.code, c.description FROM client c")
    List<Client> getAllClient();

    class Provider {

    }
}
