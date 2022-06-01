package com.mis.eoffice.serviceImplsKNH;

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
import com.mis.eoffice.db5Models.AppointmentDisplayNameInventoryKNH;
import com.mis.eoffice.db5Models.FileFolderNameInventoryKNH;
import com.mis.eoffice.db5Models.FileInventoryKNH;
import com.mis.eoffice.db5Repo.AppointmentDisplayNameRepositoryKNH;
import com.mis.eoffice.db5Repo.FileFolderNameRepositoryKNH;
import com.mis.eoffice.db5Repo.FileInventoryRepositoryKNH;
import com.mis.eoffice.dto.DetailedTable;
import com.mis.eoffice.serviceImpls.Messages;

@Service
public class DetailedTableServiceImplKNH {
	private static final Logger logger = LoggerFactory.getLogger(DetailedTableServiceImplKNH.class);

	@Autowired
	private FileInventoryRepositoryKNH filerepo;

	@Autowired
	private FileSauBranchInventoryRepository fileBranchSauInventoryRepository;

	@Autowired
	private AppointmentDisplayNameRepositoryKNH appointmentDisplayNameRepository;

	@Autowired
	private FileFolderNameRepositoryKNH fileFolderNameRepository;

	@Autowired
	private DataSauInventoryRepository htrepo;

	String status = Messages.getString("OperationsDataServiceImpl.FILESTATUS");

	public List<DetailedTable> getdetailedtableInboxFile(String sauName, Integer num,String command) {
		logger.info("command "+command);
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sauName,command);
		if (sauDat.isPresent()) {
			List<FileInventoryKNH> hd1 = filerepo.findByInitiatedBySauAndMisTypeAndTaskState(sauName, "FILE", status);
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					logger.info("filename == " + hd1.get(i).getFileNumber());

					DetailedTable obj = new DetailedTable();
					FileInventoryKNH fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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
		System.out.println(dt.size()+"dt size");
		return dt;
	}

	// typeOfFunction-> 0 for all files , 1 for 5-10 days files, 2 for >10 days and
	// 3 for 0-5 days
	// files
	private List<DetailedTable> filesOfBranches(List<HierarchyDataInventory> sauSubBranches, List<DetailedTable> dt,
			int typeOfFunction, String command) {
		for (HierarchyDataInventory branches : sauSubBranches) {
			List<FileSauBranchInventory> branchOfBranch = fileBranchSauInventoryRepository
					.findByParentIdAndCommand(branches.getSauId(),command);
			if (!branchOfBranch.isEmpty()) {
				dt = filesOfBranches(htrepo.findByParentNodeIdAndCommand(branches.getSauId(),command), dt, typeOfFunction,command);
			}

			if (typeOfFunction == 0) {
				List<FileInventoryKNH> hd2 = filerepo.findByInitiatedBySauAndMisTypeAndTaskState(branches.getSauName(),
						"FILE", status);

				dt = branchesViaType(hd2, dt,command);
			} else if (typeOfFunction == 1) {
				LocalDate currentDate = LocalDate.now();
				LocalDate threeDays = currentDate.plusDays(-5);
				LocalDate sevenDays = currentDate.plusDays(-10);
				Date threedaysbefore = Date.from(threeDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
				List<FileInventoryKNH> hd2 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(
						branches.getSauName(), "FILE", status, sevendaysbefore, threedaysbefore);

				dt = branchesViaType(hd2, dt,command);
			} else if (typeOfFunction == 2) {
				LocalDate currentDate1 = LocalDate.now();
				LocalDate lastDate1 = currentDate1.plusDays(-10);
				Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				List<FileInventoryKNH> hd2 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(
						branches.getSauName(), "FILE", status, lastDate);

				dt = branchesViaType(hd2, dt,command);

			} else {
				LocalDate currentDate1 = LocalDate.now();
				LocalDate lastDate1 = currentDate1.plusDays(-5);
				Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				List<FileInventoryKNH> hd2 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(
						branches.getSauName(), "FILE", status, lastDate);

				dt = branchesViaType(hd2, dt,command);
			}

		}
		return dt;
	}

	private List<DetailedTable> branchesViaType(List<FileInventoryKNH> hd2, List<DetailedTable> dt, String command) {
		
		if (hd2.size() > 0) {
			for (int i = 0; i < hd2.size(); i++) {
				DetailedTable obj = new DetailedTable();
				logger.info("filename == " + hd2.get(i).getFileNumber());
				FileInventoryKNH fm2 = hd2.get(i);
				Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
				String depName ="";
				if(ss.isPresent()) {
					 depName = ss.get().getDepFullName();
				}
				String file = fm2.getFileNumber();
				String iniBy = "";
				Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
				if (fileName.isPresent()) {
					String depFullName = fileName.get().getPklDirectorate();
					Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
					if (fullnames.isPresent()) {
						iniBy = fullnames.get().getDepFullName();
					}
				}
				String subject = fm2.getSubject();
				Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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

	
	public List<DetailedTable> getdetailedtablepend37(String sau, Integer num,String command) {
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
			logger.info("command "+command);
		String status = "In Progress";
		LocalDate currentDate = LocalDate.now();
		LocalDate threeDays = currentDate.plusDays(-10);
		LocalDate sevenDays = currentDate.plusDays(-20);
		Date threedaysbefore = Date.from(threeDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventoryKNH> sauData = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(sau,
				"FILE", status, sevendaysbefore, threedaysbefore);
		List<FileInventoryKNH> sauDataFilter = new ArrayList<FileInventoryKNH>();
		sauDataFilter = sauData.stream().map(data -> {
			return data;
		}).collect(Collectors.toList());
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sau,command);
		if (sauDat.isPresent()) {

			sauDataFilter.removeAll(Collections.singletonList(null));
			if (sauDataFilter.size() > 0) {
				for (int i = 0; i < sauDataFilter.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + sauDataFilter.get(i).getFileNumber());
					FileInventoryKNH fm2 = sauDataFilter.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					String iniBy = "";
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					// String iniBy=fm2.getInitiatedBySau();
					System.out.println(fm2);
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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

			List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeIdAndCommand(sauDat.get().getSauId(),command);
			if (num > dt.size() && !sauSubBranches.isEmpty()) {

				dt = filesOfBranches(sauSubBranches, dt, 1,command);
			}
		}
		return dt;
	}

//	@Override
//	public List<DetailedTable> getdetailedtablepend7(String sau, Integer num) {
//
//		String state = "In Progress";
//		List<DetailedTable> dt = new ArrayList<DetailedTable>();
//		LocalDate currentDate = LocalDate.now();
//		LocalDate sevenDays = currentDate.plusDays(-7);
//		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		List<FileInventory> sauData = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(sau,
//				"FILE", state, sevendaysbefore);
//		List<FileInventory> sauDataFilter = new ArrayList<FileInventory>();
//		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sau);
//		if (sauDat.isPresent()) {
//			List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeId(sauDat.get().getSauId());
//
//			if (!sauData.isEmpty()) {
//				sauDataFilter = sauData.stream().map(data -> {
//
//					return data;
//				}).collect(Collectors.toList());
//				sauDataFilter.removeAll(Collections.singletonList(null));
//				if (sauDataFilter.size() > 0) {
//					for (int i = 0; i < sauDataFilter.size(); i++) {
//						DetailedTable obj = new DetailedTable();
//						FileInventory fm2 = sauDataFilter.get(i);
//						Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
//						String depName ="";
//						if(ss.isPresent()) {
//							 depName = ss.get().getDepFullName();
//						}
//						String file = fm2.getFileNumber();
//						String iniBy = "";
//						Optional<FileFolderNameInventory> fileName = fileFolderNameRepository.findByFileName(file);
//						if (fileName.isPresent()) {
//							String depFullName = fileName.get().getPklDirectorate();
//							Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
//							if (fullnames.isPresent()) {
//								iniBy = fullnames.get().getDepFullName();
//							}
//						}
//						String subject = fm2.getSubject();
//						// String iniBy=fm2.getInitiatedBySau();
//						Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
//								.findByappointment(fm2.getToPerson());
//						if (apptName.isPresent()) {
//							String pendWith = apptName.get().getAppointmentdisplayname();
//							Date pendSince1 = fm2.getDateTimeRecieved();
//							LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//							int days = getdays(pendSince);
//							obj.setDepartmentName(depName);
//							obj.setInitiatedBy(iniBy);
//							obj.setSubject(subject);
//							obj.setPendingSince(pendSince);
//							obj.setPendingWith(pendWith);
//							obj.setPendingdays(days);
//							obj.setFileNumber(file);
//						} else {
//							Date pendSince1 = fm2.getDateTimeRecieved();
//							LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//							int days = getdays(pendSince);
//							obj.setDepartmentName(depName);
//							obj.setInitiatedBy(iniBy);
//							obj.setSubject(subject);
//							obj.setPendingSince(pendSince);
//							obj.setPendingWith("");
//							obj.setPendingdays(days);
//							obj.setFileNumber(file);
//						}
//						dt.add(obj);
//
//					}
//
//					if (num.equals(dt.size())) {
//
//						for (HierarchyDataInventory branches : sauSubBranches) {
//							List<FileInventory> sauData1 = filerepo
//									.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(
//											branches.getSauName(), "FILE", state, sevendaysbefore);
//							if (sauData1.size() > 0) {
//								for (int i = 0; i < sauDataFilter.size(); i++) {
//									DetailedTable obj = new DetailedTable();
//									FileInventory fm2 = sauDataFilter.get(i);
//									Optional<HierarchyDataInventory> ss = htrepo.findBySauName(fm2.getInitiatedBySau());
//									String depName ="";
//									if(ss.isPresent()) {
//										 depName = ss.get().getDepFullName();
//									}
//									String file = fm2.getFileNumber();
//									String iniBy = "";
//									Optional<FileFolderNameInventory> fileName = fileFolderNameRepository
//											.findByFileName(file);
//									if (fileName.isPresent()) {
//										String depFullName = fileName.get().getPklDirectorate();
//										Optional<HierarchyDataInventory> fullnames = htrepo.findBySauName(depFullName);
//										if (fullnames.isPresent()) {
//											iniBy = fullnames.get().getDepFullName();
//										}
//									}
//									String subject = fm2.getSubject();
//									// String iniBy=fm2.getInitiatedBySau();
//									Optional<AppointmentDisplayNameInventory> apptName = appointmentDisplayNameRepository
//											.findByappointment(fm2.getToPerson());
//									String pendWith = apptName.get().getAppointmentdisplayname();
//									Date pendSince1 = fm2.getDateTimeRecieved();
//									LocalDate pendSince = pendSince1.toInstant().atZone(ZoneId.systemDefault())
//											.toLocalDate();
//									int days = getdays(pendSince);
//
//									obj.setDepartmentName(depName);
//									obj.setFileNumber(file);
//									obj.setInitiatedBy(iniBy);
//									obj.setSubject(subject);
//									obj.setPendingSince(pendSince);
//									obj.setPendingWith(pendWith);
//									obj.setPendingdays(days);
//									dt.add(obj);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return dt;
//	}

	public List<DetailedTable> getdetailedtablepro30days(String sau, Integer num,String command) {

		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-20);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventoryKNH> hd1 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sau,command);
		if (sauDat.isPresent()) {
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == "+hd1.get(i).getFileNumber());

					FileInventoryKNH fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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
		
		List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeIdAndCommand(sauDat.get().getSauId(),command);
		if (num > dt.size() && !sauSubBranches.isEmpty()) {
			dt = filesOfBranches(sauSubBranches, dt, 2,command);
		}
		}
		return dt;

	}

	
	public List<DetailedTable> getdetailedtablependingat1020(String sauName, Integer num,String command) {

		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		LocalDate currentDate = LocalDate.now();
		LocalDate threeDays = currentDate.plusDays(-10);
		LocalDate sevenDays = currentDate.plusDays(-20);
		Date threedaysbefore = Date.from(threeDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date sevendaysbefore = Date.from(sevenDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventoryKNH> hd1 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBetween(sauName,
				"FILE", status, sevendaysbefore, threedaysbefore);

		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sauName,command);
		if (sauDat.isPresent()) {
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventoryKNH fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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

	public List<DetailedTable> getdetailedtablependingatten(String sau, Integer num,String command) {

		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-20);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventoryKNH> hd1 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedBefore(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sau,command);
		if (sauDat.isPresent()) {
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventoryKNH fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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


	public List<DetailedTable> getdetailedtablependingatfive(String sau, Integer num,String command) {
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-10);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventoryKNH> hd1 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sau,command);
		if (sauDat.isPresent()) {
//			List<FileInventory> hd1=filerepo.findByInitiatedBySauAndMisTypeAndTaskState(sauName,"FILE",status);??
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventoryKNH fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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


	public List<DetailedTable> getdetailedtableInboxFileCau(String sauName, Integer num,String command) {
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sauName,command);
		if (sauDat.isPresent()) {
			List<FileInventoryKNH> hd1 = filerepo.findByInitiatedBySauAndMisTypeAndTaskState(sauName, "FILE", status);
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					logger.info("filename == " + hd1.get(i).getFileNumber());

					DetailedTable obj = new DetailedTable();
					FileInventoryKNH fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);

					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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

			List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeIdAndCommand(sauDat.get().getSauId(),command);

			if (num > dt.size() && !sauSubBranches.isEmpty()) {
				dt = filesOfBranches(sauSubBranches, dt, 0,command);
			}
		}

		return dt;
	}


	public List<DetailedTable> getdetailedtablependingGreater20(String sau, Integer num,String command) {
		
		List<DetailedTable> dt = new ArrayList<DetailedTable>();
		String taskstate = "In Progress";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate lastDate1 = currentDate1.plusDays(-10);
		Date lastDate = Date.from(lastDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		Date currentDate = Date.from(currentDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileInventoryKNH> hd1 = filerepo.findByInitiatedBySauAndMisTypeAndTaskStateAndDateTimeRecievedAfter(sau,
				"FILE", taskstate, lastDate);
		Optional<HierarchyDataInventory> sauDat = htrepo.findBySauNameAndCommand(sau,command);
		if (sauDat.isPresent()) {
//			List<FileInventory> hd1=filerepo.findByInitiatedBySauAndMisTypeAndTaskState(sauName,"FILE",status);??
			if (hd1.size() > 0) {
				for (int i = 0; i < hd1.size(); i++) {
					DetailedTable obj = new DetailedTable();
					logger.info("filename == " + hd1.get(i).getFileNumber());

					FileInventoryKNH fm2 = hd1.get(i);
					Optional<HierarchyDataInventory> ss = htrepo.findBySauNameAndCommand(fm2.getInitiatedBySau(),command);
					String depName ="";
					if(ss.isPresent()) {
						 depName = ss.get().getDepFullName();
					}
					String file = fm2.getFileNumber();
					String iniBy = "";
					Optional<FileFolderNameInventoryKNH> fileName = fileFolderNameRepository.findByFileName(file);
					if (fileName.isPresent()) {
						String depFullName = fileName.get().getPklDirectorate();
						Optional<HierarchyDataInventory> fullnames = htrepo.findBySauNameAndCommand(depFullName,command);
						if (fullnames.isPresent()) {
							iniBy = fullnames.get().getDepFullName();
						}
					}
					String subject = fm2.getSubject();
					Optional<AppointmentDisplayNameInventoryKNH> apptName = appointmentDisplayNameRepository
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
		List<HierarchyDataInventory> sauSubBranches = htrepo.findByParentNodeIdAndCommand(sauDat.get().getSauId(),command);

		if (num > dt.size() && !sauSubBranches.isEmpty()) {
			dt = filesOfBranches(sauSubBranches, dt, 3,command);
		}}
		return dt;
	}
}
