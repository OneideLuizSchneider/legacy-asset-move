package com.legacy.asset.move;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Starting Migration " + new Date());
        new Migration().execute();
        log.info("Finished Migration " + new Date());
    }

}
