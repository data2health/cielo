package edu.wustl.cielo

import com.google.cloud.storage.Acl
import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import edu.wustl.cielo.enums.FileUploadType
import javax.servlet.http.Part
import grails.gorm.transactions.Transactional

@Transactional
class CloudService {
    def grailsApplication
    String BUCKET_NAME
    Storage storage

    void init() {
        BUCKET_NAME  = grailsApplication.config.getProperty("GCS.BUCKET_NAME")
        storage = StorageOptions.getDefaultInstance().getService()
    }

    /**
     * Upload a file to google cloud
     *
     * @param fileName the name of the file
     * @param filePart the file
     *
     * @return return the downloadlink for the upload
     */
    Map uploadFile(String fileName, FileUploadType type, Part filePart) {
        if (!BUCKET_NAME) init()

        // Modify access list to allow all users with link to upload file
        String subFolder = type.toString().toLowerCase()
        List<Acl> acls = new ArrayList<>()
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))

        Blob blob =
                storage.create(
                        BlobInfo.newBuilder(BUCKET_NAME, subFolder + File.separator + fileName).setAcl(acls).build(),
                        filePart.getInputStream())

        // return the public download link
        return [url: blob.getMediaLink(), blobId: blob.getBlobId()]
    }

    /**
     * Delete the uploaded file
     *
     * @param blobId the BlobId of file to delete
     *
     * @return true if successful false otherwise
     */
    boolean deleteFile(BlobId blobId) {
        if (!BUCKET_NAME) init()

        return storage.delete(blobId)
    }
}
