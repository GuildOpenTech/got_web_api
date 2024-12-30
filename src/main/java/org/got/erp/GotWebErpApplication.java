package org.got.erp;

import org.got.erp.usersmanagement.service.TestDataService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GotWebErpApplication {
  private final TestDataService testDataService;

  public GotWebErpApplication(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  public static void main(String[] args) {
    GotWebErpApplication app = SpringApplication.run(GotWebErpApplication.class, args)
            .getBean(GotWebErpApplication.class);
    app.initData();
  }

  private void initData() {
    testDataService.createSampleData();
  }
}