package com.ai.cmdlinerunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FoodInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static Double GST_VALUE;

//    public Double fetchGstValue() {
//        String sql = "SELECT gst FROM gst LIMIT 1";
//        return jdbcTemplate.queryForObject(sql, Double.class);
//    }

    @Override
    public void run(String... args) throws Exception {
        GST_VALUE = 5d; //fetchGstValue();

    }
}
