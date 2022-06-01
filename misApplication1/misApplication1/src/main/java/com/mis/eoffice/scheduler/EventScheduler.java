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

	@Async
	@Scheduled(cron = "${event.schedule.hierarchy.data}")
	public void getHeadValue() {

		log.info("Processing Scheduler...");
		try
		{
			hierarchyDataService.InsertSauDataInventory();
		}
		catch (Exception e) {
			log.info("Failed Data Table Push !");
		}
	}

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

//* "0 0 * * * *" = the top of every hour of every day.
//* "*/10 * * * * *" = every ten seconds.
//* "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
//* "0 0 8,10 * * *" = 8 and 10 o'clock of every day.
//* "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//* "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
//* "0 0 0 25 12 ?" = every Christmas Day at midnight