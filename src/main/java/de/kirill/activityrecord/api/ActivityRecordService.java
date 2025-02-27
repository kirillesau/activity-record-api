package de.kirill.activityrecord.api;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/** Repository interface for ActivityRecord entities. Extends JpaRepository to provide CRUD operations. */
@Service
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;
    private final ExcelMapper excelMapper;

    /**
     * Constructs an ActivityRecordService with the specified repository and mapper.
     *
     * @param activityRecordRepository the repository for activity records
     * @param excelMapper              the mapper for converting Excel data to activity records
     */
    public ActivityRecordService(ActivityRecordRepository activityRecordRepository, ExcelMapper excelMapper) {
        this.activityRecordRepository = activityRecordRepository;
        this.excelMapper = excelMapper;
    }

    /**
     * Saves activity records from an Excel file.
     *
     * @param newActivityRecords the Excel file containing new activity records
     * @throws RuntimeException if the file could not be read
     */
    public void saveActivityRecordsFromExcel(MultipartFile newActivityRecords) {
        try {
            activityRecordRepository.saveAll(excelMapper.mapToActivityRecords(newActivityRecords.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Could not read file", e);
        }
    }
}
