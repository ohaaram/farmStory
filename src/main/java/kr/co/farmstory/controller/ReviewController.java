package kr.co.farmstory.controller;

import kr.co.farmstory.dto.ReviewDTO;
import kr.co.farmstory.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 쓰기 - Form 데이터 받아서 처리
    @PostMapping("/review")
    public String reviewWrite(@RequestParam("thumb") MultipartFile thumb, ReviewDTO reviewDTO) throws IOException {
        log.info("reviewWrite Cont 1 : " + reviewDTO.getUid());
        log.info("reviewWrite Cont 2 : " + reviewDTO.getScore());
        log.info("reviewWrite Cont 3 : " + thumb);
        // 이미지 리사이징은 서비스에서 함
        // 리뷰 등록 service
        reviewService.insertReview(reviewDTO, thumb);
        return "redirect:/market/orderList?uid="+reviewDTO.getUid();
    }
}
