package com.forestfull.helper.mapper;

import com.forestfull.helper.entity.Json;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ManagementMapper {


    @Insert("INSERT INTO client_history(client_id, type, ip_address, data) VALUES (#{clientId}, 'response', #{ipAddress}, #{requestData})")
    void toRequestForSolution(@Param("clientId") Long clientId, @Param("ipAddress") String ipAddress, @Param("requestData") Json requestData);

    class Provider {

    }
}