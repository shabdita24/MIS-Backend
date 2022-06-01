package com.mis.eoffice.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mis.eoffice.service.CauDataService;
import com.mis.eoffice.service.HierarchyDataService;
import com.mis.eoffice.service.SauDataService;


@Component
public class EventScheduler {

	private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);

	@Autowired
	private HierarchyDataService hierarchyDataService; 

	@Autowired
	private SauDataService sauDataService;

	@Autowired
	private CauDataService cauDataService;

//	@Async
//	@Scheduled(cron = "${event.schedule.hierarchy.data}")
//	public void getHeadValue() {
//
//		log.info("Processing Scheduler...");
//		try
//		{
//			hierarchyDataService.InsertSauDataInventory();
//		}
//		catch (Exception e) {
//			log.info("Failed Data Table Push !");
//		}
//	}

	@Async
	@Scheduled(cron = "${event.schedule.sau.data}")
	public void pushSauDataTable() {

		try
		{
			sauDataService.mapSauData1();		}
		catch (Exception e) {
			log.info("Failed Data Table Push !");
		}
	}

	@Async
	@Scheduled(cron = "${event.schedule.branch.data}")
	public void pushSauBranchDataTable() {

		try
		{
			cauDataService.mapCauData();

		}
		catch (Exception e) {
			log.info("Failed Data Table Push !");
		}
	}
}
