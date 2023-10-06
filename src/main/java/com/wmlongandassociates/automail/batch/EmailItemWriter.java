package com.wmlongandassociates.automail.batch;


import com.wmlongandassociates.automail.email.EmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
@Slf4j
public class EmailItemWriter implements ItemWriter<File> {

    public static String DELIVERY_PATH = "C://temp//sender//delivered//";

    @Override
    public void write(Chunk<? extends File> chunk) throws Exception {
        for(File file : chunk) {
            File deliveredPath = new File(DELIVERY_PATH + file.getName());
            Files.move(file.toPath(), deliveredPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
