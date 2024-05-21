package com.forestfull.helper.mapper;

import com.forestfull.helper.domain.Client;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

@Mapper
public interface SupportMapper {

    @SelectProvider(type = Provider.class, method = "getHistoriesByClientToken")
    List<Client.History> getHistoriesByClientToken(@Param("token") String token);

    class Provider {
        public static String getHistoriesByClientToken(@Param("token") String token) {
            return new SQL() {{
                SELECT("ch.client_id, ch.type, ch.ip_address, ch.data, ch.created_time");
                FROM("client_history ch");
                JOIN("client c ON ch.client_id = c.id AND c.token = #{token}");

            }}.toString();
        }

    }
}
