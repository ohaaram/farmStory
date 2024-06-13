package kr.co.farmstory.service;

import com.querydsl.core.Tuple;
import kr.co.farmstory.dto.ReviewDTO;
import kr.co.farmstory.dto.ReviewPageRequestDTO;
import kr.co.farmstory.dto.ReviewPageResponseDTO;
import kr.co.farmstory.dto.ReviewRatioDTO;
import kr.co.farmstory.entity.Product;
import kr.co.farmstory.entity.Review;
import kr.co.farmstory.repository.ProductRepository;
import kr.co.farmstory.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Value("${file.prodImg.path}")
    private String fileUploadPath;

    // 리뷰 목록 조회
    public ReviewPageResponseDTO selectReviews(int prodno, ReviewPageRequestDTO reviewPageRequestDTO){

        Pageable pageable = reviewPageRequestDTO.getPageable("no");
        log.info("selectReviews Serv ...1 ");
        // 리뷰 목록  Page 조회
        Page<Tuple> results = reviewRepository.selectReviewsAndNick(prodno, reviewPageRequestDTO, pageable);

        // Page<Tuple>을 List<ReviewDTO>로 변환
        List<ReviewDTO> reviewList = results.getContent().stream()
                .map(tuple -> {
                    // Tuple 에서 Entity GET
                    Review review = tuple.get(0, Review.class);
                    String nick = tuple.get(1, String.class);

                    // Entity -> DTO
                    ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                    reviewDTO.setNick(nick);
                    log.info("selectReviews Serv ...3 : " + reviewDTO.toString());
                    return reviewDTO;
                }).toList();
        log.info("selectReviews Serv ...4 : " + reviewList.toString());

        // List<ReviewDTO>와 Page 리턴
        int total = (int) results.getTotalElements();
        return ReviewPageResponseDTO.builder()
                .pageRequestDTO(reviewPageRequestDTO)
                .dtoList(reviewList)
                .total(total)
                .build();
    }

    // 리뷰 avg, sum, count(*) 조회 + score별 count 조회
    @Transient
    public ReviewRatioDTO selectForRatio(int prodno){

        // 리뷰 avg, sum, count(*) 조회 : 전체 데이터 기준으로 집계해야해서 group by 사용 불가 (score별 count 조회 따로 해야함)
        Tuple result = reviewRepository.selectForRatio(prodno);
        log.info("리뷰 집계 조회 ...1 : "+result);

        // JPA는 count는 long, sum은 int or long, avg는 double로 반환된다.
        long count = result.get(0, Long.class);
        double avg = 0;
        Integer sum = 0;

        // 리뷰가 하나도 없으면 에러가 발생하기 때문에 null 체크
        if(count > 0) {
            avg = result.get(1, Double.class);
            sum = result.get(2, Integer.class);
        }
        // 해당 점수를 한 번도 받지않으면 조회되지 않기 때문에 따로 선언
        int oneScore = 0;
        int twoScore = 0;
        int threeScore = 0;
        int fourScore = 0;
        int fiveScore = 0;

        // 리뷰 score별(GROUP BY score) 조회
        List<Tuple> groupResult = reviewRepository.selectScoreCount(prodno);
        for (Tuple tuple : groupResult) {
            log.info("리뷰 집계 조회 ...2 : " + tuple);
            int score = tuple.get(0, Integer.class);
            long scoreCountLong = tuple.get(1, Long.class);
            int scoreCount = (int) scoreCountLong;

            // 각 케이스에서 해당하는 변수에 값을 더해줌
            switch (score) {
                case 1:
                    oneScore += scoreCount;
                    break;
                case 2:
                    twoScore += scoreCount;
                    break;
                case 3:
                    threeScore += scoreCount;
                    break;
                case 4:
                    fourScore += scoreCount;
                    break;
                case 5:
                    fiveScore += scoreCount;
                    break;
            }
        }

        // Select 한 값 빌더로 입력
        return ReviewRatioDTO.builder()
                .count(count)
                .sum(sum)
                .avg(avg)
                .oneScore(oneScore)
                .twoScore(twoScore)
                .threeScore(threeScore)
                .fourScore(fourScore)
                .fiveScore(fiveScore)
                .build();

    }



    // 리뷰 작성 + 상품 테이블 recount up + file -> Thumbnails
    @Transient
    public String insertReview(ReviewDTO reviewDTO, MultipartFile thumb){

        log.info("리뷰 업로드 insertReview1 reviewDTO : " + reviewDTO.toString());
        log.info("리뷰 업로드 insertReview2 이미지 : " + thumb);

        String path = new java.io.File(fileUploadPath).getAbsolutePath();
        String sName = null;
        // 이미지 리사이즈 120 * 120
        if(thumb != null && !thumb.isEmpty()) {
            try {
            // oName, sName 구하기
            String oName = thumb.getOriginalFilename();
            String ext = oName.substring(oName.lastIndexOf("."));
            sName = UUID.randomUUID().toString() + ext;
            log.info("insertReview oName : " + oName);
            log.info("insertReview sName : " + sName);


                String orgPath = path + "/orgImage";
                // 원본 파일 폴더 자동 생성
                java.io.File orgFile = new java.io.File(orgPath);
                if(!orgFile.exists()){
                    orgFile.mkdir();
                }

                // 원본 파일 저장
                thumb.transferTo(new File(orgPath, sName));
                // 썸네일 생성 후 저장
                Thumbnails.of(new File(orgPath, sName)) // 원본 파일 (경로, 이름)
                        .size(120,120) // 원하는 사이즈
                        .toFile(new File(path, sName)); // 생성한 이미지 저장

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        // DTO -> Entity
        reviewDTO.setThumbnail(sName);
        Review review = modelMapper.map(reviewDTO, Review.class);

        // review insert
        reviewRepository.save(review);

        // product recount ++
        Product product = productRepository.findById(review.getProdno()).get();
        product.setRecount(product.getRecount() + 1);

        // product update
        productRepository.save(product);
        return null;
    }

}
