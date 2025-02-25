package org.egov.vendor.driver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.vendor.config.VendorConfiguration;
import org.egov.vendor.driver.repository.DriverRepository;
import org.egov.vendor.driver.web.model.Driver;
import org.egov.vendor.driver.web.model.DriverRequest;
import org.egov.vendor.driver.web.model.DriverResponse;
import org.egov.vendor.driver.web.model.DriverSearchCriteria;
import org.egov.vendor.web.model.user.User;
import org.egov.vendor.web.model.user.UserDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DriverService {

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private DriverEnrichmentService enrichmentService;

	@Autowired
	private DriverUserService userService;

	@Autowired
	private VendorConfiguration config;

	public Driver create(DriverRequest driverRequest) {

		if (driverRequest.getDriver().getTenantId().split("\\.").length == 1) {
			throw new CustomException("Invalid TenantId", " Application cannot be create at StateLevel");
		}
		driverRequest.getDriver().getOwner()
				.setMobileNumber(driverRepository.getDriverSeqMobileNum(getSeqDriverMobileNumber()));
		userService.manageDrivers(driverRequest);
		enrichmentService.enrichCreate(driverRequest);
		driverRepository.save(driverRequest);
		return driverRequest.getDriver();

	}

	/*
	 * This method is to increment mobile number each time driver is created
	 */
	private String getSeqDriverMobileNumber() {
		return config.getDriverMobileNumberIncrement();
	}

	public Driver update(DriverRequest driverRequest) {

		if (driverRequest.getDriver().getTenantId().split("\\.").length == 1) {
			throw new CustomException("Invalid TenantId", " Application cannot be updated at StateLevel");
		}
		userService.manageDrivers(driverRequest);
		enrichmentService.enrichUpdate(driverRequest);
		driverRepository.update(driverRequest);
		return driverRequest.getDriver();

	}

	public DriverResponse search(DriverSearchCriteria criteria, RequestInfo requestInfo) {

		UserDetailResponse userDetailResponse;

		if (criteria.isDriverWithNoVendor()) {
			List<String> driverIds = driverRepository.fetchDriverIdsWithNoVendor(criteria);
			if (CollectionUtils.isEmpty(criteria.getIds())) {
				criteria.setIds(driverIds);
			} else {
				criteria.getIds().addAll(driverIds);
			}

		}

		if (criteria.getMobileNumber() != null) {
			userDetailResponse = userService.getOwner(criteria, requestInfo);
			if (userDetailResponse != null && userDetailResponse.getUser() != null
					&& !userDetailResponse.getUser().isEmpty()) {
				List<String> uuids = userDetailResponse.getUser().stream().map(User::getUuid)
						.collect(Collectors.toList());
				if (CollectionUtils.isEmpty(criteria.getOwnerIds())) {
					criteria.setOwnerIds(uuids);
				} else {
					criteria.getOwnerIds().addAll(uuids);
				}
			}
		}
		return getDriverResponse(criteria, requestInfo);

	}

	private DriverResponse getDriverResponse(DriverSearchCriteria criteria, RequestInfo requestInfo) {
		DriverResponse driverResponse = driverRepository.getDriverData(criteria);
		if (driverResponse != null && !driverResponse.getDriver().isEmpty()) {
			enrichmentService.enrichDriverSearch(driverResponse.getDriver(), requestInfo, criteria.getTenantId());
		}
		if (driverResponse != null && driverResponse.getDriver().isEmpty()) {
			List<Driver> drivers = new ArrayList<>();
			driverResponse.setDriver(drivers);
			return driverResponse;
		}
		return driverResponse;
	}
}
