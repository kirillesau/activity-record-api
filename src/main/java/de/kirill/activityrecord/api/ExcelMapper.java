package de.kirill.activityrecord.api;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Service class for mapping Excel data to ActivityRecord entities. */
@Service
public class ExcelMapper {
    @Value("${activity-record.upload.name-cell}")
    private int CELL_NAME;

    @Value("${activity-record.upload.date_of_proof-cell}")
    private int CELL_DATE_OF_PROOF;

    @Value("${activity-record.upload.location-cell}")
    private int CELL_LOCATION;

    @Value("${activity-record.upload.work_package-cell}")
    private int CELL_WORK_PACKAGE;

    @Value("${activity-record.upload.description-cell}")
    private int CELL_DESCRIPTION;

    @Value("${activity-record.upload.workload-cell}")
    private int CELL_WORKLOAD;

    @Value("${activity-record.upload.project-cell}")
    private int CELL_PROJECT;

    @Value("${activity-record.upload.customer_billable-cell}")
    private int CELL_CUSTOMER_BILLABLE;

    @Value("${activity-record.upload.name-label}")
    private String CELL_NAME_LABEL;

    @Value("${activity-record.upload.sum-label}")
    private String CELL_SUM_LABEL;

    /**
     * Maps the input Excel data to a list of ActivityRecord entities.
     *
     * @param inputStream the input stream of the Excel file
     * @return a list of ActivityRecord entities
     */
    public List<ActivityRecord> mapToActivityRecords(InputStream inputStream) {
        List<ActivityRecord> activityRecords = new ArrayList<>();
        var workbook = createWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);
        int headerRow = findHeaderColumn(sheet);
        int sumRow = findSumColumn(sheet);

        for (var row : sheet) {
            if (row.getRowNum() <= headerRow || row.getRowNum() >= sumRow) {
                continue;
            }
            activityRecords.add(mapRowToEntity(row));
        }
        return activityRecords;
    }

    /**
     * Maps a single row of the Excel sheet to an ActivityRecord entity.
     *
     * @param row the row to be mapped
     * @return the mapped ActivityRecord entity
     */
    private ActivityRecord mapRowToEntity(Row row) {
        var newActivityRecord = new ActivityRecord();
        newActivityRecord.setEmployeeName(row.getCell(CELL_NAME).getStringCellValue().trim());
        newActivityRecord.setProject(row.getCell(CELL_PROJECT).getStringCellValue().trim());
        newActivityRecord.setDateOfProof(row.getCell(CELL_DATE_OF_PROOF).getLocalDateTimeCellValue().toLocalDate());
        newActivityRecord.setLocation(row.getCell(CELL_LOCATION).getStringCellValue().trim());
        newActivityRecord.setWorkPackage(row.getCell(CELL_WORK_PACKAGE).getStringCellValue().trim());
        newActivityRecord.setDescription(row.getCell(CELL_DESCRIPTION).getStringCellValue().trim());
        newActivityRecord.setWorkload(getWorkload(row));
        newActivityRecord.setCustomerBillable("KV".equals(row.getCell(CELL_CUSTOMER_BILLABLE).getStringCellValue().trim()));
        return newActivityRecord;
    }

    /**
     * Extracts the workload time from the Excel row.
     *
     * @param row the row containing the workload data
     * @return the workload time as a LocalTime object
     */
    private LocalTime getWorkload(Row row) {
        double numericCellValue = row.getCell(CELL_WORKLOAD).getNumericCellValue();
        int hours = (int) numericCellValue;
        int minutes = (int) ((numericCellValue - hours) * 60);
        return LocalTime.of(hours, minutes);
    }

    /**
     * Creates a Workbook object from the input stream.
     *
     * @param inputStream the input stream of the Excel file
     * @return the created Workbook object
     * @throws IllegalArgumentException if the workbook cannot be created
     */
    private Workbook createWorkbook(InputStream inputStream) {
        try {
            return WorkbookFactory.create(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create workbook", e);
        }
    }

    /**
     * Finds the row number of the sum row in the Excel sheet.
     *
     * @param sheet the Excel sheet
     * @return the row number of the sum row
     * @throws IllegalArgumentException if no sum row is found
     */
    private int findSumColumn(Sheet sheet) {
        for (Row row : sheet) {
            Cell cell = row.getCell(CELL_DESCRIPTION);
            if (cell == null) continue;
            if (cell.getStringCellValue().equals(CELL_SUM_LABEL)) {
                return row.getRowNum();
            }
        }
        throw new IllegalArgumentException("No sum row found");
    }

    /**
     * Finds the row number of the header row in the Excel sheet.
     *
     * @param sheet the Excel sheet
     * @return the row number of the header row
     * @throws IllegalArgumentException if no header row is found
     */
    private int findHeaderColumn(Sheet sheet) {
        for (Row row : sheet) {
            Cell cell = row.getCell(CELL_NAME);
            if (cell == null) continue;
            if (cell.getStringCellValue().equals(CELL_NAME_LABEL)) {
                return row.getRowNum();
            }
        }
        throw new IllegalArgumentException("No header row found");
    }

}
