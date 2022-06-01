package com.mis.eoffice.serviceImpls;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mis.eoffice.db1Models.FileSauBranchInventory;
import com.mis.eoffice.db1Models.HierarchyDataInventory;
import com.mis.eoffice.db1Repo.DataSauInventoryRepository;
import com.mis.eoffice.db1Repo.FileSauBranchInventoryRepository;
import com.mis.eoffice.db2Models.AppointmentDisplayNameInventory;
import com.mis.eoffice.db2Models.FileFolderNameInventory;
import com.mis.eoffice.db2Models.FileInventory;
import com.mis.eoffice.db2Repo.AppointmentDisplayNameRepository;
import com.mis.eoffice.db2Repo.FileFolderNameRepository;
import com.mis.eoffice.db2Repo.FileInventoryRepository;
import com.mis.eoffice.dto.DetailedTable;
import com.mis.eoffice.service.DetailedTableService;

@Service
public class DetailedTableServiceImpl implements DetailedTableService {
	private static final Logger logger = LoggerFactory.getLogger(DetailedTableServiceImpl.class);

	@Autowired
	private FileInventoryRepository filerepo;

	@Autowired
	private FileSauBranchInventoryRepository fileBranchSauInventoryRepository;

	@Autowired
	private AppointmentDisplayNameRepository appointmentDisplayNameRepository;

	@Autowired
	private FileFolderNameRepository fileFolderNameRepository;

	@Autowired
	private DataSauInventoryRepository htrepo;

	String status =Messages.getString("OperationsDataServiceImpl.FILESTATUS");

	@Override
	public List<DetailedTable> getdetailedtableInboxFile(String sauName, Integer num) {

		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sauName);
		if (sauDat.isPresent()) {
			List<FileInventory> hd1 = filerepo.findByCurrDepAndMisTypeAndTaskState(sauName, "FILE", status);
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					logger.info("filename == " + hd1.get(i).getFileNumber());

					DetailedTable obj = new DetailedTable();
					FileInventory fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());

					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					} else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);
				}
			}
	}
		return dt;
	}

	// typeOfFunction-> 0 for all files , 1 for 5-10 days files, 2 for >10 days and
	// 3 for 0-5 days
	// files
	private List<DetailedTable> filesOfBranches(List<HierarchyDataInventory> sauSubBranches, List<DetailedTable> dt,
			int typeOfFunction) {
		for (HierarchyDataInventory branches : sauSubBranches) {
			List<FileSauBranchInventory> branchOfBranch = fileBranchSauInventoryRepository
					.findByParentId(branches.getSauId());
			if (!branchOfBranch.isEmpty()) {
				dt = filesOfBranches(htrepo.findByParentNodeId(branches.getSauId()), dt, typeOfFunction);
			}

			if (typeOfFunction == 0) {
				List<FileInventory> hd2 = filerepo.findByCurrDepAndMisTypeAndTaskState(branches.getSauName(),
						"FILE", status);

				dt = branchesViaType(hd2, dt);
			} else if (typeOfFunction == 1) {
				LocalDate currentDate = LocalDate.now();
				LocalDate threeDays = currentDate.plusDays(-5);
				LocalDate sevenDays = currentDate.plusDays(-10);
				Date threedaysbefore = Date.from(threeDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
				List<FileInventory> hd2 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(
						branches.getSauName(), "FILE", status, sevendaysbefore, threedaysbefore);

				dt = branchesViaType(hd2, dt);
			} else if (typeOfFunction == 2) {
				LocalDate currentDate1 = LocalDate.now();
				LocalDate lastDate1 = currentDate1.plusDays(-10);
				Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				List<FileInventory> hd2 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(
						branches.getSauName(), "FILE", status, lastDate);

				dt = branchesViaType(hd2, dt);

			} else {
				LocalDate currentDate1 = LocalDate.now();
				LocalDate lastDate1 = currentDate1.plusDays(-5);
				Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				List<FileInventory> hd2 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(
						branches.getSauName(), "FILE", status, lastDate);

				dt = branchesViaType(hd2, dt);
			}

		}
		return dt;
	}

	private List<DetailedTable> branchesViaType(List<FileInventory> hd2, List<DetailedTable> dt) {
		
		if (hd2.size() > 0) {
			for (int i = 0; i < hd2.size(); i++) {
				DetailedTable obj = new DetailedTable();
				logger.info("filename == " + hd2.get(i).getFileNumber());
				FileInventory fm2 = hd2.get(i);
				Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
				String depName ="";
				if(ss.isPresent()) {
					 depName = ss.get().getDepFullName();
				}
				String file = fm2.getFileNumber();
				String iniBy = "";
				Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
				if (fileName.isPresent()) {
					String depFullName = fileName.get().getPklDirectorate();
					Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
					if (fullnames.isPresent()) {
						iniBy = fullnames.get().getDepFullName();
					}
				}
				String subject = fm2.getSubject();
				Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
						.findByappointment(fm2.getToPerson());
				if (apptName.isPresent()) {
					String pendWith = apptName.get().getAppointmentdisplayname();
					Date pendSince1 = fm2.getDateTimeRecieved();
					LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					int days = getdays(pendSince);
					obj.setDepartmentName(depName);
					obj.setInitiatedBy(iniBy);
					obj.setSubject(subject);
					obj.setPendingSince(pendSince);
					obj.setPendingWith(pendWith);
					obj.setPendingdays(days);
					obj.setFileNumber(file);
				} else {
					Date pendSince1 = fm2.getDateTimeRecieved();
					LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					int days = getdays(pendSince);
					obj.setDepartmentName(depName);
					obj.setInitiatedBy(iniBy);
					obj.setSubject(subject);
					obj.setPendingSince(pendSince);
					obj.setPendingWith("");
					obj.setPendingdays(days);
					obj.setFileNumber(file);
				}
				dt.add(obj);
			}
		}
		return dt;
	}

	private int getdays(LocalDate pendSince) {
		LocalDate today = LocalDate.now();
		int days = (int) ChronoUnit.DAYS.between(pendSince, today);

		return days;
	}

	@Override
	public List<DetailedTable> getdetailedtablepend37(String sau, Integer num) {
		List<DetailedTable> dt = new ArrayList<DetailedTable>();

		String status = "In Progress";
		LocalDate currentDate = LocalDate.now();
		LocalDate threeDays = currentDate.plusDays(-10);
		LocalDate sevenDays = currentDate.plusDays(-20);
		Date threedaysbefore = Date.from(threeDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> sauData = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(sau,
				"FILE", status, sevendaysbefore, threedaysbefore);
		List<FileInventory> sauDataFilter = new ArrayList<FileInventory>();
		sauDataFilter = sauData.stream().map(data -> {
			return data;
		}).collect(Collectors.toList());
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sau);
		if (sauDat.isPresent()) {

			sauDataFilter.removeAll(Collections.singletonList(null));
			if (sauDataFilter.size() > 0) {
				for (int i = 0; i < sauDataFilter.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + sauDataFilter.get(i).getFileNumber());
					FileInventory fm2 = sauDataFilter.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					String iniBy = "";
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					// String iniBy=fm2.getInitiatedBySau();
					System.out.println(fm2);
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					} else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);

				}
			}

			List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeId(sauDat.get().getSauId());
			if (num > dt.size() && !sauSubBranches.isEmpty()) {

				dt = filesOfBranches(sauSubBranches, dt, 1);
			}
		}
		return dt;
	}

	@Override
	public List<DetailedTable> getdetailedtablepend7(String sau, Integer num) {

		String state = "In Progress";
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		LocalDate currentDate = LocalDate.now();
		LocalDate sevenDays = currentDate.plusDays(-7);
		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> sauData = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(sau,
				"FILE", state, sevendaysbefore);
		List<FileInventory> sauDataFilter = new ArrayList<FileInventory>();
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sau);
		if (sauDat.isPresent()) {
			List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeId(sauDat.get().getSauId());

			if (!sauData.isEmpty()) {
				sauDataFilter = sauData.stream().map(data -> {

					return data;
				}).collect(Collectors.toList());
				sauDataFilter.removeAll(Collections.singletonList(null));
				if (sauDataFilter.size() > 0) {
					for (int i = 0; i < sauDataFilter.size(); i++) {
						DetailedTable obj = new DetailedTable();
						FileInventory fm2 = sauDataFilter.get(i);
						Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
						String depName ="";
						if(ss.isPresent()) {
							 depName = ss.get().getDepFullName();
						}
						String file = fm2.getFileNumber();
						String iniBy = "";
						Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
						if (fileName.isPresent()) {
							String depFullName = fileName.get().getPklDirectorate();
							Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
							if (fullnames.isPresent()) {
								iniBy = fullnames.get().getDepFullName();
							}
						}
						String subject = fm2.getSubject();
						// String iniBy=fm2.getInitiatedBySau();
						Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
								.findByappointment(fm2.getToPerson());
						if (apptName.isPresent()) {
							String pendWith = apptName.get().getAppointmentdisplayname();
							Date pendSince1 = fm2.getDateTimeRecieved();
							LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
							int days = getdays(pendSince);
							obj.setDepartmentName(depName);
							obj.setInitiatedBy(iniBy);
							obj.setSubject(subject);
							obj.setPendingSince(pendSince);
							obj.setPendingWith(pendWith);
							obj.setPendingdays(days);
							obj.setFileNumber(file);
						} else {
							Date pendSince1 = fm2.getDateTimeRecieved();
							LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
							int days = getdays(pendSince);
							obj.setDepartmentName(depName);
							obj.setInitiatedBy(iniBy);
							obj.setSubject(subject);
							obj.setPendingSince(pendSince);
							obj.setPendingWith("");
							obj.setPendingdays(days);
							obj.setFileNumber(file);
						}
						dt.add(obj);

					}

					if (num.equals(dt.size())) {

						for (HierarchyDataInventory branches : sauSubBranches) {
							List<FileInventory> sauData1 = filerepo
									.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(
											branches.getSauName(), "FILE", state, sevendaysbefore);
							if (sauData1.size() > 0) {
								for (int i = 0; i < sauDataFilter.size(); i++) {
									DetailedTable obj = new DetailedTable();
									FileInventory fm2 = sauDataFilter.get(i);
									Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
									String depName ="";
									if(ss.isPresent()) {
										 depName = ss.get().getDepFullName();
									}
									String file = fm2.getFileNumber();
									String iniBy = "";
									Optional<FileFolderNameInventory> fileName = fileFolderNameRepository
											.findByFileName(file);
									if (fileName.isPresent()) {
										String depFullName = fileName.get().getPklDirectorate();
										Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
										if (fullnames.isPresent()) {
											iniBy = fullnames.get().getDepFullName();
										}
									}
									String subject = fm2.getSubject();
									// String iniBy=fm2.getInitiatedBySau();
									Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
											.findByappointment(fm2.getToPerson());
									String pendWith = apptName.get().getAppointmentdisplayname();
									Date pendSince1 = fm2.getDateTimeRecieved();
									LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault())
											.toLocalDate();
									int days = getdays(pendSince);

									obj.setDepartmentName(depName);
									obj.setFileNumber(file);
									obj.setInitiatedBy(iniBy);
									obj.setSubject(subject);
									obj.setPendingSince(pendSince);
									obj.setPendingWith(pendWith);
									obj.setPendingdays(days);
									dt.add(obj);
								}
							}
						}
					}
				}
			}
		}
		return dt;
	}

	@Override
	public List<DetailedTable> getdetailedtablepro30days(String sau, Integer num) {

		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-20);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> hd1 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sau);
		if (sauDat.isPresent()) {
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == "+hd1.get(i).getFileNumber());

					FileInventory fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);
				}
			}
		
		List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeId(sauDat.get().getSauId());
		if (num > dt.size() && !sauSubBranches.isEmpty()) {
			dt = filesOfBranches(sauSubBranches, dt, 2);
		}
		}
		return dt;

	}

	@Override
	public List<DetailedTable> getdetailedtablependingatfiveten(String sauName, Integer num) {

		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		LocalDate currentDate = LocalDate.now();
		LocalDate threeDays = currentDate.plusDays(-10);
		LocalDate sevenDays = currentDate.plusDays(-20);
		Date threedaysbefore = Date.from(threeDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> hd1 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBetween(sauName,
				"FILE", status, sevendaysbefore, threedaysbefore);

		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sauName);
		if (sauDat.isPresent()) {
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventory fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					} else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);
				}
			}
		}
		return dt;
	}

	@Override
	public List<DetailedTable> getdetailedtablependingatten(String sau, Integer num) {

		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-20);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> hd1 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedBefore(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sau);
		if (sauDat.isPresent()) {
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventory fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					} else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);
				}
			}
		}
		return dt;
	}

	@Override
	public List<DetailedTable> getdetailedtablependingatfive(String sau, Integer num) {
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-10);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> hd1 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sau);
		if (sauDat.isPresent()) {
//			List<FileInventory> hd1=filerepo.findByCurrDepAndMisTypeAndTaskState(sauName,"FILE",status);??
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventory fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					} else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);
				}
			}
		}
		return dt;
	}

	@Override
	public List<DetailedTable> getdetailedtableInboxFileCau(String sauName, Integer num) {
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sauName);
		if (sauDat.isPresent()) {
			List<FileInventory> hd1 = filerepo.findByCurrDepAndMisTypeAndTaskState(sauName, "FILE", status);
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					logger.info("filename == " + hd1.get(i).getFileNumber());

					DetailedTable obj = new DetailedTable();
					FileInventory fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());

					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					} else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);
				}
			}

			List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeId(sauDat.get().getSauId());

			if (num > dt.size() && !sauSubBranches.isEmpty()) {
				dt = filesOfBranches(sauSubBranches, dt, 0);
			}
		}

		return dt;
	}

	@Override
	public List<DetailedTable> getdetailedtablependingatzerofive(String sau, Integer num) {
		
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-10);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventory> hd1 = filerepo.findByCurrDepAndMisTypeAndTaskStateAndDateTimeRecievedAfter(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauName(sau);
		if (sauDat.isPresent()) {
//			List<FileInventory> hd1=filerepo.findByCurrDepAndMisTypeAndTaskState(sauName,"FILE",status);??
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventory fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
							.findByappointment(fm2.getToPerson());
					if (apptName.isPresent()) {
						String pendWith = apptName.get().getAppointmentdisplayname();
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith(pendWith);
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					} else {
						Date pendSince1 = fm2.getDateTimeRecieved();
						LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						int days = getdays(pendSince);
						obj.setDepartmentName(depName);
						obj.setInitiatedBy(iniBy);
						obj.setSubject(subject);
						obj.setPendingSince(pendSince);
						obj.setPendingWith("");
						obj.setPendingdays(days);
						obj.setFileNumber(file);
					}
					dt.add(obj);
				}
			}
		List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeId(sauDat.get().getSauId());

		if (num > dt.size() && !sauSubBranches.isEmpty()) {
			dt = filesOfBranches(sauSubBranches, dt, 3);
		}}
		return dt;
	}
}
