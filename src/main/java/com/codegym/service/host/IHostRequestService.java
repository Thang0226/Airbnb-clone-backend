package com.codegym.service.host;

import com.codegym.model.HostRequest;
import com.codegym.service.IGenerateService;

public interface IHostRequestService extends IGenerateService<HostRequest> {

    Integer[] getIncomeByMonth(String hostUsername, Integer numberOfMonth);
    Integer[] getIncomeByQuarter(String hostUsername, Integer numberOfQuarter);
    Integer[] getIncomeByYear(String hostUsername, Integer numberOfYear);
}
