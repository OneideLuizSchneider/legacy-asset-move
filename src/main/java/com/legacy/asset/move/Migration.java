package com.legacy.asset.move;

import com.legacy.asset.move.config.ConnectionS3;
import com.legacy.asset.move.dto.Image;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class Migration {

    private static final Dotenv dotenv = Dotenv.load();

    /**
     * <p>
     * This method validate and starts the migration
     * </p>
     */
    public void execute() {
        while (DbUtil.exists()) {
            try {
                this.start();
            } catch (SQLException throwables) {
                log.error("Error on method Migration.start--------");
                log.error(throwables.getMessage());
                log.error("---------------------------------------");
            }
        }
    }

    /**
     * <p>
     * This method starts the migration and threads
     * to increase the threads, just need to change the .env file and restart
     * </p>
     */
    private void start() throws SQLException {
        ResultSet rs = DbUtil.getImages();
        if (rs != null) {
            ThreadPoolExecutor executor = getThreadPool();

            while (rs.next()) {
                Image dto = new Image(
                        rs.getInt("id"),
                        rs.getString("path"),
                        rs.getString("path").replace(
                                dotenv.get("OLD_PATH"),
                                dotenv.get("NEW_PATH")
                        )
                );
                executor.submit(() -> {
                    new ConnectionS3().copyObject(
                            dto.getOldPath(),
                            dto.getNewPath()
                    );
                    new DbUtil().execute(dto);
                });
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    log.error(ex.getMessage());
                }
            }

            rs.close();
        }
    }

    private ThreadPoolExecutor getThreadPool() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.parseInt(dotenv.get("THREADS")));
    }

}
