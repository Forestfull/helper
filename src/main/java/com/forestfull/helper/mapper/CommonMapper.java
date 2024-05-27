package com.forestfull.helper.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommonMapper {

    @Insert("INSERT INTO request_history(uri, header, attribute, body) VALUES (#{uri}, #{header}, #{attribute}, #{body})")
    void recordRequestHistory(@Param("uri") String uri
            , @Param("header") String header
            , @Param("attribute") String attribute
            , @Param("body") String body);
}