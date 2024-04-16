package backend.backend.repos;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class S3Repo {
    
    @Autowired
    private AmazonS3 s3;

    public String uploadListingImage(String id, MultipartFile file) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest putReq = new PutObjectRequest("dazzling-heisenberg",
                            "buddyfinder/images/%s".formatted(id), file.getInputStream(), metadata);
        putReq.withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(putReq);
        return s3.getUrl("dazzling-heisenberg", "buddyfinder/images/%s".formatted(id)).toString();
    }
}
