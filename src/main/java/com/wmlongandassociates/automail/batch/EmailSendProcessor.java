package com.wmlongandassociates.automail.batch;

import com.wmlongandassociates.automail.email.EmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class EmailSendProcessor implements ItemProcessor<File, File> {

    @Autowired
    EmailServiceImpl emailService;

    @Override
    public File process(File item) throws Exception {
        emailService.sendSimpleMessage("pgrazaitis@gmail.com", "Test", "Body of message.", item);
        return item;
    }
}
