package com.legacy.asset.move;

import com.legacy.asset.move.dto.Image;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class Migration {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private ThreadPoolExecutor executor;

    public Migration() {
    }

    /**
     * <p>
     * This method validate and starts the migration
     * </p>
     */
    public void execute() {
        while (DbUtil.exists()) {
            try {
                this.startThreads();
                this.start(
                        this.getImages()
                );
                this.shutdownThreads();
            } catch (SQLException throwables) {
                log.error("Error on method Migration.start--------");
                log.error(throwables.getMessage());
                log.error("---------------------------------------");
            }
        }
    }

    /**
     * <p>
     * This method will return a list of images to migrate
     * </p>
     */
    private List<Image> getImages() throws SQLException {
        List<Image> imageList = new ArrayList<>();
        ResultSet rs = DbUtil.getImages();
        if (rs != null) {
            while (rs.next()) {
                Image dto = new Image(
                        rs.getInt("id"),
                        rs.getString("path"),
                        rs.getString("path").replace(
                                dotenv.get("OLD_PATH"),
                                dotenv.get("NEW_PATH")
                        )
                );
                imageList.add(dto);
            }
            rs.close();
        }
        return imageList;
    }

    /**
     * <p>
     * This method starts the migration and threads
     * to increase the threads, just need to change the .env file and restart
     * </p>
     */
    private void start(List<Image> imageList) {
        imageList.forEach((dto) -> {
            //this will starts all the threads base on rows returned from the db
            executor.execute(new Worker(dto));
        });
    }

    private ThreadPoolExecutor getThreadPool() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.parseInt(dotenv.get("THREADS")));
    }

    private void startThreads() {
        this.executor = getThreadPool();
    }

    private void shutdownThreads() {
        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }
    }

}
