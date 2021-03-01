package com.legacy.asset.move;

import com.legacy.asset.move.config.DataSource;
import com.legacy.asset.move.dto.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbUtil {

    public void execute(Image imageDto) {
        String updateSql = "update `production-db`.images set path = ? where id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, imageDto.getNewPath());
            stmt.setInt(2, imageDto.getId());
            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
