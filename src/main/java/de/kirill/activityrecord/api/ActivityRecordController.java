package de.kirill.activityrecord.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** REST controller for handling activity records. */
@RestController
@RequestMapping("/activity-record")
@Slf4j
public class ActivityRecordController {

    private final ActivityRecordService activityRecordService;

    /**
     * Constructor for ActivityRecordController.
     *
     * @param activityRecordService the service for handling activity records
     */
    public ActivityRecordController(ActivityRecordService activityRecordService) {
        this.activityRecordService = activityRecordService;
    }

    /**
     * Endpoint for uploading activity records from an Excel file.
     *
     * @param newActivityRecords the Excel file to be uploaded
     * @return an empty response with status 204 (No Content)
     */
    @PostMapping(path = "/upload")
    public ResponseEntity<Void> handleFileUpload(@RequestParam("file") MultipartFile newActivityRecords) {
        activityRecordService.saveActivityRecordsFromExcel(newActivityRecords);
        return ResponseEntity.noContent().build();
    }
}
