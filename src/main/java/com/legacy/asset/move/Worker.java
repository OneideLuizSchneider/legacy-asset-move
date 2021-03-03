package com.legacy.asset.move;

import com.legacy.asset.move.config.ConnectionS3;
import com.legacy.asset.move.dto.Image;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Worker implements Runnable {

    private final Image dto;

    public Worker(Image dto) {
        this.dto = dto;
    }

    @Override
    public void run() {
        log.info(Thread.currentThread().getName() + " Start. ID = " + dto.getId());
        execute();
        log.info(Thread.currentThread().getName() + " End. ID = " + dto.getId());
    }

    private void execute() {
        boolean done = new ConnectionS3().copyObject(
                dto.getOldPath(),
                dto.getNewPath()
        );
        if (done) {
            new DbUtil().execute(dto);
        }
    }

}
