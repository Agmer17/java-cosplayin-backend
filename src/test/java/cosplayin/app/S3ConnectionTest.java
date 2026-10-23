package cosplayin.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class S3ConnectionTest {

    @Autowired
    private S3Client s3Client;

    @Test
    void shouldConnectToS3() {
        assertNotNull(s3Client);

        var response = s3Client.listBuckets();

        response.buckets().forEach(
                bucket -> System.out.println(bucket.name())
        );
    }
}
