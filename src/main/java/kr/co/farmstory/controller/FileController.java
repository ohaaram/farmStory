package kr.co.farmstory.controller;

import kr.co.farmstory.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;

    // 파일 다운로드
    @GetMapping("/file/fileDownload/{fno}")
    public ResponseEntity<?> fileDownload(@PathVariable("fno") int fno) {
        log.info("fileDownload : " + fno);
        return fileService.fileDownload(fno);
    }
    // 파일 삭제
    @DeleteMapping("/fileDelete/{fno}")
    public void fileDelete(@PathVariable("fno") int fno) {
        log.info("fileDelete : " + fno);
        fileService.fileDelete(fno);
    }

}
