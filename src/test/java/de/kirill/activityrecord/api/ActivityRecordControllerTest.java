package de.kirill.activityrecord.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ActivityRecordControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ActivityRecordRepository activityRecordRepository;

    @Test
    @DisplayName("Activity records can be uploaded")
    void activity_records_can_be_uploaded() throws Exception {
        assertThat(activityRecordRepository.count()).isEqualTo(0);

        MockMultipartFile file = new MockMultipartFile("file", "activity_records.xlsx", TEXT_PLAIN_VALUE, new FileInputStream("src/test/resources/activity_records.xlsx"));
        mvc.perform(multipart("/activity-record/upload")
                        .file(file))
                .andExpect(status().isNoContent());

        assertThat(activityRecordRepository.count()).isEqualTo(5);

        List<ActivityRecord> activityRecords = activityRecordRepository.findAll();

        assertThat(activityRecords).extracting(
                ActivityRecord::getEmployeeName,
                ActivityRecord::getDateOfProof,
                ActivityRecord::getLocation,
                ActivityRecord::getWorkPackage,
                ActivityRecord::getDescription,
                ActivityRecord::getWorkload,
                ActivityRecord::getProject,
                ActivityRecord::getCustomerBillable
        ).containsExactlyInAnyOrder(
                tuple("John Doe", LocalDate.of(2024, 1, 1), "New York", "Feature X", "Description X", LocalTime.of(9, 30), "Project X", true),
                tuple("Jane Smith", LocalDate.of(2024, 1, 2), "Los Angeles", "Feature Y", "Description Y", LocalTime.of(4, 0), "Project Y", false),
                tuple("Alice Johnson", LocalDate.of(2024, 1, 3), "Chicago", "Feature Z", "Description Z", LocalTime.of(5, 0), "Project Z", true),
                tuple("Bob Brown", LocalDate.of(2024, 1, 4), "Houston", "Feature W", "Description W", LocalTime.of(9, 0), "Project W", false),
                tuple("Charlie Davis", LocalDate.of(2024, 1, 5), "Phoenix", "Feature V", "Description V", LocalTime.of(10, 0), "Project V", true)
        );
        assertThat(activityRecords).extracting(ActivityRecord::getEmployeeName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith", "Alice Johnson", "Bob Brown", "Charlie Davis");
        assertThat(activityRecords).extracting(ActivityRecord::getDateOfProof)
                .containsExactlyInAnyOrder(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 3), LocalDate.of(2024, 1, 4), LocalDate.of(2024, 1, 5));
        assertThat(activityRecords).extracting(ActivityRecord::getLocation)
                .containsExactlyInAnyOrder("New York", "Los Angeles", "Chicago", "Houston", "Phoenix");
        assertThat(activityRecords).extracting(ActivityRecord::getWorkPackage)
                .containsExactlyInAnyOrder("Feature X", "Feature Y", "Feature Z", "Feature W", "Feature V");
        assertThat(activityRecords).extracting(ActivityRecord::getDescription)
                .containsExactlyInAnyOrder("Description X", "Description Y", "Description Z", "Description W", "Description V");
        assertThat(activityRecords).extracting(ActivityRecord::getWorkload)
                .containsExactlyInAnyOrder(LocalTime.of(9, 30), LocalTime.of(4, 0), LocalTime.of(5, 0), LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertThat(activityRecords).extracting(ActivityRecord::getProject)
                .containsExactlyInAnyOrder("Project X", "Project Y", "Project Z", "Project W", "Project V");
    }

    @Test
    @DisplayName("Bad Request for file incorrect file format")
    void bad_request_for_incorrect_file_format() throws Exception {
        assertThat(activityRecordRepository.count()).isEqualTo(0);

        MockMultipartFile file = new MockMultipartFile("file", "activity_records_incorrect_file_format.txt", TEXT_PLAIN_VALUE, new FileInputStream("src/test/resources/activity_records_incorrect_file_format.txt"));
        mvc.perform(multipart("/activity-record/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Could not create workbook"));

        assertThat(activityRecordRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Bad Request for file without header")
    void bad_request_for_file_without_header() throws Exception {
        assertThat(activityRecordRepository.count()).isEqualTo(0);

        MockMultipartFile file = new MockMultipartFile("file", "activity_records_without_header.xlsx", TEXT_PLAIN_VALUE, new FileInputStream("src/test/resources/activity_records_without_header.xlsx"));
        mvc.perform(multipart("/activity-record/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No header row found"));

        assertThat(activityRecordRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Bad Request for file without sum row")
    void bad_request_for_file_without_sum_row() throws Exception {
        assertThat(activityRecordRepository.count()).isEqualTo(0);

        MockMultipartFile file = new MockMultipartFile("file", "activity_records_without_sum.xlsx", TEXT_PLAIN_VALUE, new FileInputStream("src/test/resources/activity_records_without_sum.xlsx"));
        mvc.perform(multipart("/activity-record/upload")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No sum row found"));

        assertThat(activityRecordRepository.count()).isEqualTo(0);
    }
}