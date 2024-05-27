package com.forestfull.helper.mapper;

import com.forestfull.helper.domain.Client;
import com.forestfull.helper.entity.Json;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ClientMapper {

    @Select("SELECT ch.client_id, ch.type, ch.ip_address, ch.data, ch.created_time FROM client_history ch JOIN client c ON ch.client_id = c.id AND c.token = #{token}")
    List<Client.History> getHistoriesByClientToken(@Param("token") String token);

    @Insert("INSERT INTO client_history(client_id, ip_address, data) VALUES (#{clientId}, #{ipAddress}, #{requestData})")
    void toRequestForSolution(@Param("clientId") Long clientId, @Param("ipAddress") String ipAddress, @Param("requestData") Json requestData);

    @Select("SELECT c.id, c.code, c.description FROM client c WHERE c.token = #{token}")
    Optional<Client> getClientByToken(@Param("token") String token);

    @Select("SELECT c.id, c.code, c.description FROM client c WHERE c.is_used = TRUE")
    List<Client> getUsedAllClient();

    class Provider {

    }
}