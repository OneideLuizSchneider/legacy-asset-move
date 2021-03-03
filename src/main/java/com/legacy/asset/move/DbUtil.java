package com.legacy.asset.move;

import com.legacy.asset.move.config.DataSource;
import com.legacy.asset.move.dto.Image;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class DbUtil {

    private static final Dotenv dotenv = Dotenv.load();

    /**
     * <p>
     * This method will update the database with the new bucket/path
     * </p>
     */
    public void execute(Image imageDto) {
        String updateSql = "update `production-db`.images set path = ? where id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, imageDto.getNewPath());
            stmt.setInt(2, imageDto.getId());
            stmt.executeUpdate();
        } catch (SQLException throwables) {
            log.error("Error on method DbUtil.execute-----------------------");
            log.error(throwables.getMessage());
            log.error("----------------------------------------------------");
        }
    }

    /**
     * <p>
     * This method will validate if at least exists one row on the db
     * </p>
     */
    public static boolean exists() {
        String sqlCount = "select count(id) as total from `production-db`.images where path like ? and id % ? = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCount)) {
            stmt.setString(1, dotenv.get("OLD_PATH") + "%");
            stmt.setInt(2, Integer.parseInt(dotenv.get("APP_TOTAL")));
            stmt.setInt(3, Integer.parseInt(dotenv.get("APP_NUMER")));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            log.info("Total: " + rs.getInt("total"));
            return rs.getInt("total") > 0;
        } catch (SQLException throwables) {
            log.error("Error on method DbUtil.exists-----------------------");
            log.error(throwables.getMessage());
            log.error("----------------------------------------------------");
        }
        return false;
    }

    /**
     * <p>
     * This method will return rows to migrate, based on params
     * </p>
     */
    public static ResultSet getImages() {
        String sqlImages = "select id, path from `production-db`.images where path like ? and id % ? = ? limit ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlImages)) {
            stmt.setString(1, dotenv.get("OLD_PATH") + "%");
            stmt.setInt(2, Integer.parseInt(dotenv.get("APP_TOTAL")));
            stmt.setInt(3, Integer.parseInt(dotenv.get("APP_NUMER")));
            stmt.setInt(4, Integer.parseInt(dotenv.get("SQL_LIMIT")));
            return stmt.executeQuery();
        } catch (SQLException throwables) {
            log.error("Error on method DbUtil.getImages-----------------------");
            log.error(throwables.getMessage());
            log.error("-------------------------------------------------------");
        }
        return null;
    }

}
