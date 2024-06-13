package kr.co.farmstory.service;

import kr.co.farmstory.dto.ArticleDTO;
import kr.co.farmstory.dto.FileDTO;
import kr.co.farmstory.entity.File;
import kr.co.farmstory.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final ModelMapper modelMapper;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    // 게시글에서 파일 업로드
    public List<FileDTO> fileUpload(ArticleDTO articleDTO) {

        // uploads 폴더 자동 생성
        java.io.File file = new java.io.File(fileUploadPath);
        if(!file.exists()){
            file.mkdir();
        }

        // Entity 클래스명과 곂치기 때문에 java.io.File로 표시
        String path = new java.io.File(fileUploadPath).getAbsolutePath();

        List<FileDTO> files = new ArrayList<>();
        log.info("fileUpload...1");
        for (MultipartFile mf : articleDTO.getFiles()) {
            log.info("fileUpload...2");
            // 파일 첨부 여부 확인
            if (!mf.isEmpty()) {
                log.info("fileUpload...3");
                String oName = mf.getOriginalFilename();
                log.info("fileUpload...4" + oName);
                
                // 파일 저장명 생성
                String ext = oName.substring(oName.lastIndexOf("."));
                String sName = UUID.randomUUID().toString() + ext;

                log.info("sName  : " + sName);
                try {
                    // 저장
                    mf.transferTo(new java.io.File(path, sName));

                    FileDTO fileDTO = FileDTO.builder()
                            .oName(oName)
                            .sName(sName)
                            .build();
                    files.add(fileDTO);

                } catch (IOException e) {
                    log.error("fileUpload : " + e.getMessage());
                }
            }
        }
        return files;
    }

    // 파일 DB에 insert
    public void insertFile(List<FileDTO> files, int ano){
        // 파일 각각 insert
        for(FileDTO fileDTO : files){
            fileDTO.setAno(ano);
            File file = modelMapper.map(fileDTO, File.class);

            fileRepository.save(file);
        }
    }
    // 파일 다운로드
    public ResponseEntity<?> fileDownload(int fno){

        File file =  fileRepository.findById(fno).get();
        log.info("fileDownload ....1 ");
        try {
            Path path = Paths.get(fileUploadPath + file.getSName());
            String contentType = Files.probeContentType(path);
            log.info("fileDownload ....2 ");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(file.getOName(), StandardCharsets.UTF_8).build());
            log.info("fileDownload ....3 ");
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            Resource resource = new InputStreamResource(Files.newInputStream(path));

            fileRepository.save(file);
            log.info("fileDownload ....4");
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        }catch (IOException e){
            log.error("fileDownload : " + e.getMessage());
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
    }
    // 파일 삭제
    @Transactional
    public void fileDelete(int fno){
        log.info("파일 삭제 service1 시작");
        // 삭제 전 조회
        Optional<File> Optfile = fileRepository.findById(fno);

        log.info("파일 삭제 service2 Optfile : " + Optfile);

        if(Optfile.isPresent()){
            log.info("파일 삭제 service3 Optfile이 null이 아닐때");

            int ano = Optfile.get().getAno();
            log.info("파일 삭제 service4 ano : " + ano);

            String fileName = Optfile.get().getSName();
            log.info("파일 삭제 service5 fileName : " + fileName);
            // 디비 삭제
            fileRepository.deleteById(fno);

            // uploads의 파일 삭제
            log.info("파일 삭제 service11 uploads 파일 삭제");
            java.io.File deleteFile = new java.io.File(fileUploadPath, fileName);

            if (deleteFile.delete()){
                log.info("파일 삭제 service11 파일 삭제 성공");
            }else{
                log.info("파일 삭제 service11 파일 삭제 실패");
            }
        }
        log.info("파일 삭제 service12 끝");
    }
}
