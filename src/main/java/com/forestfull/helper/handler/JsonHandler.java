package com.forestfull.helper.handler;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.forestfull.helper.domain.Json;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.swinnus.reefer.entity.common.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@MappedTypes(Json.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonDataTypeHandler extends BaseTypeHandler<Json> {

    public static ObjectMapper objectMapper = new ObjectMapper();
    public static ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    public static ObjectReader objectReader = objectMapper.reader();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Json parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    public static Json getJsonData(String contents) {
        try {
            final JsonNode jsonNode = objectReader.readTree(contents);
            return jsonNode.isObject() ? getJsonData(jsonNode) : objectReader.readValue(contents);
        } catch (IOException e) {
            return null;
        }
    }

    protected static Json getJsonData(JsonNode jsonNode) throws IOException {
        if (Objects.isNull(jsonNode)) return null;
        if (jsonNode.isEmpty()) return null;
        if (jsonNode.isArray()) {
            jsonNode.withArray(new JsonPointer())
            objectReader.readValue(jsonNode, Json.class).entrySet().stream()
                    .peek(entry -> {
                        if (ObjectUtils.isEmpty(entry.getValue())) return;
                        if (entry.getValue() instanceof List<?>) {

                        }
                    })
                    .collect(Json::new
                            , (json, entry) -> json.put(entry.getKey(), entry.getValue())
                            , (json, json2) -> json.)

        }


        Set<Map.Entry<String, JsonElement>> entrySet = jsonNode.entrySet();
        entrySet.forEach(entry -> {
            final JsonElement jsonElement = entry.getValue();
            try {
                if (jsonElement.isJsonArray() && !jsonElement.getAsJsonArray().isEmpty()) {
                    jsonData.put(entry.getKey(), JsonDataArrayTypeHandler.getJsonDataList(jsonElement.toString()));

                } else if (jsonElement.isJsonObject()) {
                    jsonData.put(entry.getKey(), getJsonData(jsonElement.getAsJsonObject()));

                } else if (jsonElement.isJsonNull() || (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().isEmpty())) {
                    jsonData.put(entry.getKey(), null);

                } else {
                    jsonData.put(entry.getKey(), jsonElement.getAsString());

                }
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace(System.out);
                jsonData.put(entry.getKey(), null);

            }
        });

        return jsonData;
    }

    @Override
    public Json getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getJsonData(rs.getString(columnName));
    }

    @Override
    public Json getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getJsonData(rs.getString(columnIndex));
    }

    @Override
    public Json getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getJsonData(cs.getString(columnIndex));
    }
}