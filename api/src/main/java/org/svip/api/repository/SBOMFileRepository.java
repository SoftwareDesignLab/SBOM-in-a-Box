//package org.svip.api.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.svip.api.model.SBOMFile;
//
//import java.util.List;
//
///**
// * An interface extending the default JPA interface to add more specific methods in regards to data manipulation.
// *
// * @author Ian Dunn
// */
//public interface SBOMFileRepository extends JpaRepository<SBOMFile, Long> {
//    List<SBOMFile> findByFileName(String fileName);
//
//    // Methods "auto-generate" JPA optimized queries. Adding more methods (i.e. deleteByFileName) will allow you to add
//    // different queries to the database.
//}
