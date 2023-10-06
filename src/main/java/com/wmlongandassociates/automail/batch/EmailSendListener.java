package com.wmlongandassociates.automail.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class EmailSendListener implements ItemProcessListener<File, File> {

    @Override
    public void afterProcess(File item, @Nullable File result) {
        log.info("Successfully delivered: {}", item.getName());
    }


    @Override
    public void onProcessError(File item, Exception e) {
        log.info("Failed to deliver: {}", item.getName());
    }

}
