package com.wmlongandassociates.automail.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class EmailWriteListener implements ItemWriteListener<File> {
    @Override
    public void afterWrite(Chunk<? extends File> items) {
        log.info("Successfully moved processed files.");
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends File> items) {
        log.info("Could not move chunk of successfully processed files.");
    }
}
