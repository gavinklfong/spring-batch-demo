package space.gavinklfong.demo.batch.dao;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import space.gavinklfong.demo.batch.dto.StockMovingAverage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
//@JdbcTest
@Testcontainers
public class StockExponentialMovingAverageDaoIT {

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER =
            new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
                    .withInitScript("schema.sql");

    private StockExponentialMovingAverageDao stockExponentialMovingAverageDao;

    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(MYSQL_CONTAINER.getJdbcUrl());
        dataSource.setUser(MYSQL_CONTAINER.getUsername());
        dataSource.setPassword(MYSQL_CONTAINER.getPassword());
        jdbcClient = JdbcClient.create(dataSource);
        stockExponentialMovingAverageDao = new StockExponentialMovingAverageDao(jdbcClient,
                new StockMovingAverageRowMapper());

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScripts(new ClassPathResource("stock-data.sql"));
        populator.execute(dataSource);
    }

    @Test
    void findByTickerAndOlderOrEqualToDateWithLimit() {
        List<StockMovingAverage> stockMovingAverageList =
                stockExponentialMovingAverageDao.findByTickerAndOlderOrEqualToDateWithLimit("APPL", LocalDate.parse("2024-10-15"), 5);
        assertThat(stockMovingAverageList).hasSize(3);
        stockMovingAverageList.forEach(item -> {
            assertThat(item.getTicker()).isEqualTo("APPL");
            assertThat(item.getDate()).isBefore(LocalDate.parse("2024-10-15"));
        });
    }

}
